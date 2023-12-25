package pl.kartven.universitier.application.specification;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import java.util.Objects;

public interface JpaSpecification<T> {
    static <T> Specification<T> has(String field, Object value) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(value)) return null;
            if (value instanceof String) return criteriaBuilder.like(root.get(field), "%" + value + "%");
            return criteriaBuilder.equal(root.get(field), value);
        };
    }

    static <T> Specification<T> leftJoin(String field) {
        return leftJoin(field, false);
    }

    static <T> Specification<T> leftJoin(String field, boolean isDistinct) {
        return (root, query, criteriaBuilder) -> {
            root.fetch(field, JoinType.LEFT);
            if (isDistinct) {
                query.distinct(true);
            }
            return null;
        };
    }
}
