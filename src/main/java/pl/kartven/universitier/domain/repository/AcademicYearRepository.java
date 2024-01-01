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
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    @Query(
            value = "SELECT s FROM AcademicYear s WHERE " +
                    "(:mark IS NULL OR s.mark = :mark)",
            countQuery = "SELECT COUNT(s) FROM AcademicYear  s WHERE " +
                    "(:mark IS NULL OR s.mark = :mark)"
    )
    Page<AcademicYear> findAllByCriteria(
            String mark,
            Pageable pageable
    );

    Set<AcademicYear> findDistinctByIdIn(Collection<Long> ids);

    @Query(
            value = "SELECT DISTINCT ay.* FROM students s " +
                    "INNER JOIN student_connections sc ON s.id = sc.student_id " +
                    "INNER JOIN connections con ON sc.connection_id = con.id " +
                    "INNER JOIN academic_years ay ON con.academic_year_id = ay.id " +
                    "WHERE s.id = :id",
            nativeQuery = true
    )
    Set<AcademicYear> findAllByStudentId(Long id);

    @Query(
            value = "SELECT DISTINCT ay.mark FROM modules m " +
                    "INNER JOIN connections con ON m.id = con.module_id " +
                    "INNER JOIN academic_years ay ON con.academic_year_id = ay.id " +
                    "WHERE m.id IN :id",
            nativeQuery = true
    )
    Set<String> findAllMarkByModuleId(Long id);

    @Query(
            value = "SELECT DISTINCT ay.* FROM modules m " +
                    "INNER JOIN connections con ON m.id = con.module_id " +
                    "INNER JOIN academic_years ay ON con.academic_year_id = ay.id " +
                    "WHERE m.id IN :moduleIds",
            nativeQuery = true
    )
    Set<AcademicYear> findAllByModuleIdIn(Collection<Long> moduleIds);
}
