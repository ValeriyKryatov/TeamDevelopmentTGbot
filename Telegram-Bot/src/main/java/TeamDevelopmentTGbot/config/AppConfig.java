package TeamDevelopmentTGbot.config;

import TeamDevelopmentTGbot.listener.TelegramApiException;
import TeamDevelopmentTGbot.listener.TelegramBotUpdateListener;
import ch.qos.logback.classic.Logger;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeDefault;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class AppConfig {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);

    @Bean
    public TelegramBot bot(@Value("${telegram-bot.token}") String token) {
        TelegramBot bot = new TelegramBot(token);
//        bot.execute(new DeleteMyCommands());
        return bot;
    }
}
