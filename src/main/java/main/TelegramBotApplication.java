package main;


import main.lib.Store;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;

@SpringBootApplication()
public class TelegramBotApplication implements CommandLineRunner {

    private final MyTelegramBot myTelegramBot;

    public TelegramBotApplication(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
        Store.setConditionMap(new HashMap<>());
        Store.setCurWorkerMap(new HashMap<>());
        Store.queueToSend=new LinkedBlockingDeque<>();
        Store.queueToProcess=new LinkedBlockingDeque<>();
    }

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(myTelegramBot);

    }
}
