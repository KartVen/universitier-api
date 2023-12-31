package pl.kartven.universitier.infrastructure.group.dto;

import lombok.Value;
import pl.kartven.universitier.domain.model.Group;

@Value
public class GroupViewResponse {
    Long id;
    Integer number;
    Group.Type type;
}
