package org.app.carsharingapp.bot;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.app.carsharingapp.dto.user.UserLoginRequestDto;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.exception.TelegramMessageException;
import org.app.carsharingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class NotificationBot extends TelegramLongPollingBot {
    private static final String START_COMMAND = "/start";
    private static final String AUTH_COMMAND = "/auth";
    private static final String CANCEL_COMMAND = "/cancel";
    private static final String START_MESSAGE = "Hello, I'm car pooling bot use /auth command "
            + "if you haven't done it yet to receive notification "
            + "or /cancel to stop receive messages";
    private static final String CANT_SEND_MESSAGE = "Can't send message to user chatId: %d";
    private static final String CANT_SEND_LOG_MESSAGE = "Can't send logs to chat with text: %s";
    private final String botName;
    private final UserRepository userRepository;
    private UserLoginRequestDto loginRequestDto;
    private final AuthenticationManager authenticationManager;
    private final Map<Long, AuthState> authStates = new HashMap<>();
    private final Map<Long, UserLoginRequestDto> loginData = new HashMap<>();

    private enum AuthState {
        STARTED,
        LOGIN,
        PASSWORD
    }

    public NotificationBot(@Value("${telegram.bot.token}") String botToken,
                           @Value("${telegram.bot.name}") String botName,
                           UserRepository userRepository,
                           AuthenticationManager authenticationManager) {
        super(botToken);
        this.botName = botName;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        AuthState currentState = authStates.getOrDefault(chatId, AuthState.STARTED);
        String receivedText = update.getMessage().getText();
        if (receivedText.equals(START_COMMAND)) {
            handleStartCommand(update);
        } else if (authStates.containsKey(chatId)) {
            authProcess(currentState, chatId, update);
        } else if (receivedText.equals(AUTH_COMMAND)) {
            authStates.put(chatId, AuthState.LOGIN);
            authProcess(currentState, chatId, update);
        } else if (receivedText.equals(CANCEL_COMMAND)) {
            User user = userRepository.findByChatId(chatId).orElseThrow(
                    () -> new EntityNotFoundException("Can't find user by chat id")
            );
            user.setChatId(null);
            userRepository.save(user);
            sendTextMessage(chatId, "Notification canceled successful");
        } else {
            sendTextMessage(chatId, START_MESSAGE);
        }
    }

    public void sendMessage(User user, String text) {
        Optional<Long> chatIdOpt = Optional.ofNullable(user.getChatId());
        if (chatIdOpt.isPresent()) {
            Long chatId = chatIdOpt.get();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(text);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new TelegramMessageException(String.format(CANT_SEND_LOG_MESSAGE, text), e);
            }
        }
    }

    private void handleStartCommand(Update update) {
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setText(START_MESSAGE);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new TelegramMessageException(String.format(CANT_SEND_MESSAGE, chatId), e);
        }
    }

    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new TelegramMessageException(String.format(CANT_SEND_MESSAGE, chatId), e);
        }
    }

    private void authProcess(AuthState currentState, Long chatId, Update update) {
        String text = update.getMessage().getText();
        switch (currentState) {
            case STARTED: {
                sendTextMessage(chatId, "Please enter your login:");
                authStates.put(chatId, AuthState.LOGIN);
                break;
            }
            case LOGIN: {
                loginRequestDto = loginData.getOrDefault(chatId, new UserLoginRequestDto());
                loginRequestDto.setEmail(text);
                sendTextMessage(chatId, "Password:");
                loginData.put(chatId, loginRequestDto);
                authStates.put(chatId, AuthState.PASSWORD);
                break;
            }
            case PASSWORD: {
                loginRequestDto = loginData.getOrDefault(chatId, new UserLoginRequestDto());
                loginRequestDto.setPassword(text);
                deletePreviousMessage(chatId, update);
                try {
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(), loginRequestDto.getPassword()
                    ));
                    User user = userRepository.findByEmail(
                            loginRequestDto.getEmail())
                            .orElseThrow(() ->
                                    new EntityNotFoundException("Can't find user with email: "
                                    + loginRequestDto.getEmail()));
                    user.setChatId(chatId);
                    userRepository.save(user);
                    sendTextMessage(chatId, "Successful login ");
                } catch (Exception e) {
                    sendTextMessage(chatId, "Auth failed");
                } finally {
                    authStates.remove(chatId);
                    loginRequestDto.setPassword("");
                }
                break;
            }
            default: {
                authStates.put(chatId, AuthState.STARTED);
                break;
            }
        }
    }

    private void deletePreviousMessage(Long chatId, Update update) {
        Integer messageId = update.getMessage().getMessageId();
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new TelegramMessageException("Delete message", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}
