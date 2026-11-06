
package es.um.tds.gastos;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        Label titulo = new Label("Gestión de Gastos");

        Button boton = new Button("Añadir gasto");

        boton.setOnAction(event -> {
            System.out.println("Botón pulsado");
        });

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titulo, boton);

        Scene scene = new Scene(root, 700, 450);

        stage.setTitle("Gestión de Gastos - TDS");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
