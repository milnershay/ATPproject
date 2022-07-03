module View {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.media;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires ATPProject;

    opens View to javafx.fxml;
    exports View;
}