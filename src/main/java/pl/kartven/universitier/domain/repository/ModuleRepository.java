package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.Module;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long>, JpaSpecificationExecutor<Module> {
    @Query("SELECT m FROM Module m INNER JOIN FETCH m.programme LEFT JOIN FETCH m.connections WHERE m.id = :id")
    Optional<Module> findByIdWithPAndCN(Long id);

    @Query(
            value = "SELECT m FROM Module m " +
                    "INNER JOIN FETCH m.programme p WHERE " +
                    "(:name IS NULL OR m.name = :name) OR " +
                    "(:ects IS NULL OR m.ects = :ects) OR " +
                    "(:isExam IS NULL OR m.isExam = :isExam) OR " +
                    "(:programmeName IS NULL OR p.name = :programmeName)",
            countQuery = "SELECT COUNT(m) FROM Module m " +
                    "INNER JOIN m.programme p WHERE " +
                    "(:name IS NULL OR m.name = :name) OR " +
                    "(:ects IS NULL OR m.ects = :ects) OR " +
                    "(:isExam IS NULL OR m.isExam = :isExam) OR " +
                    "(:programmeName IS NULL OR p.name = :programmeName)"
    )
    Page<Module> findAllByCriteria(
            String name,
            Integer ects,
            Boolean isExam,
            String programmeName,
            Pageable pageable
    );

    @Query(
            value = "SELECT DISTINCT m.* FROM students s " +
                    "INNER JOIN student_connections sc ON s.id = sc.student_id " +
                    "INNER JOIN connections con ON sc.connection_id = con.id " +
                    "INNER JOIN modules m ON con.module_id = m.id " +
                    "INNER JOIN programmes p ON m.programme_id = p.id " +
                    "INNER JOIN courses c ON p.course_id = c.id " +
                    "WHERE s.id = :studentId AND con.academic_year_id IN :academicYearsIds",
            nativeQuery = true
    )
    Set<Module> findAllByStudentIdAndAYIds(Long studentId, Collection<Long> academicYearsIds);

    @Query(
            value = "SELECT DISTINCT m.* FROM connections con " +
                    "INNER JOIN modules m ON con.module_id = m.id " +
                    "INNER JOIN programmes p ON m.programme_id = p.id " +
                    "INNER JOIN courses c ON p.course_id = c.id " +
                    "INNER JOIN academic_years ay ON con.academic_year_id = ay.id " +
                    "WHERE con.id IN :connectionIds",
            nativeQuery = true
    )
    Set<Module> findAllByConIdInWithConAndPAndC(Collection<Long> connectionIds);

    Set<Module> findDistinctByIdIn(Collection<Long> ids);
}
