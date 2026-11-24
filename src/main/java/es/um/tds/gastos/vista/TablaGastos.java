package es.um.tds.gastos.vista;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import es.um.tds.gastos.modelo.Gasto;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public final class TablaGastos {

    private TablaGastos() {
    }

    public static void configurar(
            TableColumn<Gasto, LocalDate> columnaFecha,
            TableColumn<Gasto, String> columnaCategoria,
            TableColumn<Gasto, BigDecimal> columnaImporte) {

        DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

        columnaFecha.setCellValueFactory(datos ->
            new ReadOnlyObjectWrapper<>(
                datos.getValue().getFecha()
            )
        );

        columnaFecha.setCellFactory(columna ->
            new TableCell<Gasto, LocalDate>() {
                @Override
                protected void updateItem(
                        LocalDate fecha, boolean vacia) {

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

        columnaImporte.setCellFactory(columna ->
            new TableCell<Gasto, BigDecimal>() {
                @Override
                protected void updateItem(
                        BigDecimal importe, boolean vacia) {

                    super.updateItem(importe, vacia);

                    setText(vacia || importe == null
                        ? null
                        : importe.toPlainString() + " €");
                }
            }
        );
    }
}