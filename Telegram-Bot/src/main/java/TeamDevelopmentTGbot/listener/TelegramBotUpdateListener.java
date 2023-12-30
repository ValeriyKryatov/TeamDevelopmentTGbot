package TeamDevelopmentTGbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import TeamDevelopmentTGbot.model.NotificationTask;
import TeamDevelopmentTGbot.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;


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
                System.out.println(update.toString());

            } catch (NullPointerException e){
                e.getStackTrace();
                logger.error("Cannot NullPointerException: {}");
            }
              System.out.println("  Этот текст, пришел от бота ==> " + text);

            if ("/start".equals(text)) {
                sendMessage(chatId, "Добро пожаловать в бот!");

            } else if ("/menu".equals(text)){
                   SendMessage response = new SendMessage(chatId, "Меню")
                           .replyMarkup(new ReplyKeyboardMarkup(new String[][] {
                                    {"Приют", "Старт"},
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
//**************************************************************************************
                Keyboard keyboard = new ReplyKeyboardMarkup(
                        new KeyboardButton[]{
                                new KeyboardButton("text Обо мне "),
                                new KeyboardButton("contact").requestContact(true),
                                new KeyboardButton("location").requestLocation(true)
                        }
                );
                SendMessage response1 = new SendMessage(chatId, "Меню2")
                        .replyMarkup(keyboard);

                bot.execute(response1);
                bot.execute(new SendMessage(chatId, "Вы находитесь в разделе Приют "));
                continue;
//****************************************************************************************
            } else  if ("/volunteers".equals(text)){

//                SendMessage response = new SendMessage(chatId, "Меню2")
//                        .replyMarkup(
//                                new InlineKeyboardMarkup(
//                                new InlineKeyboardButton[][]{
//                                        {       new InlineKeyboardButton("url").url("www.google.com"),
//                                                new InlineKeyboardButton("callback_data").callbackData("callback_data111"),
//                                                new InlineKeyboardButton("Switch!").switchInlineQuery("switch_inline_query")},
//                                        {       new InlineKeyboardButton("url1").url("www.google.com"),
//                                                new InlineKeyboardButton("callback_data").callbackData("callback_data222"),
//                                                new InlineKeyboardButton("Switch!1").switchInlineQuery("switch_inline_query11")},
//                                           }));

                          StringBuilder stringBuilder = new StringBuilder("callback ok");
                                InlineKeyboardMarkup inlineKeybMark = new InlineKeyboardMarkup();
                         inlineKeybMark.addRow (new InlineKeyboardButton[]{
//                        new InlineKeyboardButton("inline game").callbackGame("pengrad test game description"),
                        new InlineKeyboardButton("inline ok").callbackData(stringBuilder.toString()),
                        new InlineKeyboardButton("cancel").switchInlineQuery("callback cancel"),
                        new InlineKeyboardButton("url").url("www.google.com"),
                        new InlineKeyboardButton("switch inline").switchInlineQuery("query"),
                        new InlineKeyboardButton("switch inline current").switchInlineQueryCurrentChat("query")
                });
                SendMessage response = new SendMessage(chatId, "Меню3")
                        .replyMarkup(inlineKeybMark);

//                var next = InlineKeyboardButton.builder()
//                        .text("Next").callbackData("next")
//                        .build();

                bot.execute(response);

                SendMessage response2 = new SendMessage(chatId, "Меню2")
                        .replyMarkup(
                                new InlineKeyboardMarkup(
                                        new  InlineKeyboardButton("callback_data").callbackData("callback_data111")
                                        )
                );
                System.out.println(" Распечатываем response --> " + response.toString());
                bot.execute(response2);
//                bot.execute(new SendMessage(chatId,  " Это сообщение о нажатии кнопки callback_data ->   " + iKeyboardButtonnew.text()));
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
//    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
//        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
//        inlineKeyboardButton1.setText("Тык");
//        inlineKeyboardButton1.setCallbackData("Button \"Тык\" has been pressed");
//        inlineKeyboardButton2.setText("Тык2");
//        inlineKeyboardButton2.setCallbackData("Button \"Тык2\" has been pressed");
//        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
//        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
//        keyboardButtonsRow1.add(inlineKeyboardButton1);
//        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Fi4a").setCallbackData("CallFi4a"));
//        keyboardButtonsRow2.add(inlineKeyboardButton2);
//        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//        rowList.add(keyboardButtonsRow1);
//        rowList.add(keyboardButtonsRow2);
//        inlineKeyboardMarkup.setKeyboard(rowList);
//        return new SendMessage().setChatId(chatId).setText("Пример").setReplyMarkup(inlineKeyboardMarkup);
//    }

    private LocalDateTime parseTime(String text) {
        try {
            return LocalDateTime.parse(text, DATE_TIME_PATTERN);
        } catch (DateTimeParseException e) {
            logger.error("Cannot parse date and time: {}", text);
        }
        return null;
    }

    // альтернативный пример
    private void sendMessage(long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }
}
