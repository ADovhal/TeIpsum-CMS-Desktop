module com.teipsum.shopcmsdesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;

    opens com.teipsum.shopcmsdesktop to javafx.fxml;
    opens com.teipsum.shopcmsdesktop.ui to javafx.fxml;
    opens com.teipsum.shopcmsdesktop.config to javafx.fxml;

    exports com.teipsum.shopcmsdesktop;
    exports com.teipsum.shopcmsdesktop.ui;
    exports com.teipsum.shopcmsdesktop.config;
}