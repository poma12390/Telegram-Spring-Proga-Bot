package main.commands;

import main.lib.Condition;
import main.lib.Store;
import main.model.Worker;

import main.services.DataService;
import org.springframework.data.util.Pair;


public class AddCommand extends BaseCommand {
    @Override
    public String execute(DataService dataService, Long chatId, Long userId, String text){
        Condition curCondition=Store.getCondition(userId);
        Worker curWorker;
        switch (curCondition){
            case BASE:
                Store.setConditionById(userId, Condition.INPUTNAME);
                Store.setCurWorkerById(userId, new Worker(userId));
                Store.queueToSend.add(Pair.of(chatId, "Введите имя"));
                break;
            case INPUTNAME:
                curWorker=Store.getCurWorkerById(userId);
                curWorker.setName(text.trim());
                Store.setConditionById(userId, Condition.INPUTSALARY);
                Store.setCurWorkerById(userId, curWorker);
                Store.queueToSend.add(Pair.of(chatId, "Введите зарплату"));
            case INPUTSALARY:
                curWorker=Store.getCurWorkerById(userId);
                curWorker.setSalary(Float.parseFloat(text));
                Store.setConditionById(userId, Condition.BASE);
                Store.removeCurWorkerById(userId);
                Store.queueToSend.add(Pair.of(chatId, "Добавлен раб " + curWorker));
                dataService.addWorker(curWorker);
        }


        return "";
    }
}
