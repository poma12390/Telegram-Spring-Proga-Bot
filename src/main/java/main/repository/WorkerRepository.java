package main.repository;

import main.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WorkerRepository extends JpaRepository<Worker, Integer> {
}
