package main.commands;

import main.services.DataService;

public class HelpCommand extends BaseCommand{
    @Override
    Object execute(DataService dataService, Long chatId, Long userId, String text) {
        return null;
    }
}
