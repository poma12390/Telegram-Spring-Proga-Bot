package main.commands;

import main.lib.Store;
import main.services.DataService;
import main.utills.RandomUrlPicker;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import static main.frontend.BotCommands.HELP_TEXT;

public class HelpCommand extends BaseCommand{
    @Override
    public void execute(DataService dataService, Long chatId, Long userId, String text) {
        String message = HELP_TEXT;
        InputFile photo = new InputFile(RandomUrlPicker.pickRandomHelpUrl());
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(photo);
        sendPhoto.setCaption(message);
        Store.addToSendQueue(chatId, sendPhoto);
    }
}
