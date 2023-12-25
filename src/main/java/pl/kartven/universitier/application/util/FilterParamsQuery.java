package pl.kartven.universitier.application.util;

import org.springframework.data.jpa.domain.Specification;

public interface FilterParamsQuery<T, R> {
    Specification<R> genFilterQuery(T filterParams);
}
