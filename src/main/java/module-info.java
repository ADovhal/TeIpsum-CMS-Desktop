module com.teipsum.shopcmsdesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.slf4j;
    requires java.net.http;
    requires java.desktop;

    opens com.teipsum.shopcmsdesktop to javafx.fxml;
    opens com.teipsum.shopcmsdesktop.ui to javafx.fxml;
    opens com.teipsum.shopcmsdesktop.auth to javafx.fxml;
    opens com.teipsum.shopcmsdesktop.auth.controller to javafx.fxml;
    opens com.teipsum.shopcmsdesktop.auth.service to javafx.fxml;
    opens com.teipsum.shopcmsdesktop.auth.model to com.fasterxml.jackson.databind;
    opens com.teipsum.shopcmsdesktop.config to javafx.fxml;
    opens com.teipsum.shopcmsdesktop.product.controller to javafx.fxml;
    opens com.teipsum.shopcmsdesktop.product.model to com.fasterxml.jackson.databind;
    opens com.teipsum.shopcmsdesktop.product.model.enums to com.fasterxml.jackson.databind;
    opens com.teipsum.shopcmsdesktop.product.service to javafx.fxml;

    exports com.teipsum.shopcmsdesktop;
    exports com.teipsum.shopcmsdesktop.ui;
    exports com.teipsum.shopcmsdesktop.auth;
    exports com.teipsum.shopcmsdesktop.auth.controller;
    exports com.teipsum.shopcmsdesktop.auth.service;
    exports com.teipsum.shopcmsdesktop.config;
    exports com.teipsum.shopcmsdesktop.http;
    exports com.teipsum.shopcmsdesktop.product.controller;
    exports com.teipsum.shopcmsdesktop.product.model;
    exports com.teipsum.shopcmsdesktop.product.model.enums;
    exports com.teipsum.shopcmsdesktop.product.service;
}