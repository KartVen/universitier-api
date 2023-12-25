package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.Faculty;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long>, JpaSpecificationExecutor<Faculty> {
    @Query("select distinct f from Faculty f left join fetch f.courses where f.id = :id")
    Optional<Faculty> findByIdWithFetch(Long id);

    @Query(
            value = "SELECT f FROM Faculty f WHERE " +
                    "(:name IS NULL OR f.name = :name) OR " +
                    "(:shortName IS NULL OR f.shortname = :shortName)",
            countQuery = "SELECT COUNT(f) FROM Faculty f WHERE " +
                    "(:name IS NULL OR f.name = :name) OR " +
                    "(:shortName IS NULL OR f.shortname = :shortName)"
    )
    Page<Faculty> findAllByCriteria(
            String name,
            String shortName,
            Pageable pageable
    );

}
