package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.Connection;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    @Query(
            value = "SELECT c FROM Connection c " +
                    "INNER JOIN FETCH c.module m " +
                    "INNER JOIN FETCH c.academicYear ay WHERE " +
                    "(:moduleName IS NULL OR m.name = :moduleName) OR " +
                    "(:academicYearMark IS NULL OR ay.mark = :academicYearMark)",
            countQuery = "SELECT COUNT(c) FROM Connection c " +
                    "INNER JOIN c.module m " +
                    "INNER JOIN c.academicYear ay WHERE " +
                    "(:moduleName IS NULL OR m.name = :moduleName) OR " +
                    "(:academicYearMark IS NULL OR ay.mark = :academicYearMark)"
    )
    Page<Connection> findAllByCriteria(
            String moduleName,
            String academicYearMark,
            Pageable pageable
    );

    @Query("SELECT c FROM Connection c " +
            "INNER JOIN FETCH c.module " +
            "INNER JOIN FETCH c.academicYear " +
            "INNER JOIN FETCH c.group " +
            "WHERE c.id = :id")
    Optional<Connection> findByIdWithMAndACAndG(Long id);

    @Query("SELECT c FROM Connection c " +
            "INNER JOIN FETCH c.module " +
            "INNER JOIN FETCH c.academicYear " +
            "WHERE c.id = :id")
    Optional<Connection> findByIdWithMAndAC(Long id);

    @Query("SELECT c FROM Connection c " +
            "INNER JOIN FETCH c.module m " +
            "INNER JOIN FETCH c.academicYear ay " +
            "INNER JOIN FETCH c.group " +
            "WHERE m.id = :moduleId AND ay.id = :academicYearId")
    Set<Connection> findAllByModuleIdAndAYIdWithMAndACAndG(Long moduleId, Long academicYearId);

    @Query(
            value = "SELECT con FROM Student s " +
                    "INNER JOIN s.connections con " +
                    "WHERE s.id = :studentId"
    )
    Set<Connection> findAllByStudentId(Long studentId);

    @Query(
            value = "SELECT DISTINCT con.* FROM connections con " +
                    "JOIN modules m ON con.module_id = m.id " +
                    "JOIN programmes p ON m.programme_id = p.id " +
                    "WHERE p.id = :programmeId AND con.academic_year_id = :academicYearId",
            nativeQuery = true
    )
    Set<Connection> findAllByProgrammeIdAndAYId(Long programmeId, Long academicYearId);

    @Query(
            value = "SELECT DISTINCT con.* FROM connections con " +
                    "JOIN modules m ON con.module_id = m.id " +
                    "JOIN programmes p ON m.programme_id = p.id " +
                    "WHERE p.id IN :programmeIds AND con.academic_year_id IN :academicYearIds",
            nativeQuery = true
    )
    Set<Connection> findAllByProgrammeIdInAndAYIdIn(Collection<Long> programmeIds, Collection<Long> academicYearIds);

    @Query(
            value = "SELECT DISTINCT con.* FROM connections con " +
                    "JOIN modules m ON con.module_id = m.id " +
                    "WHERE m.id IN :moduleIds AND con.academic_year_id IN :academicYearIds",
            nativeQuery = true
    )
    Set<Connection> findAllByModuleIdInAndAYIdIn(Collection<Long> moduleIds, Collection<Long> academicYearIds);
}
