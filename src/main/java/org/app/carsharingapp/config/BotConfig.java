package org.app.carsharingapp.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.bot.NotificationBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class BotConfig {
    private final NotificationBot notificationBot;

    @PostConstruct
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(notificationBot);
    }
}
