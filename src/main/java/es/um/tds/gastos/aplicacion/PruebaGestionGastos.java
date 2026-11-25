package es.um.tds.gastos.aplicacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import es.um.tds.gastos.persistencia.BaseDatos;
import java.util.Set;

import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.FiltroGastos;
import es.um.tds.gastos.modelo.Gasto;

public class PruebaGestionGastos {

    public static void main(String[] args) throws IOException {
        Path fichero = Files.createTempFile("tds-prueba-", ".db");
        try {
            probar(fichero);
        } finally {
            Files.deleteIfExists(fichero);
        }
    }

    private static void probar(Path fichero) {
        GestionGastos gestion = new GestionGastos(new BaseDatos(fichero));
        System.out.println("Categorías iniciales: " + gestion.getCategorias());

        Categoria categoria = gestion.crearCategoria("Viajes");
        LocalDate fecha = LocalDate.of(2026, 11, 24);

        Gasto gasto = gestion.registrarGasto(
            new BigDecimal("12.00"), fecha, categoria
        );
        System.out.println("Gasto registrado: " + gasto);

        gestion = new GestionGastos(new BaseDatos(fichero));
        verificar(gestion.getCategorias().size() == 4);
        verificar(gestion.getGastos().getFirst().getId().equals(gasto.getId()));
        Gasto segundo = gestion.registrarGasto(new BigDecimal("3.00"), fecha, categoria);

        FiltroGastos filtro = new FiltroGastos(
            Set.of(Month.NOVEMBER), Set.of(categoria), null, null
        );
        System.out.println("Gastos de Viajes en noviembre: "
            + gestion.filtrarGastos(filtro).size());

        verificar(gestion.filtrarGastos(filtro).size() == 2);
        verificar(new BigDecimal("15.00").equals(
            gestion.calcularTotalesPorCategoria(gestion.getGastos()).get(categoria)));
        gestion.eliminarGasto(segundo);
        gestion.modificarGasto(gasto, new BigDecimal("15.00"), fecha, categoria);
        System.out.println("Total tras modificar: " + gestion.calcularTotal());
        System.out.println("Totales por categoría: "
            + gestion.calcularTotalesPorCategoria(gestion.getGastos()));

        verificar(gestion.calcularTotal().equals(new BigDecimal("15.00")));
        Categoria renombrada = gestion.modificarCategoria(categoria, "Vacaciones");
        verificar(gestion.getGastos().getFirst().getCategoria().getNombre().equals("Vacaciones"));
        gestion.eliminarGasto(gasto);
        gestion.eliminarCategoria(renombrada);
        verificar(gestion.getGastos().isEmpty() && gestion.getCategorias().size() == 3);
        System.out.println("Gastos tras borrar: " + gestion.getGastos().size());
    }

    private static void verificar(boolean condicion) {
        if (!condicion) {
            throw new AssertionError("Falló la comprobación de persistencia.");
        }
    }
}
