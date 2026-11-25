
package es.um.tds.gastos.persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import es.um.tds.gastos.modelo.*;

class GestorTransaccionesTest {

    @TempDir
    Path directorio;

    @Test
    void rollbackEntreRepositorios() {

        BaseDatos base = new BaseDatos(
            directorio.resolve("transacciones.db")
        );

        var categorias = new CategoriasSQLite(base);
        var gastos = new GastosSQLite(base);
        var alertas = new AlertasSQLite(base);

        Categoria categoria = new Categoria("Viajes");

        assertThrows(IllegalStateException.class, () -> {

            base.ejecutar(() -> {

                categorias.agregar(categoria);

                gastos.agregar(new Gasto(
                    new BigDecimal("50"),
                    LocalDate.of(2026, 11, 25),
                    categoria
                ));

                alertas.agregar(new Alerta(
                    UUID.randomUUID(),
                    new BigDecimal("100"),
                    new PeriodoMensual(),
                    categoria
                ));

                assertEquals(4, categorias.listar().size());
                assertEquals(1, gastos.listar().size());
                assertEquals(1, alertas.listar().size());

                throw new IllegalStateException("Fallo simulado");
            });
        });

        assertEquals(3, categorias.listar().size());
        assertTrue(gastos.listar().isEmpty());
        assertTrue(alertas.listar().isEmpty());
    }
}
