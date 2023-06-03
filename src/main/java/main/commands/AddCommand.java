package main.commands;

import main.model.Worker;

import main.services.DataService;


public class AddCommand extends BaseCommand {
    @Override
    public String execute(DataService dataService){
        Worker test=new Worker();
        test.setName("Test");
        test.setSalary(214);

        dataService.addWorker(test);

        return "";
    }
}
