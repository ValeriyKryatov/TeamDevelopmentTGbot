package TeamDevelopmentTGbot.listener;

import TeamDevelopmentTGbot.model.NotificationTask;
import TeamDevelopmentTGbot.repository.NotificationRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdateListener implements UpdatesListener {
    private final static Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);
    private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");
    private static final Pattern PATTERN = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+]+)");

//   для отправки сообщений в бот нам нужно создать самого бота
    private final TelegramBot bot;
    private final NotificationRepository repository;

    public TelegramBotUpdateListener(TelegramBot bot, NotificationRepository repository) {
        this.bot = bot;
        this.repository = repository;
    }

    @Override
    public int process(List<Update> updates) {
// updates - это список событий, которые приходят к телеграм боту
     for(Update update: updates){ // <-- проходимся циклом по всему листу событий телеграмм бота
          var text = update.message().text(); // <-- получаем текстовое сообщение из конкретного события
         System.out.println("получаем сообщение от бота --> "+ text);
          var chatId = update.message().chat().id(); // <-- идентификатор, чата в который отправляется сообщение
            if ("/start".equals(text)) {
                // отправка сообщения в бот нужный чат, обратно
                sendMessage(chatId, "Привет! Это планинг-бот:)! " +
                                            "Для планирования задачи отправьте её в формате: " +
                                              "01.01.2022 20:00 Сделать домашнюю работу");
             } else {
                // сообщение следующего вида --> 07.12.2023 22:15 Передать всем привет!
                // парсим (разделяем) данное сообщение на куски с помощью паттерна -> PATTERN
                // обрабатываем через регулярный паттерн -> PATTERN наш принятый текст сообщения -> text
                var matcher = PATTERN.matcher(text);
                // если сообщение похоже, на то что обработал паттерн то (matcher.matches() -> boolean)
                if (matcher.matches()) {
                  // теперь разбиваем сообщения на части - группы, группы берутся из паттерна
                  // String dateTime = matcher.group(1);
                  // приводим строку в формат времени используя паттерн формата времени -> DATE_TIME_PATTERN
                  // LocalDateTime.parse(dateTime,DATE_TIME_PATTERN); -> выделяем это в метод parseTime()
                    LocalDateTime dateTime = parseTime(matcher.group(1));
                    // если не получилось распарсить написанную строку с датой и временем, то у нас будет -> null
                    if (dateTime == null) {
                        bot.execute(new SendMessage(chatId, "Формат даты указан неверно!"));
                        continue;
                    }
                    // получаем текст с 3-й группы паттерна PATTERN
                    var taskText = matcher.group(3);
                    // заполняем нашу модель NotificationTask данными создавая от неё объект -> task
                    NotificationTask task = new NotificationTask();
                    task.setChatId(chatId);
                    task.setText(taskText);
                    task.setDateTime(dateTime);
                    // сохраняем наш созданный и заполненный объект task в репозиторий
                    NotificationTask saved = repository.save(task);
                    // применяем метод вывода сообщения в бот телеграмм
                    sendMessage(chatId, "Задача запланирована!");
                    logger.info("Notification task saved: {}", saved);
                }
            }
     }
     //  обработка всех обновлений прошла удачно, это для телеграммы
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    // альтернативный способ вывода сообщения в телеграмм
    private void sendMessage(long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }
// метод разпарсивает формат времени и если ошибка то возвращает -> null
    private LocalDateTime parseTime(String text) {
        try {
       // приводим строку в формат времени используя паттерн формата времени -> DATE_TIME_PATTERN
            return LocalDateTime.parse(text, DATE_TIME_PATTERN);
        } catch (DateTimeParseException e) {
            e.getStackTrace();
            logger.error("Cannot parse date and time: {}", text);
        }
        return null;
    }
}

