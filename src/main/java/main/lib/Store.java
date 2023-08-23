package main.lib;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import main.model.Worker;
import org.springframework.data.util.Pair;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.BlockingDeque;


@Slf4j
public class Store {
    @Getter
    private static Map<Long, Condition> conditionMap;
    public static BlockingDeque<Pair<Long, Object>> queueToSend;
    public static BlockingDeque<Update> queueToProcess;
    @Getter
    private static Map<Long, Worker> curWorkerMap;


    public static void addToSendQueue(Long chatId, String message){
        SendMessage NewsendMessage=new SendMessage();
        NewsendMessage.setText(message);
        queueToSend.add(Pair.of(chatId, NewsendMessage));
    }

    public static void addToSendQueue(Long chatId, SendPhoto message){
        queueToSend.add(Pair.of(chatId, message));
    }

    public static void addToSendQueue(Long chatId, DeleteMessage message){
        queueToSend.add(Pair.of(chatId, message));
    }

    public static void addToSendQueue(Long chatId, SendMessage message){
        queueToSend.add(Pair.of(chatId, message));
    }

    public static void setConditionMap(Map<Long, Condition> conditionMap) {
        Store.conditionMap = conditionMap;
    }

    public static boolean userExist(Long userId){
        return conditionMap.containsKey(userId);
    }

    public static void newUser(Long userId){
        conditionMap.put(userId, Condition.BASE);
        log.info("New user "+ userId);
    }


    public static void setCurWorkerMap(Map<Long, Worker> curWorkerMap) {
        Store.curWorkerMap = curWorkerMap;
    }

    public static void setCurWorkerById(Long userId, Worker worker){
        curWorkerMap.put(userId, worker);
    }

    public static void removeCurWorkerById(Long userId){
        curWorkerMap.remove(userId);
    }

    public static Worker getCurWorkerById(Long userId){
        return curWorkerMap.get(userId);
    }

    public static Condition getCondition(Long userId){
        return conditionMap.get(userId);
    }

    public static void setConditionById(Long userId, Condition condition){
        conditionMap.put(userId, condition);
    }

}
