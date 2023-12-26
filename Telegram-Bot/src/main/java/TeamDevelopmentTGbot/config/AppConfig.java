package TeamDevelopmentTGbot.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// этот класс, создан для спринга, что бы телеграмм бот стал бином и спринг мог к нему обращаться
@Configuration
public class AppConfig {
    @Bean
    public TelegramBot bot(@Value("${telegram-bot.token}") String token) {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }
}
