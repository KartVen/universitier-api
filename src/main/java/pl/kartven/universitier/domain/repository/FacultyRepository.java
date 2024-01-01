package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.Faculty;
import pl.kartven.universitier.domain.model.Group;
import pl.kartven.universitier.domain.model.Programme;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    @Query("SELECT f FROM Faculty f LEFT JOIN FETCH f.courses WHERE f.id = :id")
    Optional<Faculty> findByIdWithC(Long id);

    @Query(
            value = "SELECT f FROM Faculty f WHERE " +
                    "(:name IS NULL OR f.name = :name) OR " +
                    "(:shortName IS NULL OR f.shortName = :shortName)",
            countQuery = "SELECT COUNT(f) FROM Faculty f WHERE " +
                    "(:name IS NULL OR f.name = :name) OR " +
                    "(:shortName IS NULL OR f.shortName = :shortName)"
    )
    Page<Faculty> findAllByCriteria(
            String name,
            String shortName,
            Pageable pageable
    );

    Set<Faculty> findDistinctByIdIn(Collection<Long> ids);

}
