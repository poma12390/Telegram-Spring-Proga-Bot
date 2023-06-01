package main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


@Entity//(name="workers")
@Table(name="workers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private int id;
    @SuppressWarnings("JpaDataSourceORMInspection")
    @Column(name="ownerId", nullable = false)
    private int ownerId;
    @Column(name="salary", nullable = false)
    private Double salary;

    public Worker(int ownerId, Double salary) {
        this.ownerId = ownerId;
        this.salary = salary;
    }

    public Worker() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }
}
