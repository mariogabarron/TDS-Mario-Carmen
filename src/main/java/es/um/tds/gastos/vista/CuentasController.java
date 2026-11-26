package es.um.tds.gastos.vista;

import es.um.tds.gastos.Configuracion;
import es.um.tds.gastos.controlador.GestionCuentas;
import es.um.tds.gastos.modelo.CuentaCompartida;
import es.um.tds.gastos.modelo.Reparto;
import es.um.tds.gastos.persistencia.ErrorPersistenciaException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CuentasController {
    @FXML private ComboBox<CuentaCompartida> cuenta;
    @FXML private ComboBox<String> pagador;
    @FXML private ListView<String> saldos, lista;
    @FXML private TextField importe, nombreCuenta, persona, porcentaje;
    @FXML private CheckBox porcentual;
    @FXML private Tab consulta;
    @FXML private Label mensaje;

    private final Map<String, BigDecimal> integrantes = new LinkedHashMap<>();
    private final GestionCuentas gestion = Configuracion.getInstancia().getGestionCuentas();

    @FXML private void initialize() {
        porcentaje.disableProperty().bind(porcentual.selectedProperty().not());
        try {
            cuenta.getItems().setAll(gestion.listar());
            cuenta.getSelectionModel().selectFirst();
            seleccionar();
        } catch (ErrorPersistenciaException e) {
            mensaje.setText(e.getMessage());
        }
    }

    @FXML private void cambiarReparto() {
        mostrarIntegrantes();
    }

    private static BigDecimal numero(String texto) {
        try {
            return new BigDecimal(texto.strip().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Introduce un valor numérico.");
        }
    }

    @FXML private void anadir() {
        try {
            String nombre = persona.getText().strip();
            if (nombre.isEmpty() || integrantes.containsKey(nombre)) {
                throw new IllegalArgumentException("Introduce un nombre distinto para cada integrante.");
            }
            var peso = porcentual.isSelected() ? numero(porcentaje.getText()) : BigDecimal.ONE;
            if (peso.signum() < 0 || peso.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
            }
            integrantes.put(nombre, peso);
            mostrarIntegrantes();
            persona.clear();
            porcentaje.clear();
            mensaje.setText("");
        } catch (IllegalArgumentException e) {
            mensaje.setText(e.getMessage());
        }
    }

    @FXML private void quitar() {
        int indice = lista.getSelectionModel().getSelectedIndex();
        if (indice >= 0) {
            integrantes.remove(integrantes.keySet().stream().skip(indice).findFirst().orElseThrow());
            mostrarIntegrantes();
        }
    }

    private void mostrarIntegrantes() {
        porcentual.setDisable(!integrantes.isEmpty());
        lista.getItems().clear();
        integrantes.forEach((nombre, peso) -> lista.getItems().add(
            porcentual.isSelected() ? nombre + " · " + peso.toPlainString() + " %" : nombre));
    }

    @FXML private void crear() {
        try {
            var reparto = porcentual.isSelected() ? Reparto.PORCENTUAL : Reparto.EQUITATIVO;
            var nueva = gestion.crearCuenta(nombreCuenta.getText(), reparto, integrantes);
            integrantes.clear();
            nombreCuenta.clear();
            porcentual.setSelected(false);
            mostrarIntegrantes();
            cuenta.getItems().add(nueva);
            cuenta.setValue(nueva);
            consulta.getTabPane().getSelectionModel().select(consulta);
        } catch (IllegalArgumentException | ErrorPersistenciaException e) {
            mensaje.setText(e.getMessage());
        }
    }

    @FXML private void seleccionar() {
        pagador.getItems().clear();
        saldos.getItems().clear();
        if (cuenta.getValue() != null) {
            pagador.getItems().setAll(cuenta.getValue().integrantes().keySet());
            pagador.getSelectionModel().selectFirst();
            actualizarSaldos();
        }
    }

    @FXML private void registrar() {
        try {
            if (cuenta.getValue() == null) throw new IllegalArgumentException("Selecciona una cuenta.");
            gestion.registrarPago(cuenta.getValue(), pagador.getValue(), numero(importe.getText()));
            importe.clear();
            actualizarSaldos();
        } catch (IllegalArgumentException | ErrorPersistenciaException e) {
            mensaje.setText(e.getMessage());
        }
    }

    private void actualizarSaldos() {
        try {
            var resultado = gestion.saldos(cuenta.getValue());
            saldos.getItems().clear();
            resultado.forEach((nombre, saldo) ->
                saldos.getItems().add(nombre + " · " + (saldo.signum() > 0 ? "+" : "")
                    + saldo.toPlainString().replace('.', ',') + " €"));
            mensaje.setText("");
        } catch (ErrorPersistenciaException e) {
            mensaje.setText("No se han podido consultar los saldos.");
        }
    }
}
