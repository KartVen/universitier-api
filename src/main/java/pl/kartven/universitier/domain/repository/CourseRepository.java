package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.Course;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    @Query("SELECT c FROM Course c INNER JOIN FETCH c.faculty LEFT JOIN FETCH c.academicYears WHERE c.id = :id")
    Optional<Course> findByIdWithFAndAY(Long id);

    @Query(
            value = "SELECT c FROM Course c " +
                    "INNER JOIN FETCH c.faculty f WHERE " +
                    "(:name IS NULL OR c.name = :name) OR " +
                    "(:shortname IS NULL OR f.shortname = :shortname)",
            countQuery = "SELECT COUNT(c) FROM Course c " +
                    "INNER JOIN c.faculty f WHERE " +
                    "(:name IS NULL OR c.name = :name) OR " +
                    "(:shortname IS NULL OR f.shortname = :shortname)"
    )
    Page<Course> findAllByCriteria(
            String name,
            @Param("shortname") String facultyShortName,
            Pageable pageable
    );
}

