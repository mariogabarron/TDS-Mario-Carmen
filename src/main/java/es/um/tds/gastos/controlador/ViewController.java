package es.um.tds.gastos.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import es.um.tds.gastos.aplicacion.GestionGastos;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
 * Hemos centralizado la presentación de las pantallas en esta clase.
 */
public class ViewController {

    private final Stage stage;
    private final GestionGastos gestion;

    public ViewController(Stage stage, GestionGastos gestion) {
        this.stage = Objects.requireNonNull(stage);
        this.gestion = Objects.requireNonNull(gestion);
    }

    /*
     * Cargamos el recurso desde el classpath, tanto al ejecutar en Eclipse
     * como al empaquetar la aplicación. No utilizamos una ruta del disco.
     */
    public void mostrarGastos() throws IOException {
        String ruta = "/es/um/tds/gastos/vista/vista-gastos.fxml";
        URL recurso = ViewController.class.getResource(ruta);

        if (recurso == null) {
            throw new IOException("No se ha encontrado el recurso FXML: " + ruta);
        }

        FXMLLoader loader = new FXMLLoader(recurso);

        /*
         * Proporcionamos el servicio al controlador declarado en fx:controller.
         */
        loader.setControllerFactory(tipo -> {
            if (tipo == GastosController.class) {
                return new GastosController(gestion);
            }
            throw new IllegalArgumentException(
                "Controlador no configurado: " + tipo.getName()
            );
        });

        Parent raiz = loader.load();

        /*
         * Mostramos la pantalla cargada. Si ya existe una escena, sustituimos
         * su raíz y conservamos las dimensiones actuales de la ventana.
         */
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
