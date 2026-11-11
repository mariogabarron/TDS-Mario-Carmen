package es.um.tds.gastos;

import java.io.IOException;

import es.um.tds.gastos.aplicacion.GestionGastos;
import es.um.tds.gastos.controlador.ViewController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/* Hemos reservado esta clase para arrancar JavaFX y conectar sus componentes. */
public class App extends Application {

    private ViewController viewController;

    @Override
    public void start(Stage stage) {
        GestionGastos gestion = new GestionGastos();
        viewController = new ViewController(stage, gestion);

        try {
            viewController.mostrarGastos();
        } catch (IOException e) {
            /* Conservamos el detalle técnico y comunicamos el fallo de carga. */
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
