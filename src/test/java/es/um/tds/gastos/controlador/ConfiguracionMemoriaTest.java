
package es.um.tds.gastos.controlador;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.um.tds.gastos.ConfiguracionMemoria;
import es.um.tds.gastos.modelo.*;

class ConfiguracionMemoriaTest {

    private final GestionGastos gestion =
        new ConfiguracionMemoria().getGestionGastos();

    private final LocalDate fecha =
        LocalDate.of(2026, 11, 25);

    @Test
    void registrarGastoEnMemoria() {

        var categoria = gestion.crearCategoria("Viajes");

        gestion.registrarGasto(
            new BigDecimal("15"), fecha, categoria
        );

        assertEquals(1, gestion.getGastos().size());
        assertEquals(
            new BigDecimal("15.00"),
            gestion.calcularTotal()
        );
    }

    @Test
    void evaluarAlertasSinSQLite() {

        var categoria = gestion.crearCategoria("Videojuegos");

        gestion.crearAlerta(
            new BigDecimal("100"),
            PeriodoAlerta.MENSUAL,
            categoria
        );

        gestion.registrarGasto(
            new BigDecimal("95"), fecha, categoria
        );

        gestion.registrarGasto(
            new BigDecimal("6"), fecha, categoria
        );

        assertEquals(1, gestion.getHistorial().size());

        assertEquals(
            new BigDecimal("101.00"),
            gestion.getHistorial().getFirst().total()
        );
    }

    @Test
    void configuracionesIndependientes() {

        var categoria = gestion.crearCategoria("Viajes");

        gestion.registrarGasto(
            new BigDecimal("15"), fecha, categoria
        );

        GestionGastos otraGestion =
            new ConfiguracionMemoria().getGestionGastos();

        assertTrue(otraGestion.getGastos().isEmpty());
    }

    @Test
    void revertirImportacionFallida() {

        var categoria = gestion.crearCategoria("Videojuegos");

        gestion.crearAlerta(
            new BigDecimal("10"),
            PeriodoAlerta.MENSUAL,
            categoria
        );

        Gasto gasto = new Gasto(
            new BigDecimal("20"), fecha, categoria
        );

        assertThrows(
            IllegalStateException.class,
            () -> gestion.importarGastos(List.of(gasto, gasto))
        );

        assertTrue(gestion.getGastos().isEmpty());
        assertTrue(gestion.getHistorial().isEmpty());
    }
}
