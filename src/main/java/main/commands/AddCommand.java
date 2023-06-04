package main.commands;

import main.lib.Condition;
import main.lib.Store;
import main.model.Worker;

import main.services.DataService;
import org.springframework.data.util.Pair;


public class AddCommand extends BaseCommand {
    @Override
    public String execute(DataService dataService, Long chatId, Long userId){
        Condition curCondition=Store.getCondition(userId);
        switch (curCondition){
            case BASE:
                Worker newWorker=new Worker(userId);

                break;
        }
        Worker test=new Worker();
        test.setName("Test");
        test.setSalary(214);

        dataService.addWorker(test);
        Store.queueToSend.add(Pair.of(chatId, test.toString()));

        return "";
    }
}
