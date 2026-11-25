package es.um.tds.gastos.controlador;

import java.math.BigDecimal;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import es.um.tds.gastos.Configuracion;
import es.um.tds.gastos.aplicacion.GestionGastos;
import es.um.tds.gastos.modelo.*;

public final class AlertasController {
    private final GestionGastos gestion = Configuracion.getInstancia().getGestionGastos();

    @FXML
    private void configurar() {
        var dialogo = new Dialog<Void>();
        dialogo.setTitle("Alertas de gasto");
        var limite = new TextField();
        limite.setPromptText("Ejemplo: 100,00");
        var periodo = new ComboBox<PeriodoAlerta>();
        periodo.getItems().setAll(PeriodoAlerta.values());
        periodo.setValue(PeriodoAlerta.MENSUAL);
        var todas = new CheckBox("Todas las categorías");
        todas.setSelected(true);
        var categoria = new ComboBox<Categoria>();
        categoria.getItems().setAll(gestion.getCategorias());
        categoria.getSelectionModel().selectFirst();
        categoria.disableProperty().bind(todas.selectedProperty());
        var lista = new ListView<Alerta>();
        lista.setPrefHeight(130);
        lista.setPlaceholder(new Label("Todavía no hay alertas configuradas."));
        lista.getItems().setAll(gestion.getAlertas());
        var mensaje = new Label();
        var guardar = new Button("Crear alerta");
        guardar.setOnAction(evento -> {
            try {
                if (!todas.isSelected() && categoria.getValue() == null) {
                    throw new IllegalArgumentException("Selecciona una categoría.");
                }
                BigDecimal cantidad = new BigDecimal(limite.getText().strip().replace(',', '.'));
                gestion.crearAlerta(cantidad, periodo.getValue(), todas.isSelected() ? null : categoria.getValue());
                lista.getItems().setAll(gestion.getAlertas());
                limite.clear();
                mensaje.setText("Alerta guardada.");
            } catch (NumberFormatException e) {
                mensaje.setText("Introduce un límite numérico en euros.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());
            }
        });
        dialogo.getDialogPane().setContent(new VBox(6, new Label("Límite (€)"), limite,
            new Label("Periodicidad"), periodo, todas, categoria, guardar, mensaje,
            new Label("Alertas configuradas"), lista));
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialogo.showAndWait();
    }

    @FXML
    private void mostrarHistorial() {
        var dialogo = new Dialog<Void>();
        dialogo.setTitle("Historial de notificaciones");
        var lista = new ListView<Notificacion>();
        lista.setPlaceholder(new Label("Todavía no se ha superado ningún límite."));
        lista.getItems().setAll(gestion.getHistorial());
        lista.setPrefSize(780, 300);
        dialogo.getDialogPane().setContent(lista);
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialogo.showAndWait();
    }
}
