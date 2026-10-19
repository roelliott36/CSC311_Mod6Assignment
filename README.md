# Weather Data Analyzer

## Project Overview

The **Weather Data Analyzer** is a JavaFX desktop application that imports weather data from CSV files and presents the information through tables, charts, and statistical summaries. The application helps users analyze daily weather measurements, identify temperature trends, compare precipitation and humidity, and rank the hottest or coldest days.

This project was developed for **CSC 311** to demonstrate modern Java programming features, functional programming concepts, file processing, data analysis, and graphical user interface development.

## Project Objectives

- Import and process weather data from CSV files.
- Calculate average temperatures and humidity.
- Identify days with temperatures above a specified threshold.
- Count rainy days based on precipitation measurements.
- Categorize temperatures as Hot, Warm, or Cold.
- Display weather data using interactive tables and charts.
- Rank the five hottest or coldest days.
- Apply modern Java language features to improve code readability and maintainability.

## Features

### 1. CSV File Import

Users can upload a `.csv` file containing daily weather observations. The application reads and processes the data, displays the imported records, and updates the dashboard statistics.

### 2. Weather Statistics

The dashboard displays key statistics calculated from the imported data:

- **Average Temperature:** The average daily temperature in degrees Fahrenheit.
- **Rainy Days:** The number of days with precipitation greater than zero.
- **Average Humidity:** The average relative humidity percentage.
- **Total Days:** The total number of valid weather records imported.

### 3. Monthly Average Temperature

Users can enter a month in `YYYY-MM` format to calculate the average temperature for that month.

For example, entering `2026-10` calculates the average temperature for October 2026, provided matching records exist in the imported dataset.

### 4. Temperature Threshold Analysis

Users can enter a temperature threshold to identify days when the recorded temperature is strictly greater than the specified value.

The results include the dates and temperatures that meet the selected condition.

### 5. Temperature Categories

Daily temperatures are categorized into three groups:

| Category | Temperature Range |
|---|---|
| Hot | 85°F and above |
| Warm | 65°F to below 85°F |
| Cold | Below 65°F |

A pie chart visualizes the distribution of weather records across these categories.

### 6. Weather Data Visualization

The dashboard includes:

- An imported weather data table.
- A precipitation and humidity bar chart.
- A temperature category pie chart.
- Summary statistics.
- A ranked weather data table.
- A text area for analysis results.

### 7. Hottest and Coldest Days

Users can select a ranking option to display the five hottest or five coldest days in the dataset. The ranking results are displayed in a separate table.

### 8. Summary Output

The application generates a text-based summary of the imported weather data and its calculated statistics. Analysis results can also be displayed in the summary area.

## Technologies Used

- **Java 21 language features** — records, switch expressions, streams, and lambdas.
- **JavaFX 21.0.6** — desktop graphical user interface.
- **Maven** — project configuration and dependency management.
- **FXML** — interface layout and component definitions.
- **CSS** — interface styling, if included in the project.
- **CSV file processing** — reading and analyzing weather observations.
- **Javadoc Markdown** — documenting Java code with Markdown-formatted comments where supported.

The project can run using JDK 26 while targeting Java 21 compatibility.

## Modern Java Features

This project demonstrates several modern Java programming concepts.

### Records

The `WeatherRecord` record represents one day's weather measurements:

```
public record WeatherRecord(
        LocalDate date,
        double temperature,
        double humidity,
        double precipitation
) {
}
```

Records provide a concise way to store related data without writing a separate class with constructors, accessors, and other standard methods.

### Switch Expressions

A switch expression categorizes temperatures and returns the appropriate category:

```
private String getTemperatureCategory(double temperature) {
    int category = temperature >= 85 ? 2
            : temperature >= 65 ? 1 : 0;

    return switch (category) {
        case 2 -> "Hot";
        case 1 -> "Warm";
        default -> "Cold";
    };
}
```

### Lambdas and Streams

Lambda expressions and streams can simplify filtering, sorting, and aggregation operations. For example, calculating the average temperature:

```
double averageTemperature = weatherData.stream()
        .mapToDouble(WeatherRecord::temperature)
        .average()
        .orElse(0.0);
```

Filtering days above a temperature threshold:

```
List<WeatherRecord> hotDays = weatherData.stream()
        .filter(record -> record.temperature() > threshold)
        .toList();
```

Ranking records can also use streams to sort weather observations by temperature.

### Text Blocks

Java text blocks make multiline summary output easier to format and read:

```
String summary = """
        Weather Data Summary
        --------------------
        Average Temperature: %.2f°F
        Average Humidity: %.2f%%
        Total Days: %d
        """.formatted(
        averageTemperature,
        averageHumidity,
        weatherData.size()
);
```

### Markdown-Formatted Javadoc

Markdown-style Javadoc comments document records, methods, parameters, and examples in a readable format. Markdown support in the standard Javadoc tool is available starting with JDK 23.

## CSV File Format

The application expects a CSV file with the following column headers:

```csv
date,temperature,humidity,precipitation
2026-10-01,72.5,65,0.0
2026-10-02,86.0,58,0.2
2026-10-03,61.5,80,1.4
2026-10-04,68.0,72,0.0
2026-10-05,90.0,50,
