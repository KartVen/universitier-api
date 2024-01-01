package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.Course;
import pl.kartven.universitier.domain.model.Faculty;
import pl.kartven.universitier.domain.model.Group;
import pl.kartven.universitier.domain.model.Module;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c " +
            "INNER JOIN FETCH c.faculty " +
            "WHERE c.id = :id")
    Optional<Course> findByIdWithFAndCN(Long id);

    @Query("SELECT c FROM Course c " +
            "INNER JOIN FETCH c.faculty " +
            "LEFT JOIN FETCH c.programmes " +
            "WHERE c.id = :id")
    Optional<Course> findByIdWithFAndP(Long id);

    @Query(
            value = "SELECT c FROM Course c " +
                    "INNER JOIN FETCH c.faculty f WHERE " +
                    "(:name IS NULL OR c.name = :name) OR " +
                    "(:shortName IS NULL OR f.shortName = :shortName)",
            countQuery = "SELECT COUNT(c) FROM Course c " +
                    "INNER JOIN c.faculty f WHERE " +
                    "(:name IS NULL OR c.name = :name) OR " +
                    "(:shortName IS NULL OR f.shortName = :shortName)"
    )
    Page<Course> findAllByCriteria(
            String name,
            @Param("shortName") String facultyShortName,
            Pageable pageable
    );

    @Query(
            value = "SELECT DISTINCT m.* FROM students s " +
                    "INNER JOIN student_connections sc ON s.id = sc.student_id " +
                    "INNER JOIN connections con ON sc.connection_id = con.id " +
                    "INNER JOIN modules m ON con.module_id = m.id " +
                    "INNER JOIN programmes p ON m.programme_id = p.id " +
                    "INNER JOIN courses c ON p.course_id = c.id " +
                    "WHERE s.id = :studentId AND con.academic_year_id IN :academicYearIds",
            nativeQuery = true
    )
    Set<Course> findAllByStudentIdAndAYIds(Long studentId, Collection<Long> academicYearIds);

    @Query(
            value = "SELECT DISTINCT c.* FROM connections con " +
                    "INNER JOIN modules m ON con.module_id = m.id " +
                    "INNER JOIN programmes p ON m.programme_id = p.id " +
                    "INNER JOIN courses c ON p.course_id = c.id " +
                    "WHERE con.id IN :connectionIds",
            nativeQuery = true
    )
    Set<Course> findAllByConIdInWithConAndMAndP(Collection<Long> connectionIds);

    @Query(
            value = "SELECT DISTINCT c.* FROM courses c " +
                    "JOIN programmes pr ON c.id = pr.course_id " +
                    "JOIN modules m ON pr.id = m.programme_id " +
                    "JOIN connections con ON m.id = con.module_id " +
                    "JOIN academic_years ay ON con.academic_year_id = ay.id " +
                    "WHERE pr.id = :programmeId AND ay.id = :academicYearId",
            nativeQuery = true
    )
    Set<Course> findAllByProgIdAndAYId(Long programmeId, Long academicYearId);
}

