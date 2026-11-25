
package es.um.tds.gastos.persistencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import es.um.tds.gastos.modelo.*;

public final class AlmacenMemoria implements GestorTransacciones {

    private final Map<UUID, Gasto> gastos =
        new LinkedHashMap<>();

    private final Map<UUID, Categoria> categorias =
        new LinkedHashMap<>();

    private final Map<UUID, Alerta> alertas =
        new LinkedHashMap<>();

    private final List<Notificacion> notificaciones =
        new ArrayList<>();

    public AlmacenMemoria() {
        for (String nombre : List.of(
                "Alimentación",
                "Transporte",
                "Entretenimiento")) {

            Categoria categoria = new Categoria(nombre);
            categorias.put(categoria.getId(), categoria);
        }
    }

    private Categoria recuperarCategoria(Categoria categoria) {
        Categoria registrada = categorias.get(categoria.getId());

        if (registrada == null) {
            throw new IllegalArgumentException(
                "La categoría no está registrada."
            );
        }

        return registrada;
    }

    private Gasto copiar(Gasto gasto) {
        return new Gasto(
            gasto.getId(),
            gasto.getImporte(),
            gasto.getFecha(),
            recuperarCategoria(gasto.getCategoria())
        );
    }

    public RepositorioGastos getRepositorioGastos() {

        return new RepositorioGastos() {

            @Override
            public List<Gasto> listar() {
                return gastos.values().stream()
                    .map(AlmacenMemoria.this::copiar)
                    .toList();
            }

            @Override
            public void agregar(Gasto gasto) {

                if (gastos.containsKey(gasto.getId())) {
                    throw new IllegalStateException(
                        "El gasto ya está registrado."
                    );
                }

                gastos.put(gasto.getId(), copiar(gasto));
            }

            @Override
            public void modificar(Gasto gasto) {

                if (!gastos.containsKey(gasto.getId())) {
                    throw new IllegalArgumentException(
                        "El gasto no está registrado."
                    );
                }

                gastos.put(gasto.getId(), copiar(gasto));
            }

            @Override
            public void eliminar(Gasto gasto) {

                if (gastos.remove(gasto.getId()) == null) {
                    throw new IllegalArgumentException(
                        "El gasto no está registrado."
                    );
                }
            }
        };
    }

    private void comprobarNombre(Categoria categoria) {

        String clave = categoria.getNombre()
            .toUpperCase(Locale.ROOT);

        boolean existe = categorias.values().stream()
            .anyMatch(c ->
                !c.getId().equals(categoria.getId())
                && c.getNombre()
                    .toUpperCase(Locale.ROOT)
                    .equals(clave)
            );

        if (existe) {
            throw new IllegalArgumentException(
                "Ya existe una categoría con ese nombre."
            );
        }
    }

    public RepositorioCategorias getRepositorioCategorias() {

        return new RepositorioCategorias() {

            @Override
            public List<Categoria> listar() {
                return List.copyOf(categorias.values());
            }

            @Override
            public void agregar(Categoria categoria) {

                comprobarNombre(categoria);

                if (categorias.containsKey(categoria.getId())) {
                    throw new IllegalStateException(
                        "La categoría ya está registrada."
                    );
                }

                categorias.put(categoria.getId(), categoria);
            }

            @Override
            public void modificar(Categoria categoria) {

                recuperarCategoria(categoria);
                comprobarNombre(categoria);

                categorias.put(categoria.getId(), categoria);
            }

            @Override
            public void eliminar(Categoria categoria) {

                recuperarCategoria(categoria);

                boolean tieneGastos = gastos.values().stream()
                    .anyMatch(g ->
                        g.getCategoria().equals(categoria)
                    );

                boolean tieneAlertas = alertas.values().stream()
                    .anyMatch(a ->
                        categoria.equals(a.categoria())
                    );

                if (tieneGastos || tieneAlertas) {
                    throw new IllegalArgumentException(
                        "La categoría tiene elementos asociados."
                    );
                }

                categorias.remove(categoria.getId());
            }
        };
    }

    public RepositorioAlertas getRepositorioAlertas() {

        return new RepositorioAlertas() {

            @Override
            public void agregar(Alerta alerta) {

                if (alerta.categoria() != null) {
                    recuperarCategoria(alerta.categoria());
                }

                if (alertas.containsKey(alerta.id())) {
                    throw new IllegalStateException(
                        "La alerta ya está registrada."
                    );
                }

                alertas.put(alerta.id(), alerta);
            }

            @Override
            public List<Alerta> listar() {

                return alertas.values().stream()
                    .map(a -> new Alerta(
                        a.id(),
                        a.limite(),
                        a.periodo(),
                        a.categoria() == null
                            ? null
                            : recuperarCategoria(a.categoria())
                    ))
                    .toList();
            }

            @Override
            public List<Notificacion> historial() {
                return List.copyOf(notificaciones);
            }

            @Override
            public void guardarNotificacion(
                    Alerta alerta,
                    Notificacion notificacion) {

                if (!alertas.containsKey(alerta.id())) {
                    throw new IllegalArgumentException(
                        "La alerta no está registrada."
                    );
                }

                notificaciones.add(0, notificacion);
            }
        };
    }

    @Override
    public void ejecutar(Runnable operacion) {

        var copiaGastos = new LinkedHashMap<>(gastos);
        var copiaCategorias = new LinkedHashMap<>(categorias);
        var copiaAlertas = new LinkedHashMap<>(alertas);
        var copiaNotificaciones = new ArrayList<>(notificaciones);

        try {

            operacion.run();

        } catch (RuntimeException | Error e) {

            gastos.clear();
            gastos.putAll(copiaGastos);

            categorias.clear();
            categorias.putAll(copiaCategorias);

            alertas.clear();
            alertas.putAll(copiaAlertas);

            notificaciones.clear();
            notificaciones.addAll(copiaNotificaciones);

            throw e;
        }
    }
}
