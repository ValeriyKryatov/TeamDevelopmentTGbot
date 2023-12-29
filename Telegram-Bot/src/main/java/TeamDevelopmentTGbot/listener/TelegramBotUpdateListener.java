package TeamDevelopmentTGbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import TeamDevelopmentTGbot.model.NotificationTask;
import TeamDevelopmentTGbot.repository.NotificationRepository;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdateListener implements UpdatesListener {
    private final static Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);

    private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");
    private static final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private final TelegramBot bot;
    private final NotificationRepository repository;
    private HermitageInlineKeyboardAb hermitageInlineKeyboardAb;


    public TelegramBotUpdateListener(TelegramBot bot,
                                       NotificationRepository repository,
                                         HermitageInlineKeyboardAb hermitageInlineKeyboardAb) {
        this.bot = bot;
        this.repository = repository;
        this.hermitageInlineKeyboardAb = hermitageInlineKeyboardAb;
    }

//    public TelegramBotUpdateListener(TelegramBot bot) {
//        this.bot = bot;
//    }

    @PostConstruct
    public void init() {
        bot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        String text = "";
        long  chatId = 0;
          for (Update update : updates) {
            try {
                text = update.message().text();
                chatId = update.message().chat().id();


            } catch (NullPointerException e){
                e.getStackTrace();
                logger.error("Cannot NullPointerException: {}");
            }
              System.out.println("  Этот текст, пришел от бота ==> " + text);
            if ("/start".equals(text)) {
                sendMessage(chatId, "Добро пожаловать в бот!");

                //то отправляем пользователю нужную встроенную клавиатуру
//                getSendMessageInlineKeyboard(chatId);

            } else if ("/menu".equals(text)){
                   SendMessage response = new SendMessage(chatId, "Меню")
                           .replyMarkup(new ReplyKeyboardMarkup(new String[][] {
                                    {"Приют"},
                                    {"Поддержка"}
                            }).resizeKeyboard(true));
                    bot.execute(response);
//                continue;
               } else  if ( "/shelter".equals(text) ){
                   SendMessage response = new SendMessage(chatId, "Меню1")
                        .replyMarkup(new ReplyKeyboardMarkup(new String[][] {
                                {"Приют1"},
                                {"Поддержка1"}
                        }).resizeKeyboard(true));
                  bot.execute(response);
                  bot.execute(new SendMessage(chatId, "Вы находитесь в разделе Приют "));
                  continue;
              } else  if ("/volunteers".equals(text)){
                SendMessage response = new SendMessage(chatId, "Меню2")
                        .replyMarkup(new ReplyKeyboardMarkup(new String[][] {
                                {"Приют2"},
                                {"Поддержка2"}
                        }).resizeKeyboard(true));
                bot.execute(response);
                bot.execute(new SendMessage(chatId, "Вы находитесь в разделе Поддержка "));
//                continue;
            } else  {
                // 01-01-202220.00 Сделать домашнюю работу
                var matcher = PATTERN.matcher(text);
                if (matcher.matches()) {
                    LocalDateTime dateTime = parseTime(matcher.group(1));
                    if (dateTime == null) {
                        bot.execute(new SendMessage(chatId, "Формат даты указан неверно!"));
                        continue;
                    }
                    var taskText = matcher.group(3);
                    NotificationTask task = new NotificationTask();
                    task.setChatId(chatId);
                    task.setText(taskText);
                    task.setDateTime(dateTime);
                    NotificationTask saved = repository.save(task);
                    sendMessage(chatId, "Задача запланирована!");
                    logger.info("Notification task saved: {}", saved);
                }
                else {
                    sendMessage(chatId, "ВЫ не в правильном формате задаете задание !\n"
                                             + "27.12.2023 03:34 Это Верный Формат!");
                   }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private LocalDateTime parseTime(String text) {
        try {
            return LocalDateTime.parse(text, DATE_TIME_PATTERN);
        } catch (DateTimeParseException e) {
            logger.error("Cannot parse date and time: {}", text);
        }
        return null;
    }

//    private SendMessage setInline(Long  chatId, String message ) {
//        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
//        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
//        buttons1.add(new InlineKeyboardButton("Кнопка1").callbackData("17"));
//        buttons1.add(new InlineKeyboardButton("Кнопка2").callbackData("18"));
//        buttons1.add(new InlineKeyboardButton("Кнопка3").callbackData("19"));
//        buttons.add(buttons1);
//        var arr = buttons.toArray();
//        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup((InlineKeyboardButton[]) arr);
//
//       return new SendMessage(chatId,message).replyMarkup(markupKeyboard);
//    }
    // альтернативный пример
    private void sendMessage(long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }

//    private void setInline() {
//        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
//                List<InlineKeyboardButton> buttons1 = new ArrayList<>();
//        buttons1.add(new InlineKeyboardButton().setText(“Кнопка“).setCallbackData(17));
//        buttons.add(buttons1);
//
//        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
//        markupKeyboard.setKeyboard(buttons);
//    }
}
