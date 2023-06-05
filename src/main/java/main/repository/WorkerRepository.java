package main.repository;

import main.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    @Query("select w from Worker w where w.ownerId = ?1 order by w.id DESC")
    List<Worker> findByOwnerIdOrderByIdDesc(long ownerId);

    @Query("select count(w) from Worker w where w.ownerId = ?1")
    long usersWorkerCount(long ownerId);

}
