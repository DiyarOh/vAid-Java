module com.example.vaidjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.json;
    requires java.sql;

    opens com.example.vaidjavafx to javafx.fxml;
    exports com.example.vaidjavafx;
    exports com.example.vaidjavafx.ViewControllers;
    opens com.example.vaidjavafx.ViewControllers to javafx.fxml;
    exports com.example.vaidjavafx.Utility;
    opens com.example.vaidjavafx.Utility to javafx.fxml;
}