package main;

import lombok.extern.slf4j.Slf4j;
import main.commands.AddCommand;
import main.commands.BaseCommand;
import main.commands.HelpCommand;
import main.frontend.BotCommands;
import main.frontend.Buttons;
import main.lib.Store;
import main.services.DataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import main.frontend.Frontend;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Component
public class MyTelegramBot extends TelegramLongPollingBot implements BotCommands {

    @Value("${telegram.bot.name}")
    private String botUsername;


    private Map<String, BaseCommand> commands=Map.of(
            "/add", new AddCommand(),
            "/help", new HelpCommand()
    );

    private final DataService dataService;

    private volatile SendMessage sendMessage;
    private volatile Message message;

    public MyTelegramBot(DataService data, @Value("${telegram.bot.token}") String botToken) {
        super(botToken);
        this.dataService=data;
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ignored){
        }

    }


    @Override
    public void onUpdateReceived(Update update) {
        sendMessage();
        long chatId = 0;
        long userId = 0; //это нам понадобится позже
        String userName = null;
        String receivedMessage;
        sendMessage=new SendMessage();
        message = update.getMessage();
        if(update.hasMessage()){//Если новый юзер, создаем нового юзера с базовым состоянием
            if(!Store.userExist(update.getMessage().getFrom().getId())){
                Store.newUser(update.getMessage().getFrom().getId());
            }
        } else if (update.hasCallbackQuery()) {
            if(!Store.userExist(update.getCallbackQuery().getFrom().getId())){
                Store.newUser(update.getCallbackQuery().getFrom().getId());
            }
        }
        // Обработка входящего сообщения
        if (update.hasMessage() && update.getMessage().hasText()) {
            switch (message.getText()) {
                case "/start" -> {
                    replyToMessage("Привет, "+message.getChat().getFirstName());
                    startBot(message.getChatId());
                }
                case "/add" -> {
                    showKeyboard();
                    AddCommand command= (AddCommand) commands.get("/add");
                    command.execute(dataService, update.getMessage().getChatId(), update.getMessage().getFrom().getId(), "");
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
                    switch (Store.getCondition(update.getMessage().getFrom().getId())){
                        case BASE -> {
                            replyToMessage("Это дефолт! Брейк!");
                            System.out.println(message.getText());
                        }
                        case INPUTNAME -> {
                            AddCommand command= (AddCommand) commands.get("/add");
                            command.execute(dataService, update.getMessage().getChatId(), update.getMessage().getFrom().getId(), update.getMessage().getText());
                        }case INPUTSALARY -> {
                            AddCommand command= (AddCommand) commands.get("/add");
                            command.execute(dataService, update.getMessage().getChatId(), update.getMessage().getFrom().getId(), update.getMessage().getText());
                        }
                    }

                }
            }
            //Обработка inline клавиатуры
        }else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            receivedMessage = update.getCallbackQuery().getData();
            deleteInlineKeyboard(update);
            botAnswerUtils(receivedMessage, chatId, userName, userId);
        }
    }
    private void botAnswerUtils(String receivedMessage, long chatId, String userName, long userId) {
        switch (receivedMessage){
            case "/start":
                Store.queueToSend.add(Pair.of(chatId, userName));

                startBot(chatId);
                break;
            case "/help":
                Store.queueToSend.add(Pair.of(chatId, HELP_TEXT));
                break;
            case "/add":
                AddCommand command= (AddCommand) commands.get("/add");
                command.execute(dataService, chatId, userId, "");
            default: break;
        }
    }



    private void startBot(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите команду");
        message.setReplyMarkup(Buttons.inlineMarkup());

        try {
            execute(message);

        } catch (TelegramApiException ignored){

        }
    }

    private void updateInlineKeyboard(Update update){
        EditMessageText editMessageText=new EditMessageText();

        editMessageText.setText("Выберите команду");
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
        editMessageText.setReplyMarkup(null);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteInlineKeyboard(Update update){
        DeleteMessage deleteMessage=new DeleteMessage();
        deleteMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
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



    public void showKeyboard() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        String[][] testKeys={{"Команда 1" , "Команда 2"}, {"Команда 3", "Команда 4"}};
        ReplyKeyboardMarkup replyKeyboardMarkup = Frontend.drawKeyboard(testKeys);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

    }


    private void sendMessage(){
        new Thread(() -> {
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            while (true){
                try {
                    Pair<Long, String> sendPair= Store.queueToSend.take();
                    executorService.execute(()->{
                        SendMessage NewsendMessage=new SendMessage();
                        NewsendMessage.setChatId(sendPair.getFirst());
                        NewsendMessage.setText(sendPair.getSecond());
                        try {
                            execute(NewsendMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (InterruptedException e) {
                    executorService.shutdown();
                    break;
                }catch (RuntimeException e){
                    e.printStackTrace();
                    log.error("Send message error");
                }
            }
        }).start();
    }


    @Override
    public String getBotUsername() {
        return botUsername;
    }


}
