package main.services;

import main.model.Worker;
import main.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {
    private final WorkerRepository workerRepository;

    @Autowired
    public DataService(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    public void addWorker(Worker w){
        workerRepository.save(w);
    }
}
