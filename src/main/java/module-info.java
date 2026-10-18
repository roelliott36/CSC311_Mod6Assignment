module org.example.csc311_mod6assignment {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.csc311_mod6assignment to javafx.fxml;
    exports org.example.csc311_mod6assignment;
}