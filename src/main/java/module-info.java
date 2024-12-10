module com.example.deansofficeapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.deansofficeapp to javafx.fxml;
    exports com.example.deansofficeapp;
}