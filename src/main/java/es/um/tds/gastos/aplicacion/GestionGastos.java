package es.um.tds.gastos.aplicacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.Gasto;
import es.um.tds.gastos.modelo.FiltroGastos;

public class GestionGastos {

    private final List<Gasto> gastos = new ArrayList<>();
    private final List<Categoria> categorias = new ArrayList<>();

    public GestionGastos() {
        categorias.add(new Categoria("Alimentación"));
        categorias.add(new Categoria("Transporte"));
        categorias.add(new Categoria("Entretenimiento"));
    }

    public List<Gasto> getGastos() {
        return List.copyOf(gastos);
    }

    public List<Categoria> getCategorias() {
        return List.copyOf(categorias);
    }

    public Categoria crearCategoria(String nombre) {
        Categoria nueva = new Categoria(nombre);

        boolean existe = categorias.stream()
            .anyMatch(categoria ->
                categoria.getNombre()
                    .equalsIgnoreCase(nueva.getNombre())
            );

        if (existe) {
            throw new IllegalArgumentException(
                "Ya existe una categoría con ese nombre."
            );
        }

        categorias.add(nueva);
        return nueva;
    }

    public Gasto registrarGasto(BigDecimal importe,
                                LocalDate fecha,
                                Categoria categoria) {
        comprobarCategoria(categoria);

        Gasto gasto = new Gasto(importe, fecha, categoria);
        gastos.add(gasto);

        return gasto;
    }

    public void modificarGasto(Gasto gasto,
                               BigDecimal importe,
                               LocalDate fecha,
                               Categoria categoria) {
        comprobarGasto(gasto);
        comprobarCategoria(categoria);

        gasto.actualizarDatos(importe, fecha, categoria);
    }

    public void eliminarGasto(Gasto gasto) {
        comprobarGasto(gasto);
        gastos.remove(gasto);
    }

    public BigDecimal calcularTotal() {
        return gastos.stream()
            .map(Gasto::getImporte)
            .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    private void comprobarCategoria(Categoria categoria) {
        if (categoria == null || !categorias.contains(categoria)) {
            throw new IllegalArgumentException(
                "Selecciona una categoría registrada."
            );
        }
    }

    private void comprobarGasto(Gasto gasto) {
        if (gasto == null || !gastos.contains(gasto)) {
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