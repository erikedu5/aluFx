module com.meztli.alufx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires jakarta.persistence;

    opens com.meztli.alufx to javafx.fxml;
    opens com.meztli.alufx.entities to jakarta.persistence, org.hibernate.orm.core, ALL;
    exports com.meztli.alufx;
    opens com.meztli.alufx.repository to jakarta.persistence;
    exports com.meztli.alufx.dto;
    opens com.meztli.alufx.dto to javafx.fxml;
    exports com.meztli.alufx.controller;
    opens com.meztli.alufx.controller to javafx.fxml;
}