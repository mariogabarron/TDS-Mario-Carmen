
package es.um.tds.gastos.vista;

import java.math.BigDecimal;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import es.um.tds.gastos.Configuracion;
import es.um.tds.gastos.controlador.GestionGastos;
import es.um.tds.gastos.modelo.Alerta;
import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.PeriodoAlerta;

public class ConfigurarAlertasController {

    private GestionGastos gestion;

    @FXML private TextField limite;
    @FXML private ComboBox<PeriodoAlerta> periodo;
    @FXML private CheckBox todas;
    @FXML private ComboBox<Categoria> categoria;
    @FXML private ListView<Alerta> lista;
    @FXML private Label mensaje;

    @FXML
    private void initialize() {

        gestion = Configuracion.getInstancia()
            .getGestionGastos();

        periodo.getItems().setAll(PeriodoAlerta.values());
        periodo.setValue(PeriodoAlerta.MENSUAL);

        categoria.getItems().setAll(gestion.getCategorias());
        categoria.getSelectionModel().selectFirst();

        categoria.disableProperty().bind(
            todas.selectedProperty()
        );

        actualizarAlertas();
    }

    @FXML
    private void crearAlerta() {

        try {

            if (!todas.isSelected()
                    && categoria.getValue() == null) {

                throw new IllegalArgumentException(
                    "Selecciona una categoría."
                );
            }

            BigDecimal cantidad = new BigDecimal(
                limite.getText().strip().replace(',', '.')
            );

            gestion.crearAlerta(
                cantidad,
                periodo.getValue(),
                todas.isSelected() ? null : categoria.getValue()
            );

            actualizarAlertas();

            limite.clear();
            mensaje.setText("Alerta guardada.");

        } catch (NumberFormatException e) {

            mensaje.setText(
                "Introduce un límite numérico en euros."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            mensaje.setText(e.getMessage());
        }
    }

    private void actualizarAlertas() {
        lista.getItems().setAll(gestion.getAlertas());
    }
}
