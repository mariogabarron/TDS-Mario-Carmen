package es.um.tds.gastos.vista;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SceneManager {

    private Stage stage;
    private static final SceneManager instancia = new SceneManager();
    private static final Logger logger = LogManager.getLogger(SceneManager.class);

    private SceneManager() {
    }

    public static SceneManager getInstancia() {
        return instancia;
    }

    public void inicializar(Stage stage) {
        this.stage = stage;
    }

    public void mostrarGastos() throws IOException {

        String ruta = "/es/um/tds/gastos/vista/vista-gastos.fxml";
        URL recurso = SceneManager.class.getResource(ruta);

        if (recurso == null) {
            throw new IOException(
                "No se ha encontrado el recurso FXML: " + ruta
            );
        }

        FXMLLoader loader = new FXMLLoader(recurso);
        Parent raiz = loader.load();

        if (stage.getScene() == null) {
            stage.setScene(new Scene(raiz, 1000, 800));
        } else {
            stage.getScene().setRoot(raiz);
        }

        stage.setTitle("Gestión de Gastos - TDS");
        stage.setMinWidth(900);
        stage.setMinHeight(700);
        stage.show();
    }
    

	public void mostrarConfiguracionAlertas() {
	    mostrarDialogo(
	        "configurar-alertas.fxml",
	        "Alertas de gasto"
	    );
	}
	
	public void mostrarHistorialAlertas() {
	    mostrarDialogo(
	        "historial-alertas.fxml",
	        "Historial de notificaciones"
	    );
	}
	
	private void mostrarDialogo(String fichero, String titulo) {
	
	    String ruta = "/es/um/tds/gastos/vista/" + fichero;
	
	    try {
	
	        URL recurso = SceneManager.class.getResource(ruta);
	
	        if (recurso == null) {
	            throw new IOException(
	                "No se ha encontrado el recurso FXML: " + ruta
	            );
	        }
	
	        FXMLLoader loader = new FXMLLoader(recurso);
	        DialogPane contenido = loader.load();
	
	        Dialog<Void> dialogo = new Dialog<>();
	
	        dialogo.initOwner(stage);
	        dialogo.setTitle(titulo);
	        dialogo.setDialogPane(contenido);
	
	        dialogo.showAndWait();
	
	    } catch (IOException e) {

	        logger.error("No se pudo cargar el FXML: {}", fichero, e);

	        Alert aviso = new Alert(Alert.AlertType.ERROR);

	        aviso.setTitle("Error");
	        aviso.setHeaderText("No se pudo abrir la ventana");
	        aviso.setContentText(
	            "Consulta el fichero de registro para más información."
	        );

	        aviso.showAndWait();
	    }
	}

}