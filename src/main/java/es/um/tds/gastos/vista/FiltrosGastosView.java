package es.um.tds.gastos.vista;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.FiltroGastos;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;

public class FiltrosGastosView {

    private final MenuButton menuCategorias;
    private final DatePicker desde;
    private final DatePicker hasta;
    private final Label estado;

    private final Map<Month, CheckMenuItem> opcionesMeses =
        new LinkedHashMap<>();

    private final Map<Categoria, CheckMenuItem> opcionesCategorias =
        new LinkedHashMap<>();

    public FiltrosGastosView(
            MenuButton menuMeses,
            MenuButton menuCategorias,
            DatePicker desde,
            DatePicker hasta,
            Label estado) {

        this.menuCategorias = menuCategorias;
        this.desde = desde;
        this.hasta = hasta;
        this.estado = estado;

        for (Month mes : Month.values()) {

            String nombre = mes.getDisplayName(
                TextStyle.FULL,
                Locale.forLanguageTag("es-ES")
            );

            CheckMenuItem opcion = new CheckMenuItem(nombre);

            opcionesMeses.put(mes, opcion);
            menuMeses.getItems().add(opcion);
        }
    }

    public void actualizarCategorias(List<Categoria> categorias) {

        for (Categoria categoria : categorias) {

            if (!opcionesCategorias.containsKey(categoria)) {

                CheckMenuItem opcion =
                    new CheckMenuItem(categoria.getNombre());

                opcionesCategorias.put(categoria, opcion);
                menuCategorias.getItems().add(opcion);
            }
        }
    }

    public FiltroGastos leerFiltro() {

        Set<Month> meses = opcionesMeses.entrySet().stream()
            .filter(entrada -> entrada.getValue().isSelected())
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());

        Set<Categoria> categorias =
            opcionesCategorias.entrySet().stream()
                .filter(entrada -> entrada.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        FiltroGastos filtro = new FiltroGastos(
            meses,
            categorias,
            desde.getValue(),
            hasta.getValue()
        );

        estado.setText(
            "Meses: " + (meses.isEmpty() ? "todos" : meses.size())
            + " | Categorías: "
            + (categorias.isEmpty() ? "todas" : categorias.size())
            + " | Desde: "
            + (desde.getValue() == null
                ? "sin límite" : desde.getValue())
            + " | Hasta: "
            + (hasta.getValue() == null
                ? "sin límite" : hasta.getValue())
        );

        return filtro;
    }

    public void limpiar() {

        opcionesMeses.values().forEach(
            opcion -> opcion.setSelected(false)
        );

        opcionesCategorias.values().forEach(
            opcion -> opcion.setSelected(false)
        );

        desde.setValue(null);
        hasta.setValue(null);

        estado.setText("Sin filtros");
    }
}