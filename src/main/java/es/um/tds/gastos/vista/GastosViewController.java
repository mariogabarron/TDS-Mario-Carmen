package es.um.tds.gastos.vista;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import es.um.tds.gastos.Configuracion;
import es.um.tds.gastos.controlador.GestionGastos;
import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.FiltroGastos;
import es.um.tds.gastos.modelo.Gasto;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import es.um.tds.gastos.persistencia.ErrorPersistenciaException;

public class GastosViewController {

    private GestionGastos gestion;
    private GraficosGastos graficos;
    private FiltrosGastosView filtros;

    private Gasto gastoEnEdicion;

    private FiltroGastos filtroActivo =
        FiltroGastos.sinRestricciones();

    private final NumberFormat formatoMoneda =
        NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("es-ES")
        );

    private static final Logger logger = LogManager.getLogger(GastosViewController.class);
    
    @FXML
    private void mostrarCuentas() {
        SceneManager.getInstancia().mostrarCuentas();
    }

    @FXML private TableView<Gasto> tabla;
    @FXML private TableColumn<Gasto, LocalDate> columnaFecha;
    @FXML private TableColumn<Gasto, String> columnaCategoria;
    @FXML private TableColumn<Gasto, BigDecimal> columnaImporte;

    @FXML private TextField campoImporte;
    @FXML private DatePicker campoFecha;
    @FXML private ComboBox<Categoria> campoCategoria;

    @FXML private Label modoFormulario;
    @FXML private Label etiquetaTotal;
    @FXML private Button botonGuardar;
    @FXML private Button botonEditar;
    @FXML private Button botonEliminar;

    @FXML private TabPane pestanas;
    @FXML private Tab pestanaTabla;

    @FXML private BarChart<Number, String> graficoBarras;
    @FXML private PieChart graficoCircular;
    @FXML private Label mensajeBarras;
    @FXML private Label mensajeCircular;

    @FXML private MenuButton menuMeses;
    @FXML private MenuButton menuCategoriasFiltro;
    @FXML private DatePicker filtroDesde;
    @FXML private DatePicker filtroHasta;
    @FXML private Label estadoFiltros;

    @FXML
    private void initialize() {

        gestion = Configuracion.getInstancia().getGestionGastos();

        TablaGastos.configurar(
            columnaFecha,
            columnaCategoria,
            columnaImporte
        );

        graficos = new GraficosGastos(
            graficoBarras,
            graficoCircular,
            mensajeBarras,
            mensajeCircular
        );

        filtros = new FiltrosGastosView(
            menuMeses,
            menuCategoriasFiltro,
            filtroDesde,
            filtroHasta,
            estadoFiltros
        );

        configurarComportamiento();
        actualizarCategorias();
        prepararNuevoGasto();
        actualizarTabla();
    }

    private void configurarComportamiento() {

        botonEditar.disableProperty().bind(
            tabla.getSelectionModel()
                .selectedItemProperty().isNull()
        );

        botonEliminar.disableProperty().bind(
            tabla.getSelectionModel()
                .selectedItemProperty().isNull()
        );

        pestanas.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, anterior, actual) -> {

                if (actual != pestanaTabla) {
                    tabla.getSelectionModel().clearSelection();
                }
            });
    }

    @FXML
    private void guardarGasto() {

        try {
            BigDecimal importe = new BigDecimal(
                campoImporte.getText().strip().replace(',', '.')
            );

            if (gastoEnEdicion == null) {

                gestion.registrarGasto(
                    importe,
                    campoFecha.getValue(),
                    campoCategoria.getValue()
                );

            } else {

                gestion.modificarGasto(
                    gastoEnEdicion,
                    importe,
                    campoFecha.getValue(),
                    campoCategoria.getValue()
                );
            }

            prepararNuevoGasto();
            actualizarTabla();

        } catch (NumberFormatException e) {
            mostrarError(
                "Introduce un importe válido, por ejemplo 23,50."
            );

        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());

        } catch (ErrorPersistenciaException e) {
            logger.error("Error de persistencia al gestionar gastos", e);
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void cargarGastoSeleccionado() {

        Gasto seleccionado =
            tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            return;
        }

        gastoEnEdicion = seleccionado;

        campoImporte.setText(
            seleccionado.getImporte().toPlainString()
        );

        campoFecha.setValue(seleccionado.getFecha());
        campoCategoria.setValue(seleccionado.getCategoria());

        modoFormulario.setText("Editando gasto seleccionado");
        botonGuardar.setText("Guardar cambios");
    }

    @FXML
    private void prepararNuevoGasto() {

        gastoEnEdicion = null;

        campoImporte.clear();
        campoFecha.setValue(LocalDate.now());
        campoCategoria.getSelectionModel().selectFirst();

        tabla.getSelectionModel().clearSelection();

        modoFormulario.setText("Nuevo gasto");
        botonGuardar.setText("Añadir gasto");
    }

    @FXML
    private void eliminarGasto() {

        Gasto seleccionado =
            tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            return;
        }

        Alert confirmacion =
            new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Eliminar gasto");
        confirmacion.setHeaderText("¿Eliminar este gasto?");
        confirmacion.setContentText(seleccionado.toString());

        boolean confirmado = confirmacion.showAndWait()
            .filter(respuesta -> respuesta == ButtonType.OK)
            .isPresent();

        if (confirmado) {

            try {
                gestion.eliminarGasto(seleccionado);
                prepararNuevoGasto();
                actualizarTabla();

            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());

            } catch (ErrorPersistenciaException e) {
                logger.error("Error de persistencia al gestionar gastos", e);
                mostrarError(e.getMessage());
            }
        }
    }

    @FXML
    private void crearCategoria() {

        TextInputDialog dialogo = new TextInputDialog();

        dialogo.setTitle("Nueva categoría");
        dialogo.setHeaderText("Crear una categoría");
        dialogo.setContentText("Nombre:");

        dialogo.showAndWait().ifPresent(nombre -> {

            try {
                Categoria nueva = gestion.crearCategoria(nombre);

                actualizarCategorias();
                campoCategoria.setValue(nueva);

            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());

            } catch (ErrorPersistenciaException e) {
                logger.error("Error de persistencia al gestionar gastos", e);
                mostrarError(e.getMessage());
            }
        });
    }

    private void actualizarCategorias() {

        List<Categoria> categorias = gestion.getCategorias();

        campoCategoria.getItems().setAll(categorias);
        filtros.actualizarCategorias(categorias);
    }

    @FXML
    private void aplicarFiltros() {

        try {
            filtroActivo = filtros.leerFiltro();

            prepararNuevoGasto();
            actualizarTabla();

        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());

        } catch (ErrorPersistenciaException e) {
            logger.error("Error de persistencia al gestionar gastos", e);
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void limpiarFiltros() {
        try {
            filtros.limpiar();
            filtroActivo = FiltroGastos.sinRestricciones();

            prepararNuevoGasto();
            actualizarTabla();

        } catch (ErrorPersistenciaException e) {
            logger.error("Error al limpiar los filtros", e);
            mostrarError(e.getMessage());
        }
    }

    private void actualizarTabla() {

        List<Gasto> gastos =
            gestion.filtrarGastos(filtroActivo);

        BigDecimal total = gestion.calcularTotal(gastos);

        tabla.getItems().setAll(gastos);
        tabla.refresh();

        etiquetaTotal.setText(
            "Total mostrado: " + formatoMoneda.format(total)
            + " | Gastos mostrados: " + gastos.size()
        );

        graficos.actualizar(
            gestion.calcularTotalesPorCategoria(gastos),
            total
        );
    }

    private void mostrarError(String mensaje) {

        Alert aviso = new Alert(Alert.AlertType.ERROR);

        aviso.setTitle("Revisa los datos");
        aviso.setHeaderText("No se pudo completar la operación");
        aviso.setContentText(mensaje);

        aviso.showAndWait();
    }
}