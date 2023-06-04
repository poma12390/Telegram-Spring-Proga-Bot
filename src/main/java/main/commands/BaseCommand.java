package main.commands;

import main.services.DataService;

public abstract class BaseCommand<T> {
    abstract T execute(DataService dataService, Long chatId, Long userId);
}
