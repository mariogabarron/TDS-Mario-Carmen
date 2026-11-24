package es.um.tds.gastos.controlador;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewController {

    private Stage stage;

    public void inicializar(Stage stage) {
        this.stage = stage;
    }

    public void mostrarGastos() throws IOException {

        String ruta = "/es/um/tds/gastos/vista/vista-gastos.fxml";
        URL recurso = ViewController.class.getResource(ruta);

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
}