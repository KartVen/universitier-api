package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.Student;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    @Query("SELECT s FROM Student s " +
            "INNER JOIN FETCH s.user " +
            "WHERE s.id = :id")
    Optional<Student> findByIdWithU(Long id);

    @Query(
            value = "SELECT s FROM Student s INNER JOIN FETCH s.user u WHERE " +
                    "(:firstName IS NULL OR s.firstName = :firstName) OR " +
                    "(:lastName IS NULL OR s.lastName = :lastName) ",
            countQuery = "SELECT COUNT(s) FROM Student s INNER JOIN s.user u WHERE " +
                    "(:firstName IS NULL OR s.firstName = :firstName) OR " +
                    "(:lastName IS NULL OR s.lastName = :lastName) "
    )
    Page<Student> findAllByCriteria(
            String firstName,
            String lastName,
            Pageable pageable
    );

}
