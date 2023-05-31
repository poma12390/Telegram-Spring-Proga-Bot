package main.lib;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.BlockingQueue;


public class Store {

    public static BlockingQueue<Update> requests;
}
