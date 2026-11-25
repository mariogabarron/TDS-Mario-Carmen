
package es.um.tds.gastos.aplicacion;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import es.um.tds.gastos.modelo.*;
import es.um.tds.gastos.persistencia.BaseDatos;

class AlertasTest {

    @TempDir
    Path directorio;

    private Path fichero;
    private GestionGastos gestion;
    private Categoria juegos;
    private Categoria otra;

    private final LocalDate fecha = LocalDate.of(2026, 11, 24);

    @BeforeEach
    void inicializar() {
        fichero = directorio.resolve("alertas.db");
        gestion = new GestionGastos(new BaseDatos(fichero));

        juegos = gestion.crearCategoria("Videojuegos");
        otra = gestion.getCategorias().getFirst();
    }

    @Test
    void noNotificarAlIgualarLimite() {
        gestion.crearAlerta(
            new BigDecimal("100"),
            PeriodoAlerta.MENSUAL,
            juegos
        );

        gestion.registrarGasto(new BigDecimal("95"), fecha, juegos);
        gestion.registrarGasto(new BigDecimal("5"), fecha, juegos);

        assertTrue(gestion.getHistorial().isEmpty());
    }

    @Test
    void notificarAlSuperarLimite() {
        gestion.crearAlerta(
            new BigDecimal("100"),
            PeriodoAlerta.MENSUAL,
            juegos
        );

        gestion.registrarGasto(new BigDecimal("95"), fecha, juegos);
        gestion.registrarGasto(new BigDecimal("6"), fecha, juegos);

        assertEquals(1, gestion.getHistorial().size());
        assertEquals(
            new BigDecimal("101.00"),
            gestion.getHistorial().getFirst().total()
        );
    }

    @Test
    void ignorarOtrasCategorias() {
        gestion.crearAlerta(
            new BigDecimal("100"),
            PeriodoAlerta.MENSUAL,
            juegos
        );

        gestion.registrarGasto(new BigDecimal("200"), fecha, otra);

        assertTrue(gestion.getHistorial().isEmpty());
    }

    @Test
    void notificarAlModificarGasto() {
        gestion.crearAlerta(
            new BigDecimal("100"),
            PeriodoAlerta.MENSUAL,
            juegos
        );

        var gasto = gestion.registrarGasto(
            new BigDecimal("100"), fecha, juegos
        );

        gestion.modificarGasto(
            gasto, new BigDecimal("101"), fecha, juegos
        );

        assertEquals(1, gestion.getHistorial().size());
    }

    @Test
    void noDuplicarNotificaciones() {
        gestion.crearAlerta(
            new BigDecimal("100"),
            PeriodoAlerta.MENSUAL,
            juegos
        );

        var gasto = gestion.registrarGasto(
            new BigDecimal("101"), fecha, juegos
        );

        gestion.modificarGasto(
            gasto, new BigDecimal("102"), fecha, juegos
        );

        assertEquals(1, gestion.getHistorial().size());
    }

    @Test
    void conservarAlertasYNotificaciones() {
        gestion.crearAlerta(
            new BigDecimal("100"),
            PeriodoAlerta.MENSUAL,
            juegos
        );

        gestion.registrarGasto(new BigDecimal("101"), fecha, juegos);

        gestion = new GestionGastos(new BaseDatos(fichero));

        assertEquals(1, gestion.getAlertas().size());
        assertEquals(1, gestion.getHistorial().size());

        assertEquals(
            new BigDecimal("101.00"),
            gestion.getHistorial().getFirst().total()
        );
    }

    @Test
    void alertaSemanalEntreDosAnios() {
        LocalDate lunes = LocalDate.of(2026, 12, 28);

        gestion.crearAlerta(
            new BigDecimal("10"),
            PeriodoAlerta.SEMANAL,
            null
        );

        gestion.importarGastos(List.of(
            new Gasto(new BigDecimal("9"), lunes, juegos),
            new Gasto(new BigDecimal("2"), lunes.plusDays(6), otra)
        ));

        assertEquals(1, gestion.getHistorial().size());
        assertEquals(lunes, gestion.getHistorial().getFirst().inicio());

        gestion.registrarGasto(
            new BigDecimal("2"), lunes.plusDays(7), otra
        );

        assertEquals(1, gestion.getHistorial().size());
    }

    @Test
    void revertirImportacionFallida() {
        gestion.crearAlerta(
            new BigDecimal("10"),
            PeriodoAlerta.MENSUAL,
            juegos
        );

        var repetido = new Gasto(
            new BigDecimal("20"), fecha, juegos
        );

        assertThrows(
            IllegalStateException.class,
            () -> gestion.importarGastos(List.of(repetido, repetido))
        );

        assertTrue(gestion.getGastos().isEmpty());
        assertTrue(gestion.getHistorial().isEmpty());
    }
}
