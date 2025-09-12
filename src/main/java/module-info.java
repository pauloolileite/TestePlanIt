module com.example.planit {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires java.sql;
    requires jdatepicker;
    requires com.formdev.flatlaf;
    requires com.formdev.flatlaf.intellijthemes;
    requires com.formdev.flatlaf.extras;

    opens com.example.planit to javafx.fxml;
    exports com.example.planit;
}