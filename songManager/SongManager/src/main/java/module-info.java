module com.songmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.songmanager to javafx.fxml;
    exports com.songmanager;
}