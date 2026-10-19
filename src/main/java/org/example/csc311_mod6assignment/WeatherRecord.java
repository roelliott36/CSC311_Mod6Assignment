
package org.example.csc311_mod6assignment;

import java.time.LocalDate;

/**
 * Represents one day's weather measurements.
 *
 * Example:
 * ```java
 * WeatherRecord day = new WeatherRecord(
 *     LocalDate.parse("2026-10-18"), 75.5, 60, 0.2
 * );
 * ```
 *
 * @param date the date of the observation
 * @param temperature the temperature in degrees Fahrenheit
 * @param humidity the relative humidity percentage
 * @param precipitation the amount of precipitation
 */
public record WeatherRecord(
        LocalDate date,
        double temperature,
        double humidity,
        double precipitation
) {
}
