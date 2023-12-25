package pl.kartven.universitier.application.usecase.module;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.domain.repository.ModuleRepository;

@AllArgsConstructor
@Component
public class ModuleGetUseCase {
    private final ModuleRepository repository;
}
