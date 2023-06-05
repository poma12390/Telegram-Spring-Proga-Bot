package main.commands;

import main.lib.Condition;
import main.lib.Store;
import main.model.Worker;

import main.services.DataService;
import main.validators.InputValidator;
import org.springframework.data.util.Pair;

import javax.xml.validation.Validator;


public class AddCommand extends BaseCommand {
    @Override
    public void execute(DataService dataService, Long chatId, Long userId, String text){
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
                break;
            case INPUTSALARY:
                curWorker=Store.getCurWorkerById(userId);
                try {
                    float salary=InputValidator.validateFloat(text);
                    if (salary<=0){
                        Store.addToSendQueue(chatId, "Не обижайте работника, он хочет кушать (ЗП > 0)");
                        break;
                    }
                    curWorker.setSalary(salary);
                    Store.setConditionById(userId, Condition.BASE);
                    Store.removeCurWorkerById(userId);
                    dataService.addWorker(curWorker);
                    Store.queueToSend.add(Pair.of(chatId, "Добавлен раб " + dataService.getLastWorkerByOwnerId(userId)));
                }   catch (NumberFormatException e){
                    Store.queueToSend.add(Pair.of(chatId, "Зарплата должна быть вещественным числом"));
                }finally {
                    break;
                }
        }
    }
}
