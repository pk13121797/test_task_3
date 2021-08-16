package by.pavvel.project.repository;

import by.pavvel.project.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    @Modifying
    @Query("update Employee e set e.name = ?1,e.surname = ?2, e.middleName = ?3,e.post = ?4 where e.id = ?5")
    void updateEmployee(String name, String surname, String middleName,
                            String post, Long id);

    @Modifying
    @Query("delete from Employee e where e.id =?1")
    void deleteEmployeeById(Long id);
}
