module View {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires ATP.Project.PartB;

    opens View to javafx.fxml;
    exports View;
}