package main.commands;

import main.lib.Store;
import main.services.DataService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


public class InfoCommand extends BaseCommand{
    @Override
    public void execute(DataService dataService, Long chatId, Long userId, String text) {
        Long usersWorkers=dataService.usersWorkersCount(userId);
        Long allUsers=dataService.workersCount();
        String message=String.format("Всего рабов в коллекции " +"%d" + "\n"+
                "Из них принадлежат вам " +"%d", allUsers, usersWorkers);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        Store.addToSendQueue(chatId, message);
    }
}
