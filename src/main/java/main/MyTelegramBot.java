package main;

import lombok.extern.slf4j.Slf4j;
import main.commands.AddCommand;
import main.commands.BaseCommand;
import main.commands.HelpCommand;
import main.frontend.BotCommands;
import main.frontend.Buttons;
import main.services.DataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import main.frontend.Frontend;

import java.util.Map;


@Slf4j
@Component
public class MyTelegramBot extends TelegramLongPollingBot implements BotCommands {

    private final DataService dataService;

    private SendMessage sendMessage;
    private Message message;

    public MyTelegramBot(DataService data, @Value("${telegram.bot.token}") String botToken) {
        super(botToken);
        this.dataService=data;
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ignored){
        }

    }

    @Value("${telegram.bot.name}")
    private String botUsername;


    private Map<String, BaseCommand> commands=Map.of(
            "/add", new AddCommand(),
            "/help", new HelpCommand()
    );




    @Override
    public void onUpdateReceived(Update update) {
        long chatId = 0;
        long userId = 0; //это нам понадобится позже
        String userName = null;
        String receivedMessage;
        sendMessage=new SendMessage();
        message = update.getMessage();
        // Обработка входящего сообщения
        if (update.hasMessage() && update.getMessage().hasText()) {
            switch (message.getText()) {
                case "/start" -> {
                    replyToMessage("Это команда старт!");
                    //showKeyboard(message, "pivo");
                    startBot(message.getChatId(), "poma12390");
                    System.out.println(message.getText());
                }
                case "/add" -> {
                    replyToMessage("Это test add!");
                    showKeyboard("trying");
                    AddCommand command= (AddCommand) commands.get("/add");
                    command.execute(dataService);
                    System.out.println(message.getText());
                }
                case "Команда 1" -> {
                    replyToMessage("Это команда 1");
                    System.out.println(Frontend.selectDate(message));
                }
                case "Команда 2" -> {
                    replyToMessage("Это команда 2");
                    System.out.println(message.getText());
                }
                default -> {
                    replyToMessage("Это дефолт! Брейк!");
                    System.out.println(message.getText());
                }
            }
        }else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            receivedMessage = update.getCallbackQuery().getData();

            botAnswerUtils(receivedMessage, chatId, userName);
        }
    }
    private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
        switch (receivedMessage){
            case "/start":
                startBot(chatId, userName);
                break;
            case "/help":
                sendHelpText(chatId, HELP_TEXT);
                break;
            default: break;
        }
    }
    private void sendHelpText(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hi, " + userName + "! I'm a Telegram bot.'");
        message.setReplyMarkup(Buttons.inlineMarkup());

        try {
            execute(message);

        } catch (TelegramApiException ignored){

        }
    }

    //отправить текст
    public void sendText(String text){
        String chatId = message.getChatId().toString();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void replyToMessage(String text){
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    public void showKeyboard(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        String[][] testKeys={{"Команда 1" , "Команда 2"}, {"Команда 3", "Команда 4"}};
        ReplyKeyboardMarkup replyKeyboardMarkup = Frontend.drawKeyboard(testKeys);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendText(text);

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }


}
