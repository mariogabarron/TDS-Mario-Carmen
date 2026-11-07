package es.um.tds.gastos;

import es.um.tds.gastos.aplicacion.GestionGastos;
import es.um.tds.gastos.vista.VistaGastos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        GestionGastos gestion = new GestionGastos();
        VistaGastos vista = new VistaGastos(gestion);

        Scene scene = new Scene(vista, 1000, 800);
        
        stage.setTitle("Gestión de Gastos - TDS");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}