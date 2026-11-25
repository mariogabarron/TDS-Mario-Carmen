
package es.um.tds.gastos;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.um.tds.gastos.persistencia.ErrorPersistenciaException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class App extends Application {

    private static final Logger logger =
        LogManager.getLogger(App.class);

    @Override
    public void start(Stage stage) {

        logger.info("Iniciando Gestión de Gastos");

        try {

            Configuracion configuracion = new ConfiguracionSQLite();
            Configuracion.setInstancia(configuracion);
            configuracion.getViewController().inicializar(stage);
            configuracion.getViewController().mostrarGastos();

        } catch (IOException | ErrorPersistenciaException e) {

            logger.error("Error al iniciar la aplicación", e);
            Alert aviso = new Alert(Alert.AlertType.ERROR);
            aviso.setTitle("Error al iniciar");
            aviso.setHeaderText(
                "No se pudo iniciar la aplicación."
            );
            aviso.setContentText(
                "Consulta el fichero gestion-gastos.log."
            );
            aviso.showAndWait();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        logger.info("Aplicación finalizada");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
