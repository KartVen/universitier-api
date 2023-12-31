package pl.kartven.universitier.application.usecase.module;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;

@Component
@AllArgsConstructor
public class ModuleDeleteUseCase implements IModuleDeleteUseCase {
    @Override
    public Either<ApiException, Void> execute(Long id) {
        return Either.right(null);
    }
}
