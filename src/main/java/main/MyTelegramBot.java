package main;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import main.frontend.Frontend;



@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    public MyTelegramBot(@Value("${telegram.bot.token}") String botToken) {
        super(botToken);
    }

    @Value("${telegram.bot.name}")
    private String botUsername;



    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        // Обработка входящего сообщения
        if (update.hasMessage() && update.getMessage().hasText()) {
            switch (message.getText()) {
                case "/start" -> {
                    replyToMessage(new SendMessage(), message, "Это команда старт!");
                    showKeyboard(message, "pivo");
                    System.out.println(message.getText());
                }
                case "Команда 1" -> {
                    replyToMessage(new SendMessage(), message, "Это команда 1");
                    System.out.println(Frontend.selectDate(message));
                }
                case "Команда 2" -> {
                    replyToMessage(new SendMessage(), message, "Это команда 2");
                    System.out.println(message.getText());
                }
                default -> {
                    replyToMessage(new SendMessage(), message, "Это дефолт! Брейк!");
                    System.out.println(message.getText());
                }
            }
        }
    }
    //отправить текст
    public void sendText(SendMessage sendMessage, Message message, String text){
        String chatId = message.getChatId().toString();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void replyToMessage(SendMessage sendMessage, Message message, String text){
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    public void showKeyboard(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        String[][] testKeys={{"Команда 1" , "Команда 2"}, {"Команда 3", "Команда 4"}};
        ReplyKeyboardMarkup replyKeyboardMarkup = Frontend.drawKeyboard(testKeys);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendText(sendMessage, message, text);

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }


}
