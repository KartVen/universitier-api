package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.Staff;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {

    @Query("SELECT s FROM Staff s " +
            "INNER JOIN FETCH s.user " +
            "WHERE s.keyId = :id")
    Optional<Staff> findByKeyIdWithU(String id);

    @Query(
            value = "SELECT s FROM Staff s INNER JOIN FETCH s.user u WHERE " +
                    "(:firstName IS NULL OR s.firstName = :firstName) OR " +
                    "(:lastName IS NULL OR s.lastName = :lastName)",
            countQuery = "SELECT COUNT(s) FROM Staff s INNER JOIN s.user u WHERE " +
                    "(:firstName IS NULL OR s.firstName = :firstName) OR " +
                    "(:lastName IS NULL OR s.lastName = :lastName)"
    )
    Page<Staff> findAllByCriteria(
            String firstName,
            String lastName,
            Pageable pageable
    );
}
