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

import es.um.tds.gastos.modelo.FiltroGastos;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * Hemos organizado la interfaz de gestión de gastos mediante un BorderPane.
 * Presentamos el formulario, la tabla, los gráficos y los controles de filtrado,
 * y delegamos las operaciones de gestión en la clase GestionGastos.
 */
public class VistaGastos extends BorderPane {

    /*
     * Conservamos la referencia al servicio que recibe las operaciones solicitadas
     * desde la interfaz y proporciona los datos que presentamos.
     */
    private final GestionGastos gestion;

    /*
     * Declaramos los controles con los que mostramos los gastos y recogemos
     * el importe, la fecha y la categoría del formulario.
     */
    private final TableView<Gasto> tabla = new TableView<>();
    private final TextField campoImporte = new TextField();
    private final DatePicker campoFecha =
        new DatePicker(LocalDate.now());
    private final ComboBox<Categoria> campoCategoria =
        new ComboBox<>();

    private final Label modoFormulario = new Label("Nuevo gasto");
    private final Label etiquetaTotal = new Label();

    private final Button botonGuardar = new Button("Añadir gasto");

    /*
     * Utilizamos esta referencia para distinguir el alta de la edición.
     * Cuando su valor es null, registramos un gasto nuevo.
     */
    private Gasto gastoEnEdicion;
    
    /*
     * Agrupamos las representaciones en pestañas y configuramos las barras
     * con importes en el eje horizontal y categorías en el eje vertical.
     */
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

    /*
     * Hemos elegido el formato monetario de España para presentar los importes
     * con su símbolo de moneda y sus separadores correspondientes.
     */
    private final NumberFormat formatoMoneda =
    	NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("es-ES")
        );
    private final MenuButton menuMeses =
    	new MenuButton("Meses");

    private final MenuButton menuCategoriasFiltro =
    	new MenuButton("Categorías");

    /*
     * Relacionamos cada mes y categoría con su casilla de selección.
     * Con LinkedHashMap conservamos el orden de incorporación de las opciones.
     */
    private final Map<Month, CheckMenuItem> opcionesMeses =
    	new LinkedHashMap<>();

    private final Map<Categoria, CheckMenuItem> opcionesCategorias =
    	new LinkedHashMap<>();

    private final DatePicker filtroDesde = new DatePicker();
    private final DatePicker filtroHasta = new DatePicker();

    private final Label estadoFiltros =
    	new Label("Sin filtros");

    /*
     * Mantenemos separado el filtro aplicado de las selecciones de los controles.
     * Inicialmente consultamos los gastos sin restricciones.
     */
    private FiltroGastos filtroActivo =
    	FiltroGastos.sinRestricciones();

    /*
     * Recibimos el servicio de gestión y construimos las distintas zonas de la
     * ventana. Cargamos las categorías disponibles y presentamos la consulta inicial.
     */
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
        cabecera.getChildren().add(crearPanelFiltros());
        cabecera.setPadding(new Insets(0, 0, 15, 0));

        setTop(cabecera);

        campoCategoria.getItems().setAll(
            gestion.getCategorias()
        );

        prepararNuevoGasto();
        actualizarTabla();
    }

    /*
     * Definimos las columnas de fecha, categoría e importe y especificamos
     * cómo obtenemos y representamos los valores de cada gasto.
     */
    private void configurarTabla() {
        TableColumn<Gasto, LocalDate> columnaFecha =
            new TableColumn<>("Fecha");

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

    /*
     * Organizamos los campos y botones del formulario y vinculamos sus eventos
     * con las operaciones de alta, edición, borrado y creación de categorías.
     */
    private void configurarFormulario() {
        campoImporte.setPromptText("Ejemplo: 12,50");

        campoCategoria.setMaxWidth(Double.MAX_VALUE);

        /* Hemos limitado la entrada de la fecha al selector de calendario del campo. */
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

        /*
         * Vinculamos la disponibilidad de editar y eliminar con la selección
         * de la tabla, de modo que ambos botones se desactivan si no hay una fila elegida.
         */
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

    /*
     * Leemos los datos del formulario y solicitamos el alta o la modificación
     * según exista un gasto en edición. Mostramos los errores de conversión
     * y validación sin completar la limpieza del formulario cuando se producen.
     */
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
     * Configuramos los ejes, los gráficos y las pestañas de consulta.
     * También vinculamos los mensajes informativos con la ausencia de datos.
     */
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

        /*
         * Mostramos los mensajes cuando ocultamos los gráficos por falta de datos.
         * Sincronizamos también la participación de los mensajes en la distribución visual.
         */
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

        /*
         * Limpiamos la selección al abandonar la tabla para no mantener habilitadas
         * las acciones de edición y borrado sobre una fila que ha quedado oculta.
         */
        pestanas.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, anterior, actual) -> {
                if (actual != pestañaTabla) {
                    tabla.getSelectionModel().clearSelection();
                }
            });

        setCenter(pestanas);
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
    
    /*
     * Construimos los controles de selección múltiple y los límites temporales.
     * Asociamos los botones con la aplicación y la limpieza de los filtros.
     */
    private VBox crearPanelFiltros() {
        for (Month mes : Month.values()) {
            String nombre = mes.getDisplayName(
                TextStyle.FULL,
                Locale.forLanguageTag("es-ES")
            );

            CheckMenuItem opcion = new CheckMenuItem(nombre);

            opcionesMeses.put(mes, opcion);
            menuMeses.getItems().add(opcion);
        }

        actualizarCategoriasFiltro();

        /*
         * Revisamos las categorías al abrir el menú para incluir las que se hayan
         * creado desde el formulario sin perder las selecciones anteriores.
         */
        menuCategoriasFiltro.setOnShowing(
            evento -> actualizarCategoriasFiltro()
        );

        filtroDesde.setPromptText("Sin límite inicial");
        filtroHasta.setPromptText("Sin límite final");

        /*
         * Seleccionamos los límites temporales mediante los calendarios de los
         * campos y deshabilitamos la escritura directa de sus fechas.
         */
        filtroDesde.setEditable(false);
        filtroHasta.setEditable(false);

        Button aplicar = new Button("Aplicar filtros");
        Button limpiar = new Button("Limpiar filtros");

        aplicar.setOnAction(evento -> aplicarFiltros());
        limpiar.setOnAction(evento -> limpiarFiltros());

        HBox selecciones = new HBox(
            10,
            menuMeses,
            menuCategoriasFiltro,
            aplicar,
            limpiar
        );

        HBox fechas = new HBox(
            10,
            new Label("Desde:"),
            filtroDesde,
            new Label("Hasta:"),
            filtroHasta
        );

        return new VBox(
            8,
            new Label("Filtrar gastos"),
            selecciones,
            fechas,
            estadoFiltros
        );
    }
    
}