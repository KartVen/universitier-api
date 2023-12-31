package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.AcademicYear;

import java.util.Collection;
import java.util.Set;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long>, JpaSpecificationExecutor<AcademicYear> {
    @Query(
            value = "SELECT s FROM AcademicYear s WHERE " +
                    "(:semesters IS NULL OR s.semesters = :semesters) AND " +
                    "(:range IS NULL OR s.range = :range)",
            countQuery = "SELECT COUNT(s) FROM AcademicYear  s WHERE " +
                    "(:semesters IS NULL OR s.semesters = :semesters) AND " +
                    "(:range IS NULL OR s.range = :range)"
    )
    Page<AcademicYear> findAllByCriteria(
            Integer semesters,
            String range,
            Pageable pageable
    );

    Set<AcademicYear> findDistinctByIdIn(Collection<Long> ids);

    @Query(
            value = "SELECT DISTINCT ay.* FROM students s " +
                    "INNER JOIN student_connections sc ON s.id = sc.student_id " +
                    "INNER JOIN connections con ON sc.connection_id = con.id " +
                    "INNER JOIN academic_years ay ON con.academic_year_id = ay.id " +
                    "WHERE s.id = 1",
            nativeQuery = true
    )
    Set<AcademicYear> findAllByStudentId(Long id);
}
