package es.um.tds.gastos.controlador;

import es.um.tds.gastos.ConfiguracionSQLite;
import es.um.tds.gastos.modelo.*;
import es.um.tds.gastos.persistencia.CuentasMemoria;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class CuentasCompartidasTest {
    private final GestionCuentas gestion = new GestionCuentas(new CuentasMemoria());

    private Map<String, BigDecimal> grupo() {
        var grupo = new LinkedHashMap<String, BigDecimal>();
        grupo.put("Raquel", new BigDecimal("40"));
        grupo.put("Luis", new BigDecimal("30"));
        grupo.put("María", new BigDecimal("30"));
        return grupo;
    }

    private void comprobar(GestionCuentas servicio, CuentaCompartida cuenta, String... importes) {
        var esperados = Arrays.stream(importes).map(BigDecimal::new).toList();
        assertEquals(esperados, List.copyOf(servicio.saldos(cuenta).values()));
    }

    @Test void ejemploDelEnunciado() {
        var cuenta = gestion.crearCuenta("Viaje", Reparto.EQUITATIVO, grupo());
        comprobar(gestion, cuenta, "0.00", "0.00", "0.00");
        gestion.registrarPago(cuenta, "Raquel", new BigDecimal("30"));
        comprobar(gestion, cuenta, "20.00", "-10.00", "-10.00");
        gestion.registrarPago(cuenta, "Luis", new BigDecimal("9"));
        comprobar(gestion, cuenta, "17.00", "-4.00", "-13.00");
    }

    @Test void porcentajesYListaFija() {
        var nombres = grupo();
        var cuenta = gestion.crearCuenta("Piso", Reparto.PORCENTUAL, nombres);
        nombres.clear();
        assertThrows(UnsupportedOperationException.class, () -> cuenta.integrantes().clear());
        assertThrows(IllegalArgumentException.class,
            () -> gestion.registrarPago(cuenta, "Otra", BigDecimal.TEN));
        gestion.registrarPago(cuenta, "Raquel", new BigDecimal("30"));
        comprobar(gestion, cuenta, "18.00", "-9.00", "-9.00");
    }

    @Test void rechazaPorcentajesInvalidos() {
        var nombres = grupo();
        for (String valor : List.of("39", "41", "-1")) {
            nombres.put("Raquel", new BigDecimal(valor));
            assertThrows(IllegalArgumentException.class,
                () -> gestion.crearCuenta("Piso", Reparto.PORCENTUAL, nombres));
        }
        assertTrue(gestion.listar().isEmpty());
    }

    @Test void distribuyeCentimosEnOrdenDeInscripcion() {
        var cuenta = gestion.crearCuenta("Viaje", Reparto.EQUITATIVO, grupo());
        gestion.registrarPago(cuenta, "Raquel", BigDecimal.TEN);
        comprobar(gestion, cuenta, "6.66", "-3.33", "-3.33");
        assertThrows(IllegalArgumentException.class,
            () -> gestion.registrarPago(cuenta, "Luis", new BigDecimal("0.001")));
    }

    @Test void conservaLosGastosDeUnaBaseVersionDos(@TempDir Path carpeta) throws Exception {
        var fichero = carpeta.resolve("anterior.db");
        String categoria = UUID.randomUUID().toString();
        String gasto = UUID.randomUUID().toString();
        try (var conexion = java.sql.DriverManager.getConnection("jdbc:sqlite:" + fichero);
             var orden = conexion.createStatement()) {
            orden.execute("CREATE TABLE categorias (id TEXT PRIMARY KEY, nombre TEXT NOT NULL, clave TEXT UNIQUE)");
            orden.execute("CREATE TABLE gastos (id TEXT PRIMARY KEY, importe TEXT, fecha TEXT, categoria_id TEXT REFERENCES categorias(id))");
            orden.execute("PRAGMA user_version = 2");
            try (var alta = conexion.prepareStatement("INSERT INTO categorias VALUES (?, 'Transporte', 'transporte')")) {
                alta.setString(1, categoria);
                alta.executeUpdate();
            }
            try (var alta = conexion.prepareStatement("INSERT INTO gastos VALUES (?, '12.30', '2026-11-24', ?)")) {
                alta.setString(1, gasto);
                alta.setString(2, categoria);
                alta.executeUpdate();
            }
        }
        var config = new ConfiguracionSQLite(fichero);
        assertEquals(UUID.fromString(gasto), config.getGestionGastos().getGastos().getFirst().getId());
        assertEquals(1, config.getGestionGastos().getCategorias().size());
        var cuenta = config.getGestionCuentas().crearCuenta("Piso", Reparto.EQUITATIVO, grupo());
        comprobar(config.getGestionCuentas(), cuenta, "0.00", "0.00", "0.00");
    }

    @Test void conservaCuentasPagosYOrdenAlReabrir(@TempDir Path carpeta) {
        var fichero = carpeta.resolve("gastos.db");
        var servicio = new ConfiguracionSQLite(fichero).getGestionCuentas();
        var piso = servicio.crearCuenta("Piso", Reparto.PORCENTUAL, grupo());
        servicio.crearCuenta("Viaje", Reparto.EQUITATIVO, grupo());
        servicio.registrarPago(piso, "Raquel", new BigDecimal("30"));
        var reabierto = new ConfiguracionSQLite(fichero).getGestionCuentas();
        var cuentas = reabierto.listar();
        assertEquals(piso.id(), cuentas.get(0).id());
        assertEquals(grupo(), cuentas.get(0).integrantes());
        comprobar(reabierto, cuentas.get(0), "18.00", "-9.00", "-9.00");
        comprobar(reabierto, cuentas.get(1), "0.00", "0.00", "0.00");
    }
}
