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
import es.um.tds.gastos.modelo.EvaluadorAlertas;

public class GestionGastos {

    private final RepositorioAlertas alertas;
    private final RepositorioGastos gastos;
    private final RepositorioCategorias categorias;
    private final EvaluadorAlertas evaluador = new EvaluadorAlertas();
    private final GestorTransacciones transacciones;

	
	public GestionGastos() {
	    this(new BaseDatos(
	        Path.of(System.getProperty("user.home"),
	                "gestion-gastos-tds.db")
	    ));
	}
	
	public GestionGastos(BaseDatos base) {
	    this(
	        new GastosSQLite(base),
	        new CategoriasSQLite(base),
	        new AlertasSQLite(base),
	        base
	    );
	}
	
	public GestionGastos(RepositorioGastos gastos, RepositorioCategorias categorias, 
			RepositorioAlertas alertas, GestorTransacciones transacciones) {
	    this.gastos = gastos;
	    this.categorias = categorias;
	    this.alertas = alertas;
	    this.transacciones = transacciones;
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
	    List<Gasto> lote = List.copyOf(importados);
	    transacciones.ejecutar(() -> {
	        for (Gasto gasto : lote) {
	            gastos.agregar(gasto);
	            evaluarAlertas(gasto, null);
	        }
	    });
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
	        					LocalDate fecha, Categoria categoria) {
	
	    comprobarCategoria(categoria);
	    Gasto gasto = new Gasto(importe, fecha, categoria);
	    transacciones.ejecutar(() -> {
	        gastos.agregar(gasto);
	        evaluarAlertas(gasto, null);
	    });
	
	    return gasto;
	}

	public void modificarGasto(Gasto gasto, BigDecimal importe,
	        			LocalDate fecha, Categoria categoria) {
	
	    comprobarGasto(gasto);
	    comprobarCategoria(categoria);
	
	    Gasto actualizado = new Gasto(
	        gasto.getId(), importe, fecha, categoria
	    );
	
	    transacciones.ejecutar(() -> {
	        Gasto anterior = gastos.listar().stream()
	            .filter(g -> g.getId().equals(gasto.getId()))
	            .findFirst()
	            .orElseThrow(() -> new IllegalArgumentException(
	                "El gasto no está registrado."
	            ));
	
	        gastos.modificar(actualizado);
	        evaluarAlertas(actualizado, anterior);
	    });
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
    
    private void evaluarAlertas(Gasto actual, Gasto anterior) {

        List<Gasto> registrados = gastos.listar();

        for (Alerta alerta : alertas.listar()) {

            evaluador.evaluar(
                alerta, actual, anterior, registrados
            ).ifPresent(notificacion ->
                alertas.guardarNotificacion(alerta, notificacion)
            );
        }
    }
    
}