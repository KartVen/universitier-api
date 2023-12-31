package pl.kartven.universitier.application.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Data
@NoArgsConstructor
public class FilterParams {
    String phrase;

    public String getPhrase() {
        return phrase == null || phrase.isEmpty() ? null: phrase;
    }

    @Component
    public static class FilterParamsConverterImpl extends FilterParamsConverter<FilterParams> {
        @Override
        protected Class<FilterParams> getResultClass() {
            return FilterParams.class;
        }
    }
}
