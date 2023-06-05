package main.commands;

import main.services.DataService;

public abstract class BaseCommand<T> {
    abstract void execute(DataService dataService, Long chatId, Long userId, String text);
}
