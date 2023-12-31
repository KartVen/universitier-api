package pl.kartven.universitier.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.Group;

import java.util.Collection;
import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query(
            value = "SELECT g FROM Group g WHERE " +
                    "(:number IS NULL OR g.number = :number) OR " +
                    "(:type IS NULL OR g.type = :type)",
            countQuery = "SELECT COUNT(g) FROM Group g WHERE " +
                    "(:number IS NULL OR g.number = :number) OR " +
                    "(:type IS NULL OR g.type = :type)"
    )
    Page<Group> findAllByCriteria(
            Integer number,
            String type,
            Pageable pageable
    );

    Set<Group> findDistinctByIdIn(Collection<Long> ids);

    @Query(
            value = "SELECT g.* FROM groups g " +
                    "LEFT JOIN connections cn ON g.id = cn.group_id " +
                    "LEFT JOIN academic_years ay ON cn.academic_year_id = ay.id " +
                    "LEFT JOIN modules m ON cn.module_id = m.id " +
                    "WHERE m.id = :moduleId AND ay.id = :academicYearId",
            nativeQuery = true
    )
    Set<Group> findAllByModuleIdAndAYId(Long moduleId, Long academicYearId);
}
