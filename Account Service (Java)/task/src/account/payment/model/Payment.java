package account.payment.model;

import account.user.model.AppUser;
import jakarta.persistence.*;

@Entity
@Table(name = "payments",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"employee_id", "period"})})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private AppUser employee; // foreign key to the 'users' table

    @Column(nullable = false)
    private String period; // mm-YYYY

    @Column(nullable = false)
    private long salary; // cents

    public Payment() {
    }

    public Payment(AppUser employee, String period, long salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public AppUser getEmployee() {
        return employee;
    }

    public void setEmployee(AppUser employee) {
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
