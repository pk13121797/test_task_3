package by.pavvel.repository;

import by.pavvel.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    @Query(
            value = "select e from Employee e left join fetch e.images where lower(e.name) like %?1% " +
                    "or lower(e.surname) like %?1% or lower(e.middleName) like %?1%",
            countQuery = "select count(e) from Employee e "
    )
    Page<Employee> findEmployees(String q, Pageable pageable);

    @Query(
            value = "select distinct e from Employee e left join fetch e.images",
            countQuery = "select count(e) from Employee e "
    )
    Page<Employee> findAllWithImagesPageable(Pageable pageable);

    @Modifying
    @Query("update Employee e set e.name = ?1,e.surname = ?2, e.middleName = ?3,e.post = ?4 where e.id = ?5")
    void updateEmployee(String name, String surname, String middleName, String post, Long id);

    @Modifying
    @Query("delete from Employee e where e.id =?1")
    void deleteEmployeeById(Long id);
}
