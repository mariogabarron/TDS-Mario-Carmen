package es.um.tds.gastos.consola;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import es.um.tds.gastos.aplicacion.GestionGastos;
import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.Gasto;

public class ConsolaGastos {

    private final GestionGastos gestion;
    private final Scanner entrada;

    public ConsolaGastos(GestionGastos gestion, Scanner entrada) {
        this.gestion = gestion;
        this.entrada = entrada;
    }

    public static void main(String[] args) {
        GestionGastos gestion = new GestionGastos();

        try (Scanner entrada = new Scanner(System.in)) {
            ConsolaGastos consola =
                new ConsolaGastos(gestion, entrada);

            consola.ejecutar();
        }
    }

    public void ejecutar() {
        System.out.println("GESTIÓN DE GASTOS — CONSOLA");
        System.out.println(
            "Los datos se conservan durante esta ejecución."
        );

        boolean continuar = true;

        try {
            while (continuar) {
                mostrarMenu();

                String opcion = leerTexto("Elige una opción: ");

                try {
                    switch (opcion) {
                        case "1" -> listarGastos();
                        case "2" -> registrarGasto();
                        case "3" -> modificarGasto();
                        case "4" -> eliminarGasto();
                        case "5" -> listarCategorias();
                        case "6" -> crearCategoria();
                        case "0" -> continuar = false;
                        default -> System.out.println(
                            "Opción incorrecta. Elige entre 0 y 6."
                        );
                    }

                } catch (DateTimeParseException e) {
                    System.out.println(
                        "Fecha incorrecta. Usa AAAA-MM-DD "
                            + "y una fecha que exista."
                    );

                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

        } catch (NoSuchElementException e) {
            System.out.println(
                "\nSe ha cerrado la entrada de la consola."
            );
        }

        System.out.println("Aplicación finalizada.");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("1. Listar gastos y consultar total");
        System.out.println("2. Registrar un gasto");
        System.out.println("3. Modificar un gasto");
        System.out.println("4. Eliminar un gasto");
        System.out.println("5. Listar categorías");
        System.out.println("6. Crear una categoría");
        System.out.println("0. Salir");
        System.out.println();
    }

    private void listarGastos() {
        List<Gasto> gastos = gestion.getGastos();

        if (gastos.isEmpty()) {
            System.out.println("No hay gastos registrados.");
            return;
        }

        mostrarGastos(gastos);

        System.out.println(
            "Total: "
                + gestion.calcularTotal().toPlainString()
                + " €"
        );
    }

    private void mostrarGastos(List<Gasto> gastos) {
        System.out.println();
        System.out.println("N.º | Fecha | Categoría | Importe");

        for (int i = 0; i < gastos.size(); i++) {
            System.out.println(
                (i + 1) + " | " + gastos.get(i)
            );
        }

        System.out.println();
    }

    private void registrarGasto() {
        BigDecimal importe = leerImporte(
            "Importe (€): "
        );

        LocalDate fecha = leerFecha(
            "Fecha (AAAA-MM-DD): "
        );

        Categoria categoria = seleccionarCategoria(null);

        Gasto gasto = gestion.registrarGasto(
            importe, fecha, categoria
        );

        System.out.println("Gasto registrado: " + gasto);
    }

    private void modificarGasto() {
        Gasto gasto = seleccionarGasto();

        if (gasto == null) {
            return;
        }

        System.out.println(
            "Pulsa Intro sin escribir para conservar cada dato."
        );

        String textoImporte = leerTexto(
            "Importe [" + gasto.getImporte().toPlainString()
                + "]: "
        );

        BigDecimal importe = textoImporte.isEmpty()
            ? gasto.getImporte()
            : convertirImporte(textoImporte);

        String textoFecha = leerTexto(
            "Fecha [" + gasto.getFecha() + "]: "
        );

        LocalDate fecha = textoFecha.isEmpty()
            ? gasto.getFecha()
            : LocalDate.parse(textoFecha);

        Categoria categoria = seleccionarCategoria(
            gasto.getCategoria()
        );

        gestion.modificarGasto(
            gasto, importe, fecha, categoria
        );

        System.out.println("Gasto modificado correctamente.");
    }

    private void eliminarGasto() {
        Gasto gasto = seleccionarGasto();

        if (gasto == null) {
            return;
        }

        System.out.println("Gasto seleccionado: " + gasto);

        String confirmacion = leerTexto(
            "¿Eliminarlo? Escribe s para confirmar: "
        );

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Eliminación cancelada.");
            return;
        }

        gestion.eliminarGasto(gasto);

        System.out.println("Gasto eliminado correctamente.");
    }

    private Gasto seleccionarGasto() {
        List<Gasto> gastos = gestion.getGastos();

        if (gastos.isEmpty()) {
            System.out.println("No hay gastos registrados.");
            return null;
        }

        mostrarGastos(gastos);

        String texto = leerTexto(
            "Número del gasto, o 0 para cancelar: "
        );

        if (texto.equals("0")) {
            return null;
        }

        int indice = convertirIndice(texto, gastos.size());

        return gastos.get(indice);
    }

    private void listarCategorias() {
        mostrarCategorias(gestion.getCategorias());
    }

    private void mostrarCategorias(List<Categoria> categorias) {
        System.out.println();

        for (int i = 0; i < categorias.size(); i++) {
            System.out.println(
                (i + 1) + ". " + categorias.get(i).getNombre()
            );
        }

        System.out.println();
    }

    private Categoria seleccionarCategoria(Categoria actual) {
        List<Categoria> categorias = gestion.getCategorias();

        if (categorias.isEmpty()) {
            throw new IllegalArgumentException(
                "Primero debes crear una categoría."
            );
        }

        mostrarCategorias(categorias);

        String mensaje = actual == null
            ? "Número de categoría: "
            : "Número de categoría [Intro conserva "
                + actual.getNombre() + "]: ";

        String texto = leerTexto(mensaje);

        if (texto.isEmpty() && actual != null) {
            return actual;
        }

        int indice = convertirIndice(texto, categorias.size());

        return categorias.get(indice);
    }

    private void crearCategoria() {
        String nombre = leerTexto(
            "Nombre de la nueva categoría: "
        );

        Categoria categoria = gestion.crearCategoria(nombre);

        System.out.println(
            "Categoría creada: " + categoria.getNombre()
        );
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().strip();
    }

    private BigDecimal leerImporte(String mensaje) {
        return convertirImporte(leerTexto(mensaje));
    }

    private BigDecimal convertirImporte(String texto) {
        try {
            return new BigDecimal(texto.replace(',', '.'));

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Introduce un importe numérico, por ejemplo 12,50."
            );
        }
    }

    private LocalDate leerFecha(String mensaje) {
        return LocalDate.parse(leerTexto(mensaje));
    }

    private int convertirIndice(String texto, int cantidad) {
        int numero;

        try {
            numero = Integer.parseInt(texto);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Debes introducir un número entero."
            );
        }

        if (numero < 1 || numero > cantidad) {
            throw new IllegalArgumentException(
                "El número debe estar entre 1 y " + cantidad + "."
            );
        }

        return numero - 1;
    }
}	