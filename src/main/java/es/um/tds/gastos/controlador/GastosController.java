package es.um.tds.gastos.controlador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import es.um.tds.gastos.aplicacion.GestionGastos;
import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.FiltroGastos;
import es.um.tds.gastos.modelo.Gasto;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

/*
 * Hemos separado el comportamiento de la pantalla de su estructura visual.
 * Recibimos desde el FXML los controles y atendemos sus eventos, mientras
 * delegamos las operaciones de gestión en GestionGastos.
 */
public class GastosController {

    private final GestionGastos gestion;

    /*
     * Referenciamos los controles creados por FXMLLoader mediante sus fx:id.
     * No los construimos aquí: utilizamos @FXML para recibir esas instancias.
     */
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

    /* Distinguimos el alta de la edición mediante la referencia al gasto. */
    private Gasto gastoEnEdicion;

    private final NumberFormat formatoMoneda =
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-ES"));

    /* Conservamos las opciones dinámicas y el filtro actualmente aplicado. */
    private final Map<Month, CheckMenuItem> opcionesMeses = new LinkedHashMap<>();
    private final Map<Categoria, CheckMenuItem> opcionesCategorias = new LinkedHashMap<>();
    private FiltroGastos filtroActivo = FiltroGastos.sinRestricciones();

    /*
     * Recibimos el mismo servicio que conserva ViewController. En el constructor
     * no accedemos a los controles porque todavía no se ha cargado el FXML.
     */
    public GastosController(GestionGastos gestion) {
        this.gestion = Objects.requireNonNull(gestion);
    }

    /*
     * Inicializamos el comportamiento cuando FXMLLoader ya ha inyectado los
     * controles. Las dimensiones, los textos y la distribución están en el FXML.
     */
    @FXML
    private void initialize() {
        configurarTabla();
        configurarComportamiento();
        configurarFiltros();
        campoCategoria.getItems().setAll(gestion.getCategorias());
        prepararNuevoGasto();
        actualizarTabla();
    }

    /*
     * Definimos las columnas de fecha, categoría e importe y especificamos
     * cómo obtenemos y representamos los valores de cada gasto.
     */
    private void configurarTabla() {
        /*
         * Adaptamos la fecha de cada gasto al valor observable que necesita la tabla.
         */
        columnaFecha.setCellValueFactory(datos ->
            new ReadOnlyObjectWrapper<>(
                datos.getValue().getFecha()
            )
        );

        DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

        /*
         * Personalizamos la representación de las fechas. Limpiamos el texto de
         * las celdas vacías para evitar que conserven datos al reutilizarse.
         */
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

        columnaCategoria.setCellValueFactory(datos ->
            new ReadOnlyStringWrapper(
                datos.getValue().getCategoria().getNombre()
            )
        );

        columnaImporte.setCellValueFactory(datos ->
            new ReadOnlyObjectWrapper<>(
                datos.getValue().getImporte()
            )
        );

        /*
         * Escribimos el importe en notación decimal y añadimos el símbolo del euro.
         * También retiramos el texto cuando la celda deja de contener un valor.
         */
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
    }

    /*
     * Vinculamos la selección con los botones y sincronizamos los mensajes
     * de los gráficos. Al salir de la tabla retiramos la selección visible.
     */
    private void configurarComportamiento() {
        botonEditar.disableProperty().bind(
            tabla.getSelectionModel().selectedItemProperty().isNull()
        );
        botonEliminar.disableProperty().bind(
            tabla.getSelectionModel().selectedItemProperty().isNull()
        );
        mensajeBarras.visibleProperty().bind(graficoBarras.visibleProperty().not());
        mensajeBarras.managedProperty().bind(mensajeBarras.visibleProperty());
        mensajeCircular.visibleProperty().bind(graficoCircular.visibleProperty().not());
        mensajeCircular.managedProperty().bind(mensajeCircular.visibleProperty());
        pestanas.getSelectionModel().selectedItemProperty().addListener(
            (observable, anterior, actual) -> {
                if (actual != pestanaTabla) {
                    tabla.getSelectionModel().clearSelection();
                }
            }
        );
    }

    /*
     * Generamos las opciones que dependen de los meses y las categorías.
     * Hemos declarado los menús y los botones que las contienen en el FXML.
     */
    private void configurarFiltros() {
        for (Month mes : Month.values()) {
            String nombre = mes.getDisplayName(
                TextStyle.FULL, Locale.forLanguageTag("es-ES")
            );
            CheckMenuItem opcion = new CheckMenuItem(nombre);
            opcionesMeses.put(mes, opcion);
            menuMeses.getItems().add(opcion);
        }
        actualizarCategoriasFiltro();
        menuCategoriasFiltro.setOnShowing(evento -> actualizarCategoriasFiltro());
    }

    /*
     * Leemos los datos del formulario y solicitamos el alta o la modificación
     * según exista un gasto en edición. Mostramos los errores de conversión
     * y validación sin completar la limpieza del formulario cuando se producen.
     */
    @FXML
    private void guardarGasto() {
        try {
            /*
             * Eliminamos los espacios exteriores y normalizamos la coma decimal
             * antes de construir el importe a partir del texto.
             */
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
                "Introduce un importe válido, por ejemplo 23,50."
            );
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /*
     * Copiamos al formulario los datos del gasto seleccionado y conservamos
     * su referencia para que el siguiente guardado solicite una modificación.
     */
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

    /*
     * Restablecemos el formulario para registrar un gasto nuevo. Cancelamos
     * la referencia de edición, limpiamos la selección y proponemos la fecha actual.
     */
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

    /*
     * Solicitamos confirmación antes de eliminar el gasto seleccionado.
     * Solo ejecutamos el borrado cuando recibimos una respuesta afirmativa.
     */
    @FXML
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

        /*
         * Interpretamos el cierre del diálogo o cualquier respuesta distinta de OK
         * como una cancelación del borrado.
         */
        boolean confirmado = confirmacion.showAndWait()
            .filter(respuesta -> respuesta == ButtonType.OK)
            .isPresent();

        if (confirmado) {
            gestion.eliminarGasto(seleccionado);
            prepararNuevoGasto();
            actualizarTabla();
        }
    }

    /*
     * Solicitamos el nombre de una categoría y delegamos su creación en el
     * servicio. Si la operación termina correctamente, renovamos el desplegable
     * y seleccionamos la categoría creada.
     */
    @FXML
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

    /*
     * Consultamos los gastos que cumplen el filtro activo y utilizamos esa
     * misma lista para renovar la tabla, calcular el total y actualizar los gráficos.
     */
    private void actualizarTabla() {
        List<Gasto> gastos =
            gestion.filtrarGastos(filtroActivo);

        /*
         * Sumamos con BigDecimal únicamente los importes incluidos en la consulta
         * para mantener la precisión decimal del total mostrado.
         */
        BigDecimal total = gastos.stream()
            .map(Gasto::getImporte)
            .reduce(
                new BigDecimal("0.00"),
                BigDecimal::add
            );

        tabla.getItems().setAll(gastos);
        tabla.refresh();

        tabla.setPlaceholder(
            new Label("No hay gastos que coincidan con la consulta.")
        );

        etiquetaTotal.setText(
            "Total mostrado: "
                + formatoMoneda.format(total)
                + " | Gastos mostrados: "
                + gastos.size()
        );

        actualizarGraficos(gastos);
    }

    /*
     * Presentamos el mensaje de error mediante un diálogo y esperamos
     * a que el usuario lo cierre antes de continuar.
     */
    private void mostrarError(String mensaje) {
        Alert aviso = new Alert(Alert.AlertType.ERROR);
        aviso.setTitle("Revisa los datos");
        aviso.setHeaderText("No se pudo completar la operación");
        aviso.setContentText(mensaje);
        aviso.showAndWait();
    }

    /*
     * Solicitamos los importes acumulados por categoría y reconstruimos ambas
     * representaciones a partir de los gastos recibidos, respetando así el filtrado.
     */
    private void actualizarGraficos(List<Gasto> gastos) {
        Map<Categoria, BigDecimal> totales =
            gestion.calcularTotalesPorCategoria(gastos);

        boolean hayDatos = !totales.isEmpty();

        graficoBarras.setVisible(hayDatos);
        graficoCircular.setVisible(hayDatos);

        /*
         * Retiramos las representaciones anteriores para que las sucesivas
         * actualizaciones no acumulen barras ni sectores duplicados.
         */
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

        /*
         * Ordenamos las categorías de mayor a menor importe y resolvemos
         * los empates mediante su nombre.
         */
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

            /*
             * Convertimos el importe a double únicamente para su representación
             * gráfica; conservamos BigDecimal en los cálculos monetarios.
             */
            serie.getData().add(
                new XYChart.Data<>(
                    importe.doubleValue(),
                    nombre
                )
            );

            /*
             * Calculamos el porcentaje sobre el total de la consulta y lo redondeamos
             * a una cifra decimal para mostrarlo en la etiqueta. Construimos los
             * sectores con los importes, no con estos porcentajes redondeados.
             */
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

    /*
     * Incorporamos las categorías que todavía no tienen una opción de filtrado.
     * Conservamos las casillas existentes y sus selecciones.
     */
    private void actualizarCategoriasFiltro() {
        for (Categoria categoria : gestion.getCategorias()) {
            if (!opcionesCategorias.containsKey(categoria)) {
                CheckMenuItem opcion = new CheckMenuItem(
                    categoria.getNombre()
                );

                opcionesCategorias.put(categoria, opcion);
                menuCategoriasFiltro.getItems().add(opcion);
            }
        }
    }

    /*
     * Recogemos las selecciones y construimos un filtro nuevo. Lo activamos
     * tras su construcción y actualizamos la consulta. Si la validación del
     * constructor falla, conservamos el filtro aplicado anteriormente.
     */
    @FXML
    private void aplicarFiltros() {
        Set<Month> mesesSeleccionados =
            opcionesMeses.entrySet().stream()
                .filter(entrada -> entrada.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Categoria> categoriasSeleccionadas =
            opcionesCategorias.entrySet().stream()
                .filter(entrada -> entrada.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        try {
            FiltroGastos nuevoFiltro = new FiltroGastos(
                mesesSeleccionados,
                categoriasSeleccionadas,
                filtroDesde.getValue(),
                filtroHasta.getValue()
            );

            filtroActivo = nuevoFiltro;

            prepararNuevoGasto();
            actualizarTabla();

            estadoFiltros.setText(
                "Filtro aplicado: "
                    + mesesSeleccionados.size()
                    + " meses y "
                    + categoriasSeleccionadas.size()
                    + " categorías seleccionados. "
                    + "0 significa sin restricción. "
                    + "Desde: "
                    + (filtroDesde.getValue() == null
                        ? "sin límite"
                        : filtroDesde.getValue())
                    + ". Hasta: "
                    + (filtroHasta.getValue() == null
                        ? "sin límite"
                        : filtroHasta.getValue())
            );

        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /*
     * Desmarcamos las opciones, retiramos los límites de fecha y restablecemos
     * una consulta sin restricciones. Además, cancelamos la edición pendiente.
     */
    @FXML
    private void limpiarFiltros() {
        opcionesMeses.values().forEach(
            opcion -> opcion.setSelected(false)
        );

        opcionesCategorias.values().forEach(
            opcion -> opcion.setSelected(false)
        );

        filtroDesde.setValue(null);
        filtroHasta.setValue(null);

        filtroActivo = FiltroGastos.sinRestricciones();

        estadoFiltros.setText("Sin filtros");

        prepararNuevoGasto();
        actualizarTabla();
    }

}
