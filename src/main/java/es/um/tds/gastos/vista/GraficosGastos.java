package es.um.tds.gastos.vista;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.um.tds.gastos.modelo.Categoria;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class GraficosGastos {

    private final BarChart<Number, String> barras;
    private final PieChart circular;

    private final NumberFormat formatoMoneda =
        NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("es-ES")
        );

    public GraficosGastos(
            BarChart<Number, String> barras,
            PieChart circular,
            Label mensajeBarras,
            Label mensajeCircular) {

        this.barras = barras;
        this.circular = circular;

        mensajeBarras.visibleProperty().bind(
            barras.visibleProperty().not()
        );
        mensajeBarras.managedProperty().bind(
            mensajeBarras.visibleProperty()
        );

        mensajeCircular.visibleProperty().bind(
            circular.visibleProperty().not()
        );
        mensajeCircular.managedProperty().bind(
            mensajeCircular.visibleProperty()
        );
    }

    public void actualizar(
            Map<Categoria, BigDecimal> totales,
            BigDecimal totalGeneral) {

        boolean hayDatos = !totales.isEmpty();

        barras.setVisible(hayDatos);
        circular.setVisible(hayDatos);

        barras.getData().clear();
        circular.getData().clear();

        if (!hayDatos) {
            return;
        }

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
                new XYChart.Data<>(importe.doubleValue(), nombre)
            );

            BigDecimal porcentaje = importe
                .multiply(new BigDecimal("100"))
                .divide(totalGeneral, 1, RoundingMode.HALF_UP);

            String etiqueta = nombre
                + ": " + formatoMoneda.format(importe)
                + " (" + porcentaje.toPlainString().replace('.', ',')
                + " %)";

            circular.getData().add(
                new PieChart.Data(etiqueta, importe.doubleValue())
            );
        }

        barras.getData().add(serie);
    }
}