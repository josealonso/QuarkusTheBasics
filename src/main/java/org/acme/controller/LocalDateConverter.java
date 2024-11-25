package org.acme.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Named
@ApplicationScoped
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

    // JPA Converter methods
    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
        return Optional.ofNullable(localDate)
          .map(Date::valueOf)
          .orElse(null);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date) {
        return Optional.ofNullable(date)
          .map(Date::toLocalDate)
          .orElse(null);
    }
}
