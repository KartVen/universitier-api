package pl.kartven.universitier.application.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
public class FilterParams {
    String phrase;

    @Component
    public static class FilterParamsConverterImpl extends FilterParamsConverter<FilterParams> {
        @Override
        protected Class<FilterParams> getResultClass() {
            return FilterParams.class;
        }
    }
}
