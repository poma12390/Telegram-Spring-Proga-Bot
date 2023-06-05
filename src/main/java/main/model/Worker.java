package main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;


@Entity
@Table(name="workers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custom_seq")
    @SequenceGenerator(
            name = "custom_seq",
            allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private int id;

    @SuppressWarnings("JpaDataSourceORMInspection")
    @Column(name="name", nullable = false)
    private String name;
    @SuppressWarnings("JpaDataSourceORMInspection")
    @Column(name="ownerId", nullable = false)
    private long ownerId;
    @Column(name="salary", nullable = false)
    private float salary;

    @SuppressWarnings("JpaDataSourceORMInspection")
    @Column(name = "InitDate", nullable = false)
    private final Date initDate;


    public Worker(long ownerId) {
        this.initDate=new Date();
        this.ownerId = ownerId;
    }

    public Worker() {
        this.initDate=new Date();
    }

    public Date getInitDate() {
        return initDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return  "id " + id + ",\n" +
                "Зовут " + name + ",\n" +
                "Получает " + salary + ",\n" +
                "Принят на работу " + new SimpleDateFormat("dd MMMM yyyy hh:mm").format(initDate);
    }
}
