package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.AcademicYear;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long>, JpaSpecificationExecutor<AcademicYear> {
    @Query(
            value = "SELECT s FROM AcademicYear s WHERE " +
                    "(:semesters IS NULL OR s.semesters = :semesters) AND " +
                    "(:academicYear IS NULL OR s.academicYear = :academicYear)",
            countQuery = "SELECT COUNT(s) FROM AcademicYear  s WHERE " +
                    "(:semesters IS NULL OR s.semesters = :semesters) AND " +
                    "(:academicYear IS NULL OR s.academicYear = :academicYear)"
    )
    Page<AcademicYear> findAllByCriteria(
            Integer semesters,
            String academicYear,
            Pageable pageable
    );

    Set<AcademicYear> findDistinctByIdIn(Collection<Long> ids);
}
