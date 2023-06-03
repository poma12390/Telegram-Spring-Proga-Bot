package main.lib;

import org.springframework.data.util.Pair;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.BlockingDeque;


public class Store {
    public static Map<Integer, Condition> conditionMap;


    public static BlockingDeque<Pair<Long, String>> queueToSend;
    public static BlockingDeque<Update> queueToProcess;


}
