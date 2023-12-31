package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pl.kartven.universitier.domain.model.Programme;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ProgrammeRepository extends JpaRepository<Programme, Long>, JpaSpecificationExecutor<Programme> {
    @Query("SELECT p FROM Programme p " +
            "INNER JOIN FETCH p.course " +
            "LEFT JOIN FETCH p.modules " +
            "WHERE p.id = :id")
    Optional<Programme> findByIdWithCAndM(Long id);

    @Query(
            value = "SELECT p FROM Programme p " +
                    "INNER JOIN FETCH p.course c WHERE " +
                    "(:name IS NULL OR p.name = :name) OR " +
                    "(:shortName IS NULL OR p.shortname = :shortName) OR " +
                    "(:courseName IS NULL OR c.name = :courseName)",
            countQuery = "SELECT COUNT(p) FROM Programme p " +
                    "INNER JOIN p.course c WHERE " +
                    "(:name IS NULL OR p.name = :name) OR " +
                    "(:courseName IS NULL OR c.name = :courseName)"
    )
    Page<Programme> findAllByCriteria(
            String name,
            String shortName,
            String courseName,
            Pageable pageable
    );

    Set<Programme> findDistinctByIdIn(Collection<Long> ids);
}
