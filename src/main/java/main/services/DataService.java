package main.services;

import main.model.Worker;
import main.repository.WorkerRepository;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    private final WorkerRepository workerRepository;

    public DataService(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }


    public void addWorker(Worker w){
        workerRepository.save(w);
    }
}
