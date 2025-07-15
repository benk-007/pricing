package com.smsmode.pricing.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class DayOfWeekSetConverter implements AttributeConverter<Set<DayOfWeek>, String> {

    /**
     * Converts a Set<DayOfWeek> to a comma-separated string of ordinal values for database storage.
     */
    @Override
    public String convertToDatabaseColumn(Set<DayOfWeek> dayOfWeeks) {
        if (CollectionUtils.isEmpty(dayOfWeeks)) {
            return null;
        }
        return dayOfWeeks.stream()
                .map(day -> String.valueOf(day.ordinal()))
                .collect(Collectors.joining(","));
    }

    /**
     * Converts a comma-separated string of ordinal values back to a Set<DayOfWeek>.
     */
    @Override
    public Set<DayOfWeek> convertToEntityAttribute(String dbData) {
        if (ObjectUtils.isEmpty(dbData)) {
            return new LinkedHashSet<>();
        }
        return Arrays.stream(dbData.split(","))
                .map(s -> DayOfWeek.values()[Integer.parseInt(s)])
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}