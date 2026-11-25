package es.um.tds.gastos.aplicacion;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.nio.file.Path;

import es.um.tds.gastos.persistencia.BaseDatos;

class GestionGastosTest {

    @TempDir
    Path directorio;

    @Test
    void registrarGasto() {
        GestionGastos gestion = new GestionGastos(
            new BaseDatos(directorio.resolve("prueba.db"))
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