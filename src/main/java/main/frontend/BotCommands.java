package main.frontend;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "start bot"),
            new BotCommand("/help", "bot info"),
            new BotCommand("/add", "add worker"),
            new BotCommand("/info", "info about your workers")
    );

    String HELP_TEXT = "This bot was made by @poma12390 " +
            "The following commands are available to you:\n" +
            "/start - start the bot\n" +
            "/help - help menu\n" +
            "/add - add worker\n" +
            "/info - info about your workers";
}