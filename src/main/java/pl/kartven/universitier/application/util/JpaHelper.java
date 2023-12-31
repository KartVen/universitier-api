package pl.kartven.universitier.application.util;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JpaHelper {
    public static  <T> Either<ApiException, Set<T>> getEntitiesSets(
            Function<Collection<Long>, Set<T>> repoFindFun,
            Collection<Long> ids,
            Function<T, Long> entityGetIdFun,
            String notFoundExceptionName
    ) {
        var entitiesEither = Option.of(repoFindFun.apply(ids))
                .toEither((ApiException) new ServerProcessingException());
        if (entitiesEither.isLeft()) return Either.left(entitiesEither.getLeft());
        if (entitiesEither.get().size() != ids.size()) {
            var missingIds = entitiesEither.get()
                    .stream()
                    .map(entityGetIdFun)
                    .filter(id -> !ids.contains(id))
                    .toList();
            entitiesEither.mapLeft(e -> new ResourceNotFoundException(notFoundExceptionName + " not found: " + missingIds));
        }
        return entitiesEither;
    }
}
