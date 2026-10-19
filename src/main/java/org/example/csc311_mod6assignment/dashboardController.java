package org.example.csc311_mod6assignment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controls the Weather Data Analyzer dashboard.
 *
 * Loads CSV weather records, calculates statistics, creates charts,
 * and displays ranked weather observations.
 *
 * Example CSV:
 * ```text
 * date,temperature,humidity,precipitation
 * 2026-10-11,59.8,85,0.00
 * 2026-10-12,62.6,77,0.10
 * ```
 */
public class dashboardController {

    @FXML private TableView<WeatherRecord> importedDataViewTable;
    @FXML private TableColumn<WeatherRecord, String> importedDataDateView;
    @FXML private TableColumn<WeatherRecord, String> importedDataTempView;
    @FXML private TableColumn<WeatherRecord, String> importedDataHumidityView;
    @FXML private TableColumn<WeatherRecord, String> importedDataPrecipitationView;

    @FXML private TextArea summaryTextArea;

    @FXML private TableView<WeatherRecord> rankedViewTable;
    @FXML private TableColumn<WeatherRecord, String> rankedDataDateView;
    @FXML private TableColumn<WeatherRecord, String> rankedDataTempView;
    @FXML private TableColumn<WeatherRecord, String> rankedDataHumidityView;
    @FXML private TableColumn<WeatherRecord, String> rankedDataPrecipitationView;

    @FXML private MenuButton selectRankingMenuDropdown;
    @FXML private MenuItem top5HottestDays;
    @FXML private MenuItem top5ColdestDays;

    @FXML private Button applyRankingButton;
    @FXML private Button uploadFileButton;

    @FXML private TextField fileNameTextDisplay;
    @FXML private TextField dateRangeTextDisplay;

    @FXML private Label avgTempNumberDisplay;
    @FXML private Label numDaysNumberDisplay;
    @FXML private Label avgHumidityPercentDisplay;
    @FXML private Label totalDaysNumDisplay;

    @FXML private PieChart tempCategoriesPieChart;
    @FXML private BarChart<String, Number> precipitationVsHumidity;

    private final ObservableList<WeatherRecord> weatherData =
            FXCollections.observableArrayList();

    private String selectedRanking = "HOT";

    /**
     * Initializes table columns and connects menu actions.
     */
    @FXML
    private void initialize() {
        configureTableColumns();

        top5HottestDays.setOnAction(event -> {
            selectedRanking = "HOT";
            selectRankingMenuDropdown.setText("Top 5 Hottest Days");
        });

        top5ColdestDays.setOnAction(event -> {
            selectedRanking = "COLD";
            selectRankingMenuDropdown.setText("Top 5 Coldest Days");
        });

        importedDataViewTable.setItems(weatherData);

        summaryTextArea.setEditable(false);
        summaryTextArea.setWrapText(true);

        applyRankingButton.setDisable(true);
    }

    /**
     * Configures table cells to display values from each weather record.
     */
    private void configureTableColumns() {
        importedDataDateView.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().date().toString()));

        importedDataTempView.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        String.format("%.1f", cell.getValue().temperature())));

        importedDataHumidityView.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        String.format("%.1f%%", cell.getValue().humidity())));

        importedDataPrecipitationView.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        String.format("%.2f", cell.getValue().precipitation())));

        rankedDataDateView.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().date().toString()));

        rankedDataTempView.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        String.format("%.1f", cell.getValue().temperature())));

        rankedDataHumidityView.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        String.format("%.1f%%", cell.getValue().humidity())));

        rankedDataPrecipitationView.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        String.format("%.2f", cell.getValue().precipitation())));
    }

    /**
     * Opens a file chooser and imports a weather CSV file.
     *
     * The CSV must contain date, temperature, humidity, and precipitation
     * columns. Dates must use ISO format, such as 2026-10-18.
     */
    @FXML
    private void uploadFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Weather CSV File");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = chooser.showOpenDialog(uploadFileButton.getScene().getWindow());

        if (file == null) {
            return;
        }

        try {
            List<WeatherRecord> importedRecords = parseCsv(file);

            if (importedRecords.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "No Weather Data",
                        "The selected CSV file contains no valid weather records.");
                return;
            }

            weatherData.setAll(importedRecords);
            fileNameTextDisplay.setText(file.getName());

            LocalDate firstDate = weatherData.stream()
                    .map(WeatherRecord::date)
                    .min(Comparator.naturalOrder())
                    .orElseThrow();

            LocalDate lastDate = weatherData.stream()
                    .map(WeatherRecord::date)
                    .max(Comparator.naturalOrder())
                    .orElseThrow();

            dateRangeTextDisplay.setText(firstDate + " to " + lastDate);

            updateDashboard();
            applyRankingButton.setDisable(false);

        } catch (IOException | IllegalArgumentException exception) {
            showAlert(Alert.AlertType.ERROR, "CSV Import Failed",
                    exception.getMessage());
        }
    }

    /**
     * Reads and validates weather records from a CSV file.
     *
     * Example:
     * ```java
     * List<WeatherRecord> records = parseCsv(file);
     * ```
     *
     * @param file the selected CSV file
     * @return the successfully parsed weather records
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if a data row is invalid
     */
    private List<WeatherRecord> parseCsv(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("The CSV file is empty.");
        }

        String header = lines.getFirst().strip().toLowerCase();

        if (!header.contains("date")
                || !header.contains("temperature")
                || !header.contains("humidity")
                || !header.contains("precipitation")) {
            throw new IllegalArgumentException(
                    "CSV headers must include date, temperature, humidity, and precipitation.");
        }

        List<WeatherRecord> records = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).strip();

            if (line.isEmpty()) {
                continue;
            }

            String[] values = line.split(",", -1);

            if (values.length != 4) {
                throw new IllegalArgumentException(
                        "Invalid CSV format on line " + (i + 1)
                                + ". Each row must contain four comma-separated values.");
            }

            try {
                LocalDate date = LocalDate.parse(values[0].strip());
                double temperature = Double.parseDouble(values[1].strip());
                double humidity = Double.parseDouble(values[2].strip());
                double precipitation = Double.parseDouble(values[3].strip());

                if (!Double.isFinite(temperature)
                        || !Double.isFinite(humidity)
                        || !Double.isFinite(precipitation)) {
                    throw new IllegalArgumentException(
                            "Non-finite number on line " + (i + 1) + ".");
                }

                if (humidity < 0 || humidity > 100 || precipitation < 0) {
                    throw new IllegalArgumentException(
                            "Humidity must be 0–100 and precipitation cannot be negative. "
                                    + "Check line " + (i + 1) + ".");
                }

                records.add(new WeatherRecord(
                        date, temperature, humidity, precipitation));

            } catch (DateTimeParseException | NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Invalid date or number on CSV line " + (i + 1) + ".");
            }
        }

        return records.stream()
                .sorted(Comparator.comparing(WeatherRecord::date))
                .toList();
    }

    /**
     * Refreshes the statistics, charts, and imported data table.
     */
    private void updateDashboard() {
        if (weatherData.isEmpty()) {
            return;
        }

        double averageTemperature = weatherData.stream()
                .mapToDouble(WeatherRecord::temperature)
                .average()
                .orElse(0);

        double averageHumidity = weatherData.stream()
                .mapToDouble(WeatherRecord::humidity)
                .average()
                .orElse(0);

        long rainyDays = weatherData.stream()
                .filter(record -> record.precipitation() > 0)
                .count();

        avgTempNumberDisplay.setText(
                String.format("%.1f °F", averageTemperature));

        avgHumidityPercentDisplay.setText(
                String.format("%.1f%%", averageHumidity));

        numDaysNumberDisplay.setText(String.valueOf(rainyDays));
        totalDaysNumDisplay.setText(String.valueOf(weatherData.size()));

        updateTemperaturePieChart();
        updateWeatherBarChart();
        updateSummary();
    }

    /**
     * Categorizes a temperature using an enhanced switch expression.
     *
     * @param temperature the temperature in degrees Fahrenheit
     * @return Hot, Warm, or Cold
     */
    private String getTemperatureCategory(double temperature) {
        int category = temperature >= 85 ? 2 : temperature >= 65 ? 1 : 0;

        return switch (category) {
            case 2 -> "Hot";
            case 1 -> "Warm";
            default -> "Cold";
        };
    }

    /**
     * Builds a pie chart showing the number of Hot, Warm, and Cold days.
     */
    private void updateTemperaturePieChart() {
        Map<String, Long> categories = weatherData.stream()
                .collect(Collectors.groupingBy(
                        record -> getTemperatureCategory(record.temperature()),
                        Collectors.counting()));

        ObservableList<PieChart.Data> slices =
                FXCollections.observableArrayList();

        for (String category : List.of("Hot", "Warm", "Cold")) {
            slices.add(new PieChart.Data(
                    category + " (" + categories.getOrDefault(category, 0L) + ")",
                    categories.getOrDefault(category, 0L)));
        }

        tempCategoriesPieChart.setData(slices);
    }

    /**
     * Creates a bar chart comparing precipitation and humidity.
     * To keep the chart readable, at most the first 12 dates are displayed.
     */
    private void updateWeatherBarChart() {
        XYChart.Series<String, Number> precipitationSeries =
                new XYChart.Series<>();
        precipitationSeries.setName("Precipitation");

        XYChart.Series<String, Number> humiditySeries =
                new XYChart.Series<>();
        humiditySeries.setName("Humidity (%)");

        weatherData.stream()
                .limit(12)
                .forEach(record -> {
                    String date = record.date().toString();

                    precipitationSeries.getData().add(
                            new XYChart.Data<>(date, record.precipitation()));

                    humiditySeries.getData().add(
                            new XYChart.Data<>(date, record.humidity()));
                });

        precipitationVsHumidity.getData().setAll(
                precipitationSeries, humiditySeries);
    }

    /**
     * Displays an overall text summary using a Java text block.
     */
    private void updateSummary() {
        double averageTemperature = weatherData.stream()
                .mapToDouble(WeatherRecord::temperature)
                .average()
                .orElse(0);

        double averageHumidity = weatherData.stream()
                .mapToDouble(WeatherRecord::humidity)
                .average()
                .orElse(0);

        long rainyDays = weatherData.stream()
                .filter(record -> record.precipitation() > 0)
                .count();

        long hotDays = weatherData.stream()
                .filter(record -> getTemperatureCategory(record.temperature())
                        .equals("Hot"))
                .count();

        long warmDays = weatherData.stream()
                .filter(record -> getTemperatureCategory(record.temperature())
                        .equals("Warm"))
                .count();

        long coldDays = weatherData.size() - hotDays - warmDays;

        String summary = """
                WEATHER DATA SUMMARY
                --------------------
                Total observations: %d
                Average temperature: %.1f °F
                Average humidity: %.1f%%
                Rainy days: %d

                TEMPERATURE CATEGORIES
                Hot (85°F and above): %d
                Warm (65°F to below 85°F): %d
                Cold (below 65°F): %d

                Use the analysis buttons to explore a specific month
                or find days above a temperature threshold.
                """.formatted(
                weatherData.size(),
                averageTemperature,
                averageHumidity,
                rainyDays,
                hotDays,
                warmDays,
                coldDays).stripIndent();

        summaryTextArea.setText(summary);
    }

    /**
     * Displays the five hottest or coldest days based on the menu selection.
     */
    @FXML
    private void applyRanking() {
        if (weatherData.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Data",
                    "Upload a CSV file before applying a ranking.");
            return;
        }

        Comparator<WeatherRecord> comparator =
                Comparator.comparingDouble(WeatherRecord::temperature);

        List<WeatherRecord> ranked;

        if (selectedRanking.equals("HOT")) {
            ranked = weatherData.stream()
                    .sorted(comparator.reversed())
                    .limit(5)
                    .toList();
        } else {
            ranked = weatherData.stream()
                    .sorted(comparator)
                    .limit(5)
                    .toList();
        }

        rankedViewTable.setItems(
                FXCollections.observableArrayList(ranked));
    }

    /**
     * Calculates the average temperature for a requested month.
     *
     * The user enters a month in YYYY-MM format, such as 2026-10.
     */
    @FXML
    private void showMonthlyAverage() {
        if (weatherData.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Data",
                    "Upload a CSV file first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(
                YearMonth.from(weatherData.getFirst().date()).toString());

        dialog.setTitle("Monthly Average Temperature");
        dialog.setHeaderText("Calculate an average temperature for one month");
        dialog.setContentText("Enter month (YYYY-MM):");

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        try {
            YearMonth month = YearMonth.parse(result.get().strip());

            List<WeatherRecord> monthlyRecords = weatherData.stream()
                    .filter(record -> YearMonth.from(record.date()).equals(month))
                    .toList();

            if (monthlyRecords.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Matching Data",
                        "There are no observations for " + month + ".");
                return;
            }

            double average = monthlyRecords.stream()
                    .mapToDouble(WeatherRecord::temperature)
                    .average()
                    .orElse(0);

            summaryTextArea.setText("""
                    MONTHLY TEMPERATURE REPORT
                    --------------------------
                    Month: %s
                    Number of observations: %d
                    Average temperature: %.1f °F
                    """.formatted(month, monthlyRecords.size(), average)
                    .stripIndent());

        } catch (DateTimeParseException exception) {
            showAlert(Alert.AlertType.ERROR, "Invalid Month",
                    "Enter the month in YYYY-MM format, such as 2026-10.");
        }
    }

    /**
     * Lists the days whose temperatures exceed a user-provided threshold.
     */
    @FXML
    private void showDaysAboveThreshold() {
        if (weatherData.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Data",
                    "Upload a CSV file first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("85");
        dialog.setTitle("Temperature Threshold");
        dialog.setHeaderText("Find days above a temperature threshold");
        dialog.setContentText("Temperature in °F:");

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        try {
            double threshold = Double.parseDouble(result.get().strip());

            if (!Double.isFinite(threshold)) {
                throw new NumberFormatException();
            }

            List<WeatherRecord> matchingDays = weatherData.stream()
                    .filter(record -> record.temperature() > threshold)
                    .toList();

            String details = matchingDays.stream()
                    .map(record -> "%s — %.1f °F"
                            .formatted(record.date(), record.temperature()))
                    .collect(Collectors.joining("\n"));

            summaryTextArea.setText("""
                    TEMPERATURE THRESHOLD REPORT
                    ----------------------------
                    Threshold: Above %.1f °F
                    Matching days: %d

                    %s
                    """.formatted(threshold, matchingDays.size(),
                            matchingDays.isEmpty() ? "No matching days found." : details)
                    .stripIndent());

        } catch (NumberFormatException exception) {
            showAlert(Alert.AlertType.ERROR, "Invalid Temperature",
                    "Enter a valid numeric temperature.");
        }
    }

    /**
     * Displays a JavaFX alert for errors, warnings, or information.
     *
     * @param type the alert type
     * @param title the dialog title
     * @param message the message to display
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}