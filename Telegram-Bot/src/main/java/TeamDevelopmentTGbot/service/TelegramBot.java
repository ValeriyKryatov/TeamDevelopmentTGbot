package TeamDevelopmentTGbot.service;

import TeamDevelopmentTGbot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
//        кнопка меню в углу сообщений
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/m1", "вызов клавиатуры в сообщении"));
        listofCommands.add(new BotCommand("/m2", "вызов стационарной клавиатуры"));
        listofCommands.add(new BotCommand("/m3", ".... в разработке "));
         try {
            this.execute(new SetMyCommands(listofCommands,
                    new BotCommandScopeDefault(),
                    null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    //    метод, который принимает объекты с телеграмм бота и в зависимости
//    от их свойств соотвествующим способом обрабатывает их
    @Override
    public void onUpdateReceived(Update update) {
        String messageText;
        if (update.hasMessage() && update.getMessage().hasText()) {
            messageText = update.getMessage().getText();
            System.out.println("  Этот текст, пришел от бота в Message() ==> " + messageText);
            actionSelectorFromUpdate(messageText,update);
        } else if (update.hasCallbackQuery()){
            messageText = update.getCallbackQuery().getData();
            System.out.println("  Этот текст, пришел от бота в CallbackQuery ==> " + messageText);
            actionSelectorFromUpdate(messageText,update);
        }
    }

    //    метод обработки входящих сообщений с бота и в зависимости от него включает нужный метод
    private void actionSelectorFromUpdate (String text, Update update){
        long chatId = update.getMessage().getChatId();
        switch (text) {
            case "/start":
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                // обработчик при этом условии
            break;
            case "/m1": {
                // вызов клавиатуры привязанной к сообщению в чате
                sendMessage(eInlineKeyboardAb(chatId));
                // обработчик при этом условии
            }
            break;
            case "/m2": {
                // вызов клавиатуры которая находится под чатом
                sendMessage(eReplyKeyboardAb(chatId));
                // обработчик при этом условии
            }
            break;
            default:
                // вызов метода, всегда выполняется, данный default: можно удалить
                sendMessage(chatId, "Sorry, command was not recognized");
                // обработчик при этом условии
        }
    }

    //   приветствие при запуске бота
    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);

    }

    //    вывод текстового сообщения в телеграмм
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            this.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    //    вывод различных видов клавиатур в телеграмм бот
    private void sendMessage (SendMessage sendMessage){
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    //    клавиатура привязанная к сообщению - прототип
    public static SendMessage eInlineKeyboardAb(long chat_id) {

        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText("Клавиатура привязанная к сообщению ");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Клавиша 1");
        inlineKeyboardButton1.setCallbackData("/kl1");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Клавиша 2");
        inlineKeyboardButton2.setCallbackData("/kl2");
        rowInline1.add(inlineKeyboardButton1);
        rowInline1.add(inlineKeyboardButton2);

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Клавиша 3");
        inlineKeyboardButton3.setCallbackData("/kl3");
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText("Клавиша 4");
        inlineKeyboardButton4.setCallbackData("/kl4");
        rowInline2.add(inlineKeyboardButton3);
        rowInline2.add(inlineKeyboardButton4);

//      ...... иные необходимые ряды кнопок по вышеуказанной аналогии

        List<InlineKeyboardButton> rowInline11 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton21 = new InlineKeyboardButton();
        inlineKeyboardButton21.setText("Переход на внешний сайт");
        inlineKeyboardButton21.setUrl("https://collections.hermitagemuseum.org");
        inlineKeyboardButton21.setCallbackData("/url1");
        rowInline11.add(inlineKeyboardButton21);

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
//     ...... набор строк в комбинации
        rowsInline.add(rowInline11);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        return message;
    }

    //    клавиатура, находящаяся под сообщениями - прототип
    public static SendMessage eReplyKeyboardAb(long chat_id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Команда 1");
        keyboardFirstRow.add("Команда 2");

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add("Команда 3");
        keyboardSecondRow.add("Команда 4");

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendMessage.setChatId(chat_id);
        sendMessage.setReplyToMessageId(sendMessage.getReplyToMessageId());
        sendMessage.setText("клавиатура1");
        return sendMessage;
    }
}



