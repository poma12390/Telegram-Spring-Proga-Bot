package main.commands;

import main.lib.Condition;
import main.lib.Store;
import main.model.Worker;

import main.services.DataService;
import main.validators.InputValidator;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.xml.validation.Validator;


public class AddCommand extends BaseCommand {
    @Override
    public void execute(DataService dataService, Long chatId, Long userId, String text){
        Condition curCondition=Store.getCondition(userId);
        Worker curWorker;
        SendMessage sendMessage;
        switch (curCondition){
            case BASE:
                Store.setConditionById(userId, Condition.INPUTNAME);
                Store.setCurWorkerById(userId, new Worker(userId));
                sendMessage = new SendMessage();
                sendMessage.setText("Введите имя");
                Store.addToSendQueue(chatId, sendMessage);
                break;
            case INPUTNAME:
                curWorker=Store.getCurWorkerById(userId);
                curWorker.setName(text.trim());
                Store.setConditionById(userId, Condition.INPUTSALARY);
                Store.setCurWorkerById(userId, curWorker);
                sendMessage = new SendMessage();
                sendMessage.setText("Введите зарплату");
                Store.addToSendQueue(chatId, sendMessage);
                break;
            case INPUTSALARY:
                curWorker=Store.getCurWorkerById(userId);
                try {
                    float salary=InputValidator.validateFloat(text);
                    if (salary<=0){
                        sendMessage = new SendMessage();
                        sendMessage.setText("Не обижайте работника, он хочет кушать (ЗП > 0)");
                        Store.addToSendQueue(chatId, sendMessage);
                        break;
                    }
                    curWorker.setSalary(salary);
                    Store.setConditionById(userId, Condition.BASE);
                    Store.removeCurWorkerById(userId);
                    dataService.addWorker(curWorker);

                    sendMessage = new SendMessage();
                    sendMessage.setText("Добавлен раб"  + dataService.getLastWorkerByOwnerId(userId));
                    Store.addToSendQueue(chatId, sendMessage);

                }   catch (NumberFormatException e){

                    sendMessage = new SendMessage();
                    sendMessage.setText("Зарплата должна быть вещественным числом");
                    Store.addToSendQueue(chatId, sendMessage);
                }finally {
                    break;
                }
        }
    }
}
