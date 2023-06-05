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

    public Long workersCount(){
        return workerRepository.count();
    }

    public Long usersWorkersCount(Long userId){
        return workerRepository.usersWorkerCount(userId);
    }
    public void addWorker(Worker w){
        workerRepository.save(w);
    }

    public Worker getLastWorkerByOwnerId(Long ownerId){
        return workerRepository.findByOwnerIdOrderByIdDesc(ownerId).get(0);
    }

}
