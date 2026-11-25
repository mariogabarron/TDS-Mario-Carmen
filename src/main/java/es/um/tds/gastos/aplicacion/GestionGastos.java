package es.um.tds.gastos.aplicacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.nio.file.Path;
import es.um.tds.gastos.persistencia.*;
import java.util.List;
import java.util.UUID;
import es.um.tds.gastos.modelo.Alerta;
import es.um.tds.gastos.modelo.Notificacion;
import es.um.tds.gastos.modelo.PeriodoAlerta;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.Gasto;
import es.um.tds.gastos.modelo.FiltroGastos;

public class GestionGastos {

    private final RepositorioAlertas alertas;
    private final RepositorioGastos gastos;
    private final RepositorioCategorias categorias;

    public GestionGastos() {
        this(new BaseDatos(Path.of(System.getProperty("user.home"), "gestion-gastos-tds.db")));
    }

    public GestionGastos(BaseDatos base) {
        this(new GastosSQLite(base), new CategoriasSQLite(base), new AlertasSQLite(base));
    }

    public GestionGastos(RepositorioGastos gastos, RepositorioCategorias categorias) {
        this(gastos, categorias, null);
    }

    public GestionGastos(RepositorioGastos gastos, RepositorioCategorias categorias, RepositorioAlertas alertas) {
        this.alertas = alertas;
        this.gastos = gastos;
        this.categorias = categorias;
    }

    public void crearAlerta(BigDecimal limite, PeriodoAlerta periodo, Categoria categoria) {
        if (categoria != null) {
            comprobarCategoria(categoria);
        }
        repositorioAlertas().agregar(new Alerta(UUID.randomUUID(), limite, periodo, categoria));
    }

    public List<Alerta> getAlertas() {
        return repositorioAlertas().listar();
    }

    public List<Notificacion> getHistorial() {
        return repositorioAlertas().historial();
    }

    private RepositorioAlertas repositorioAlertas() {
        if (alertas == null) {
            throw new IllegalStateException("Esta configuración requiere un repositorio de alertas.");
        }
        return alertas;
    }

    public void importarGastos(List<Gasto> importados) {
        gastos.importar(List.copyOf(importados));
    }

    public List<Gasto> getGastos() {
        return gastos.listar();
    }

    public List<Categoria> getCategorias() {
        return categorias.listar();
    }

    public Categoria crearCategoria(String nombre) {
        Categoria nueva = new Categoria(nombre);
        categorias.agregar(nueva);
        return nueva;
    }

    public Categoria modificarCategoria(Categoria categoria, String nombre) {
        comprobarCategoria(categoria);
        Categoria actualizada = new Categoria(categoria.getId(), nombre);
        categorias.modificar(actualizada);
        return actualizada;
    }

    public void eliminarCategoria(Categoria categoria) {
        comprobarCategoria(categoria);
        categorias.eliminar(categoria);
    }

    public Gasto registrarGasto(BigDecimal importe,
                                LocalDate fecha,
                                Categoria categoria) {
        comprobarCategoria(categoria);

        Gasto gasto = new Gasto(importe, fecha, categoria);
        gastos.agregar(gasto);

        return gasto;
    }

    public void modificarGasto(Gasto gasto,
                               BigDecimal importe,
                               LocalDate fecha,
                               Categoria categoria) {
        comprobarGasto(gasto);
        comprobarCategoria(categoria);

        gastos.modificar(new Gasto(gasto.getId(), importe, fecha, categoria));
        gasto.actualizarDatos(importe, fecha, categoria);
    }

    public void eliminarGasto(Gasto gasto) {
        comprobarGasto(gasto);
        gastos.eliminar(gasto);
    }

    public BigDecimal calcularTotal() {
        return calcularTotal(getGastos());
    }

    public BigDecimal calcularTotal(List<Gasto> gastos) {
        return gastos.stream()
            .map(Gasto::getImporte)
            .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
    private void comprobarCategoria(Categoria categoria) {
        if (categoria == null || !getCategorias().contains(categoria)) {
            throw new IllegalArgumentException(
                "Selecciona una categoría registrada."
            );
        }
    }

    private void comprobarGasto(Gasto gasto) {
        if (gasto == null) {
            throw new IllegalArgumentException(
                "El gasto no está registrado."
            );
        }
    }
    
    public Map<Categoria, BigDecimal> calcularTotalesPorCategoria(
            List<Gasto> gastosConsultados) {

        return gastosConsultados.stream()
            .collect(Collectors.groupingBy(
                Gasto::getCategoria,
                LinkedHashMap::new,
                Collectors.reducing(
                    new BigDecimal("0.00"),
                    Gasto::getImporte,
                    BigDecimal::add
                )
            ));
    }
    
    public List<Gasto> filtrarGastos(FiltroGastos filtro) {
        return getGastos().stream()
            .filter(filtro::cumple)
            .toList();
    }
    
}