package org.example.csc311_mod6assignment;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class dashboardController {

    @FXML private ImageView dashboardBackground;

    @FXML private TableView<?> importedDataViewTable;
    @FXML private TableColumn<?, ?> importedDataDateView;
    @FXML private TableColumn<?, ?> importedDataTempView;
    @FXML private TableColumn<?, ?> importedDataHumidityView;
    @FXML private TableColumn<?, ?> importedDataPrecipitationView;

    @FXML private TextArea summaryTextArea;

    @FXML private TableView<?> rankedViewTable;
    @FXML private TableColumn<?, ?> rankedDataDateView;
    @FXML private TableColumn<?, ?> rankedDataTempView;
    @FXML private TableColumn<?, ?> rankedDataHumidityView;
    @FXML private TableColumn<?, ?> rankedDataPrecipitationView;

    @FXML private Label importedDataViewLabel;
    @FXML private MenuButton selectRankingMenuDropdown;
    @FXML private MenuItem top5HottestDays;
    @FXML private MenuItem top5ColdestDays;

    @FXML private Button applyRankingButton;
    @FXML private Button uploadFileButton;

    @FXML private TextField fileNameTextDisplay;
    @FXML private TextField dateRangeTextDisplay;

    @FXML private Label avgTempLabel;
    @FXML private Label rainyDaysLabel;
    @FXML private Label avgHumidityLabel;
    @FXML private Label totalDaysLabel;

    @FXML private Label avgTempNumberDisplay;
    @FXML private Label tempBasedOnDataLabel;
    @FXML private Label numDaysNumberDisplay;
    @FXML private Label rainyBasedOnDataLabel;
    @FXML private Label avgHumidityPercentDisplay;
    @FXML private Label humidityBasedOnDataLabel;
    @FXML private Label totalDaysNumDisplay;
    @FXML private Label totalDaysBasedOnDataLabel;

    @FXML private Label summaryLabel;
    @FXML private PieChart tempCategoriesPieChart;
    @FXML private Label hotLabel;
    @FXML private Label warmLabel;
    @FXML private Label coldLabel;

    @FXML private BarChart<?, ?> precipitationVsHumidity;

    @FXML
    private void initialize() {
    }
}
