package main;

import lombok.extern.slf4j.Slf4j;
import main.commands.*;
import main.frontend.BotCommands;
import main.frontend.Buttons;
import main.lib.Store;
import main.services.DataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import main.frontend.Frontend;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Component
public class MyTelegramBot extends TelegramLongPollingBot implements BotCommands {

    @Value("${telegram.bot.name}")
    private String botUsername;


    private Map<String, BaseCommand> commands=Map.of(
            "/start", new StartCommand(),
            "/add", new AddCommand(),
            "/help", new HelpCommand(),
            "/info", new InfoCommand()
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
            log.info(message.getChat().getFirstName() + " написал " + update.getMessage().getText());
            switch (message.getText()) {
                case "/start" -> {
                    StartCommand command= (StartCommand) commands.get("/start");
                    command.execute(dataService, update.getMessage().getChatId(), update.getMessage().getFrom().getId(), "");
                    replyToMessage("Привет, "+message.getChat().getFirstName());
                }
                case "/add" -> {
                    AddCommand command= (AddCommand) commands.get("/add");
                    command.execute(dataService, update.getMessage().getChatId(), update.getMessage().getFrom().getId(), "");
                }
                case "/info" -> {
                    InfoCommand command=(InfoCommand) commands.get("/info");
                    command.execute(dataService, update.getMessage().getChatId(), update.getMessage().getFrom().getId(), "");
                }
                case "/help" -> {
                    HelpCommand command=(HelpCommand) commands.get("/help");
                    command.execute(dataService, update.getMessage().getChatId(), update.getMessage().getFrom().getId(), "");

                }
                default -> {
                    switch (Store.getCondition(update.getMessage().getFrom().getId())){
                        case BASE -> {
                            replyToMessage("Это дефолт! Брейк!");
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
                Store.addToSendQueue(chatId, userName);
                StartCommand StartCommand= (StartCommand) commands.get("/start");
                StartCommand.execute(dataService, chatId, userId, "");
                break;
            case "/help":
                HelpCommand helpCommand= (HelpCommand) commands.get("/help");
                helpCommand.execute(dataService, chatId, userId, "");
                break;
            case "/add":
                AddCommand addCommand= (AddCommand) commands.get("/add");
                addCommand.execute(dataService, chatId, userId, "");
                break;
            case "/info":
                InfoCommand infoCommand=(InfoCommand) commands.get("/info");
                infoCommand.execute(dataService, chatId, userId, "");
            default: break;
        }
    }


    private void deleteInlineKeyboard(Update update){
        DeleteMessage deleteMessage=new DeleteMessage();
        deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        Store.addToSendQueue(update.getCallbackQuery().getMessage().getChatId(), deleteMessage);
    }


    public void replyToMessage(String text){
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        Store.addToSendQueue(message.getChatId(), sendMessage);
    }


    @Deprecated
    public void showKeyboard() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        String[][] testKeys={{"Команда 1" , "Команда 2"}, {"Команда 3", "Команда 4"}};
        ReplyKeyboardMarkup replyKeyboardMarkup = Frontend.drawKeyboard(testKeys);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

    }

    //Отправитель сообщений
    private void sendMessage(){
        new Thread(() -> {
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            while (true){
                try {
                    Pair<Long, Object> sendPair= Store.queueToSend.take();
                    executorService.execute(()->{
                        Object o = sendPair.getSecond();
                        if(o.getClass()==SendMessage.class){
                            SendMessage NewsendMessage= (SendMessage) sendPair.getSecond();
                            NewsendMessage.setChatId(sendPair.getFirst());
                            try {
                                execute(NewsendMessage);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else if(o.getClass()==SendPhoto.class){
                            SendPhoto NewsendPhoto = (SendPhoto) sendPair.getSecond();
                            NewsendPhoto.setChatId(sendPair.getFirst());
                            try {
                                execute(NewsendPhoto);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else if(o.getClass()==DeleteMessage.class){
                            DeleteMessage deleteMessage= (DeleteMessage) sendPair.getSecond();
                            deleteMessage.setChatId(sendPair.getFirst());
                            try {
                                execute(deleteMessage);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
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
