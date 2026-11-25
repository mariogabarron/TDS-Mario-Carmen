
package es.um.tds.gastos.controlador;

import javafx.fxml.FXML;
import es.um.tds.gastos.Configuracion;

public final class AlertasController {

    @FXML
    private void configurar() {

        Configuracion.getInstancia()
            .getViewController()
            .mostrarConfiguracionAlertas();
    }

    @FXML
    private void mostrarHistorial() {

        Configuracion.getInstancia()
            .getViewController()
            .mostrarHistorialAlertas();
    }
}
