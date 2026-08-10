package ar.edu.utn.dds.k3003.bot;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@ConditionalOnProperty(name = "telegram.bot.enabled", havingValue = "true")
public class TelegramBotRegistrar {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotRegistrar.class);
    private final TelegramIncentivosBot telegramIncentivosBot;

    public TelegramBotRegistrar(TelegramIncentivosBot telegramIncentivosBot) {
        this.telegramIncentivosBot = telegramIncentivosBot;
    }

    @PostConstruct
    public void registrarBot() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramIncentivosBot);
            log.info("[TELEGRAM_BOT] Bot registrado correctamente username={}",
                    telegramIncentivosBot.getBotUsername());
        } catch (TelegramApiException exception) {
            throw new IllegalStateException("No se pudo registrar el bot de Telegram.", exception);
        }
    }
}
