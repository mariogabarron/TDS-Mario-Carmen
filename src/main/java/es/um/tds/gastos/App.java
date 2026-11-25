package es.um.tds.gastos;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        Configuracion configuracion = new ConfiguracionMemoria();
        Configuracion.setInstancia(configuracion);
        configuracion.getViewController().inicializar(stage);

        try {
            configuracion.getViewController().mostrarGastos();
        } catch (IOException e) {
            e.printStackTrace();

            Alert aviso = new Alert(Alert.AlertType.ERROR);
            aviso.setTitle("Error al iniciar");
            aviso.setHeaderText("No se pudo cargar la pantalla de gastos.");
            aviso.setContentText(
                "Revisa la ubicación de vista-gastos.fxml. "
                + "El detalle del error aparece en la consola de Eclipse."
            );

            aviso.showAndWait();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}