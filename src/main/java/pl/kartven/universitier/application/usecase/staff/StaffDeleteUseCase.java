package pl.kartven.universitier.application.usecase.staff;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;

@Component
@AllArgsConstructor
public class StaffDeleteUseCase implements IStaffDeleteUseCase {
    @Override
    public Either<ApiException, Void> execute(Long id) {
        return Either.right(null);
    }
}
