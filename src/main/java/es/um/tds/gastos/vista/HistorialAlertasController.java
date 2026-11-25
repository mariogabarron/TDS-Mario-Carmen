package es.um.tds.gastos.vista;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import es.um.tds.gastos.Configuracion;
import es.um.tds.gastos.controlador.GestionGastos;
import es.um.tds.gastos.modelo.Notificacion;

public class HistorialAlertasController {

    @FXML
    private ListView<Notificacion> lista;

    @FXML
    private void initialize() {

        GestionGastos gestion = Configuracion
            .getInstancia()
            .getGestionGastos();

        lista.getItems().setAll(
            gestion.getHistorial()
        );
    }
}
