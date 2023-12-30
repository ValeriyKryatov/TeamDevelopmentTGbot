package TeamDevelopmentTGbot.listener;

import TeamDevelopmentTGbot.model.NotificationTask;
import TeamDevelopmentTGbot.repository.NotificationRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdateListener implements UpdatesListener {
    private final static Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);

    private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");
    private static final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private final TelegramBot bot;
    private final NotificationRepository repository;

    public TelegramBotUpdateListener(TelegramBot bot, NotificationRepository repository) {
        this.bot = bot;
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        bot.setUpdatesListener(this);
    }
    private final Map<Long, Boolean> userAlreadyInteracted = new HashMap<>(); // Карта для отслеживания взаимодействия с пользователями

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            var text = update.message().text();
            var chatId = update.message().chat().id();

            if (userAlreadyInteracted.containsKey(chatId)) {
                // Пользователь уже обращался
                if (text.equalsIgnoreCase("/start")) {
                    // Если пользователь выбирает меню
                    sendMessage(chatId, "Выберите опцию из меню...");
                } else {
                    // Обрабатываем запросы пользователя
                    sendMessage(chatId, "Вы выбрали: " + text);
                }
            } else {
                // Пользователь обращается в первый раз
                userAlreadyInteracted.put(chatId, true);
                sendMessage(chatId, "Привет! Это ваше первое обращение. Напишите /menu для доступа к меню.");
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    private void sendMessage(long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }


}