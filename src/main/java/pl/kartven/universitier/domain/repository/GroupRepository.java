package pl.kartven.universitier.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kartven.universitier.domain.model.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
