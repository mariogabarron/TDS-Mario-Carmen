package es.um.tds.gastos.vista;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import es.um.tds.gastos.aplicacion.GestionGastos;
import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.Gasto;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

public class VistaGastos extends BorderPane {

    private final GestionGastos gestion;

    private final TableView<Gasto> tabla = new TableView<>();
    private final TextField campoImporte = new TextField();
    private final DatePicker campoFecha =
        new DatePicker(LocalDate.now());
    private final ComboBox<Categoria> campoCategoria =
        new ComboBox<>();

    private final Label modoFormulario = new Label("Nuevo gasto");
    private final Label etiquetaTotal = new Label();

    private final Button botonGuardar = new Button("Añadir gasto");

    private Gasto gastoEnEdicion;
    
    private final TabPane pestanas = new TabPane();

    private final NumberAxis ejeImportes = new NumberAxis();
    private final CategoryAxis ejeCategorias = new CategoryAxis();

    private final BarChart<Number, String> graficoBarras =
        new BarChart<>(ejeImportes, ejeCategorias);

    private final PieChart graficoCircular = new PieChart();

    private final Label mensajeBarras =
        new Label("Añade gastos para ver su distribución.");

    private final Label mensajeCircular =
        new Label("Añade gastos para ver su distribución.");

    private final NumberFormat formatoMoneda =
        NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("es-ES")
        );

    public VistaGastos(GestionGastos gestion) {
        this.gestion = gestion;

        setPadding(new Insets(20));

        configurarTabla();
        configurarFormulario();
        configurarVisualizaciones();

        Label titulo = new Label("Gestión de gastos");
        titulo.setStyle(
            "-fx-font-size: 24px; -fx-font-weight: bold;"
        );

        VBox cabecera = new VBox(8, titulo, etiquetaTotal);
        cabecera.setPadding(new Insets(0, 0, 15, 0));

        setTop(cabecera);

        campoCategoria.getItems().setAll(
            gestion.getCategorias()
        );

        prepararNuevoGasto();
        actualizarTabla();
    }

    private void configurarTabla() {
        TableColumn<Gasto, LocalDate> columnaFecha =
            new TableColumn<>("Fecha");

        columnaFecha.setCellValueFactory(datos ->
            new ReadOnlyObjectWrapper<>(
                datos.getValue().getFecha()
            )
        );

        DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

        columnaFecha.setCellFactory(columna ->
            new TableCell<Gasto, LocalDate>() {
                @Override
                protected void updateItem(LocalDate fecha,
                                          boolean vacia) {
                    super.updateItem(fecha, vacia);

                    setText(vacia || fecha == null
                        ? null
                        : fecha.format(formatoFecha));
                }
            }
        );

        TableColumn<Gasto, String> columnaCategoria =
            new TableColumn<>("Categoría");

        columnaCategoria.setCellValueFactory(datos ->
            new ReadOnlyStringWrapper(
                datos.getValue().getCategoria().getNombre()
            )
        );

        TableColumn<Gasto, BigDecimal> columnaImporte =
            new TableColumn<>("Importe");

        columnaImporte.setCellValueFactory(datos ->
            new ReadOnlyObjectWrapper<>(
                datos.getValue().getImporte()
            )
        );

        columnaImporte.setCellFactory(columna ->
            new TableCell<Gasto, BigDecimal>() {
                @Override
                protected void updateItem(BigDecimal importe,
                                          boolean vacia) {
                    super.updateItem(importe, vacia);

                    setText(vacia || importe == null
                        ? null
                        : importe.toPlainString() + " €");
                }
            }
        );

        columnaFecha.setPrefWidth(150);
        columnaCategoria.setPrefWidth(250);
        columnaImporte.setPrefWidth(150);

        tabla.getColumns().add(columnaFecha);
        tabla.getColumns().add(columnaCategoria);
        tabla.getColumns().add(columnaImporte);

        tabla.setPlaceholder(
            new Label("Todavía no hay gastos registrados.")
        );
    }

    private void configurarFormulario() {
        campoImporte.setPromptText("Ejemplo: 12,50");

        campoCategoria.setMaxWidth(Double.MAX_VALUE);

        // Por ahora la fecha se elige desde el calendario.
        campoFecha.setEditable(false);

        Button botonCategoria = new Button("Nueva categoría");
        botonCategoria.setOnAction(event -> crearCategoria());

        GridPane campos = new GridPane();
        campos.setHgap(10);
        campos.setVgap(10);

        campos.addRow(0, new Label("Importe (€):"), campoImporte);
        campos.addRow(1, new Label("Fecha:"), campoFecha);
        campos.addRow(
            2,
            new Label("Categoría:"),
            campoCategoria,
            botonCategoria
        );

        Button botonNuevo = new Button("Nuevo / cancelar edición");
        Button botonEditar = new Button("Editar seleccionado");
        Button botonEliminar = new Button("Eliminar seleccionado");

        botonGuardar.setOnAction(event -> guardarGasto());
        botonNuevo.setOnAction(event -> prepararNuevoGasto());
        botonEditar.setOnAction(event -> cargarGastoSeleccionado());
        botonEliminar.setOnAction(event -> eliminarGasto());

        botonEditar.disableProperty().bind(
            tabla.getSelectionModel()
                .selectedItemProperty()
                .isNull()
        );

        botonEliminar.disableProperty().bind(
            tabla.getSelectionModel()
                .selectedItemProperty()
                .isNull()
        );

        HBox acciones = new HBox(
            10, botonGuardar, botonNuevo
        );
        acciones.setAlignment(Pos.CENTER_LEFT);

        HBox accionesTabla = new HBox(
            10, botonEditar, botonEliminar
        );

        VBox panel = new VBox(
            12, accionesTabla, modoFormulario, campos, acciones
        );
        panel.setPadding(new Insets(15, 0, 0, 0));

        setBottom(panel);
    }

    private void guardarGasto() {
        try {
            String textoImporte = campoImporte.getText()
                .strip()
                .replace(',', '.');

            BigDecimal importe = new BigDecimal(textoImporte);

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
                "Introduce un importe válido, por ejemplo 12,50."
            );
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

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

    private void prepararNuevoGasto() {
        gastoEnEdicion = null;

        campoImporte.clear();
        campoFecha.setValue(LocalDate.now());
        campoCategoria.getSelectionModel().selectFirst();

        tabla.getSelectionModel().clearSelection();

        modoFormulario.setText("Nuevo gasto");
        botonGuardar.setText("Añadir gasto");
    }

    private void eliminarGasto() {
        Gasto seleccionado =
            tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            return;
        }

        Alert confirmacion = new Alert(
            Alert.AlertType.CONFIRMATION
        );
        confirmacion.setTitle("Eliminar gasto");
        confirmacion.setHeaderText("¿Eliminar este gasto?");
        confirmacion.setContentText(seleccionado.toString());

        boolean confirmado = confirmacion.showAndWait()
            .filter(respuesta -> respuesta == ButtonType.OK)
            .isPresent();

        if (confirmado) {
            gestion.eliminarGasto(seleccionado);
            prepararNuevoGasto();
            actualizarTabla();
        }
    }

    private void crearCategoria() {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Nueva categoría");
        dialogo.setHeaderText("Crear una categoría");
        dialogo.setContentText("Nombre:");

        dialogo.showAndWait().ifPresent(nombre -> {
            try {
                Categoria nueva = gestion.crearCategoria(nombre);

                campoCategoria.getItems().setAll(
                    gestion.getCategorias()
                );
                campoCategoria.setValue(nueva);

            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            }
        });
    }

    private void actualizarTabla() {
        List<Gasto> gastos = gestion.getGastos();

        BigDecimal total = gastos.stream()
            .map(Gasto::getImporte)
            .reduce(new BigDecimal("0.00"), BigDecimal::add);

        tabla.getItems().setAll(gastos);
        tabla.refresh();

        etiquetaTotal.setText(
            "Total registrado: " + formatoMoneda.format(total)
        );

        actualizarGraficos(gastos);
    }
    
    private void mostrarError(String mensaje) {
        Alert aviso = new Alert(Alert.AlertType.ERROR);
        aviso.setTitle("Revisa los datos");
        aviso.setHeaderText("No se pudo completar la operación");
        aviso.setContentText(mensaje);
        aviso.showAndWait();
    }
    
    private void configurarVisualizaciones() {
        ejeImportes.setLabel("Importe acumulado (€)");
        ejeImportes.setForceZeroInRange(true);

        ejeCategorias.setLabel("Categoría");

        graficoBarras.setTitle("Gasto acumulado por categoría");
        graficoBarras.setLegendVisible(false);
        graficoBarras.setAnimated(false);
        graficoBarras.setCategoryGap(15);

        graficoCircular.setTitle("Distribución del gasto");
        graficoCircular.setLegendVisible(false);
        graficoCircular.setLabelsVisible(true);
        graficoCircular.setAnimated(false);

        mensajeBarras.visibleProperty().bind(
            graficoBarras.visibleProperty().not()
        );

        mensajeBarras.managedProperty().bind(
            mensajeBarras.visibleProperty()
        );

        mensajeCircular.visibleProperty().bind(
            graficoCircular.visibleProperty().not()
        );

        mensajeCircular.managedProperty().bind(
            mensajeCircular.visibleProperty()
        );

        StackPane panelBarras = new StackPane(
            graficoBarras, mensajeBarras
        );

        StackPane panelCircular = new StackPane(
            graficoCircular, mensajeCircular
        );

        Tab pestañaTabla = new Tab("Tabla de gastos", tabla);
        Tab pestañaBarras = new Tab("Barras", panelBarras);
        Tab pestañaCircular = new Tab("Circular", panelCircular);

        pestanas.getTabs().setAll(
            pestañaTabla,
            pestañaBarras,
            pestañaCircular
        );

        pestanas.setTabClosingPolicy(
            TabPane.TabClosingPolicy.UNAVAILABLE
        );

        pestanas.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, anterior, actual) -> {
                if (actual != pestañaTabla) {
                    tabla.getSelectionModel().clearSelection();
                }
            });

        setCenter(pestanas);
    }
    
    private void actualizarGraficos(List<Gasto> gastos) {
        Map<Categoria, BigDecimal> totales =
            gestion.calcularTotalesPorCategoria(gastos);

        boolean hayDatos = !totales.isEmpty();

        graficoBarras.setVisible(hayDatos);
        graficoCircular.setVisible(hayDatos);

        graficoBarras.getData().clear();
        graficoCircular.getData().clear();

        if (!hayDatos) {
            return;
        }

        BigDecimal totalGeneral = totales.values().stream()
            .reduce(new BigDecimal("0.00"), BigDecimal::add);

        XYChart.Series<Number, String> serie =
            new XYChart.Series<>();

        serie.setName("Gasto por categoría");

        List<Map.Entry<Categoria, BigDecimal>> ordenados =
            totales.entrySet().stream()
                .sorted(
                    Map.Entry
                        .<Categoria, BigDecimal>comparingByValue()
                        .reversed()
                        .thenComparing(
                            entrada -> entrada.getKey().getNombre()
                        )
                )
                .toList();

        for (Map.Entry<Categoria, BigDecimal> entrada : ordenados) {
            String nombre = entrada.getKey().getNombre();
            BigDecimal importe = entrada.getValue();

            serie.getData().add(
                new XYChart.Data<>(
                    importe.doubleValue(),
                    nombre
                )
            );

            BigDecimal porcentaje = importe
                .multiply(new BigDecimal("100"))
                .divide(
                    totalGeneral,
                    1,
                    RoundingMode.HALF_UP
                );

            String etiqueta = nombre
                + ": "
                + formatoMoneda.format(importe)
                + " ("
                + porcentaje.toPlainString().replace('.', ',')
                + " %)";

            graficoCircular.getData().add(
                new PieChart.Data(
                    etiqueta,
                    importe.doubleValue()
                )
            );
        }

        graficoBarras.getData().add(serie);
    }
}