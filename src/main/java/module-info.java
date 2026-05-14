module com.teipsum.shopcmsdesktop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.teipsum.shopcmsdesktop to javafx.fxml;
    exports com.teipsum.shopcmsdesktop;
}