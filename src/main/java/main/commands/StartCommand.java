package main.commands;

import main.frontend.Buttons;
import main.lib.Store;
import main.services.DataService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class StartCommand  extends BaseCommand{
    @Override
    public void execute(DataService dataService, Long chatId, Long userId, String text) {
        SendMessage message = new SendMessage();
        message.setText("Выберите команду");
        message.setReplyMarkup(Buttons.inlineMarkup());
        Store.addToSendQueue(chatId, message);
    }
}
