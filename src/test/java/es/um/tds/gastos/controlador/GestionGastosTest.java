
package es.um.tds.gastos.controlador;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.nio.file.Path;

import es.um.tds.gastos.persistencia.AlertasSQLite;
import es.um.tds.gastos.persistencia.BaseDatos;
import es.um.tds.gastos.persistencia.CategoriasSQLite;
import es.um.tds.gastos.persistencia.GastosSQLite;

class GestionGastosTest {

    @TempDir
    Path directorio;

    @Test
    void registrarGasto() {

        BaseDatos base = new BaseDatos(
            directorio.resolve("prueba.db")
        );

        GestionGastos gestion = new GestionGastos(
            new GastosSQLite(base),
            new CategoriasSQLite(base),
            new AlertasSQLite(base),
            base
        );

        var categoria = gestion.crearCategoria("Viajes");

        gestion.registrarGasto(
            new BigDecimal("15.00"),
            LocalDate.now(),
            categoria
        );

        assertEquals(1, gestion.getGastos().size());

        assertEquals(
            new BigDecimal("15.00"),
            gestion.calcularTotal()
        );
    }
}
