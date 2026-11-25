package es.um.tds.gastos.aplicacion;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import es.um.tds.gastos.modelo.*;
import es.um.tds.gastos.persistencia.BaseDatos;

public final class PruebaAlertas {
    public static void main(String[] args) throws Exception {
        Path fichero = Files.createTempFile("tds-alertas-", ".db");
        try {
            probar(fichero);
        } finally {
            Files.deleteIfExists(fichero);
        }
    }

    private static void probar(Path fichero) {
        var gestion = new GestionGastos(new BaseDatos(fichero));
        var juegos = gestion.crearCategoria("Videojuegos");
        var otra = gestion.getCategorias().getFirst();
        var fecha = LocalDate.of(2026, 11, 24);
        gestion.crearAlerta(new BigDecimal("100"), PeriodoAlerta.MENSUAL, juegos);
        gestion.registrarGasto(new BigDecimal("200"), fecha, otra);
        gestion.registrarGasto(new BigDecimal("95"), fecha, juegos);
        var gasto = gestion.registrarGasto(new BigDecimal("5"), fecha, juegos);
        verificar(gestion.getHistorial().isEmpty(), "100 € exactos: historial vacío");
        gestion.modificarGasto(gasto, new BigDecimal("6"), fecha, juegos);
        verificar(gestion.getHistorial().size() == 1, "101 €: un aviso");
        gestion.modificarGasto(gasto, new BigDecimal("7"), fecha, juegos);
        gestion = new GestionGastos(new BaseDatos(fichero));
        verificar(gestion.getAlertas().size() == 1 && gestion.getHistorial().size() == 1
            && gestion.getHistorial().getFirst().total().equals(new BigDecimal("101.00")),
            "Al reabrir persisten la alerta y el aviso original de 101 €");
        var lunes = LocalDate.of(2026, 12, 28);
        gestion.crearAlerta(new BigDecimal("10"), PeriodoAlerta.SEMANAL, null);
        gestion.importarGastos(List.of(new Gasto(new BigDecimal("9"), lunes, juegos),
            new Gasto(new BigDecimal("2"), lunes.plusDays(6), otra)));
        verificar(gestion.getHistorial().size() == 2 && gestion.getHistorial().getFirst().inicio().equals(lunes),
            "Importación: una semana que cruza de año suma ambas categorías");
        gestion.registrarGasto(new BigDecimal("2"), lunes.plusDays(7), otra);
        verificar(gestion.getHistorial().size() == 2, "El lunes empieza otra semana");
        var repetido = new Gasto(new BigDecimal("20"), lunes.plusDays(7), otra);
        int cantidad = gestion.getGastos().size();
        try {
            gestion.importarGastos(List.of(repetido, repetido));
            throw new AssertionError("La importación duplicada debía fallar");
        } catch (IllegalStateException esperado) {
            verificar(gestion.getGastos().size() == cantidad && gestion.getHistorial().size() == 2,
                "Un fallo de importación revierte gastos y avisos");
        }
    }

    private static void verificar(boolean resultado, String mensaje) {
        if (!resultado) {
            throw new AssertionError(mensaje);
        }
        System.out.println(mensaje);
    }
}
