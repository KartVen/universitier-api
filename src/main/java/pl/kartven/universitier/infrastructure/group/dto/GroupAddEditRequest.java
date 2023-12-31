package pl.kartven.universitier.infrastructure.group.dto;

import lombok.Data;
import pl.kartven.universitier.domain.model.Group;

@Data
public class GroupAddEditRequest {
    private final Integer number;
    private final Group.Type type;
}
