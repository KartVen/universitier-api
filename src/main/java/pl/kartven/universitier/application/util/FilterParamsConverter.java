package pl.kartven.universitier.application.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import pl.kartven.universitier.application.exception.BadRequestException;

public abstract class FilterParamsConverter<T> implements Converter<String, T> {
    @Autowired
    private ObjectMapper objectMapper;

    protected abstract Class<T> getResultClass();

    public T convert(@NonNull String source) {
        return Try.of(() -> objectMapper.readValue(source, getResultClass()))
                .getOrElseThrow(() ->
                        new BadRequestException("Can't parse " + getClass().getSimpleName() + " params: " + source)
                );
    }

    @Override
    @NonNull
    public <U> Converter<String, U> andThen(@NonNull Converter<? super T, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}