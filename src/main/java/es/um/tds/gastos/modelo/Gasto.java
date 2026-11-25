package es.um.tds.gastos.modelo;

import java.util.UUID;
import java.util.Objects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public final class Gasto {
	
    private final UUID id;
    private BigDecimal importe;
    private LocalDate fecha;
    private Categoria categoria;

    public Gasto(BigDecimal importe, LocalDate fecha,
                 Categoria categoria) {
        this(UUID.randomUUID(), importe, fecha, categoria);
    }

    public Gasto(UUID id, BigDecimal importe, LocalDate fecha, Categoria categoria) {
        this.id = Objects.requireNonNull(id);
        actualizarDatos(importe, fecha, categoria);
    }

    public void actualizarDatos(BigDecimal importe,
                                LocalDate fecha,
                                Categoria categoria) {

        if (importe == null || importe.signum() <= 0) {
            throw new IllegalArgumentException(
                "El importe debe ser mayor que cero."
            );
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                "El gasto debe tener una fecha."
            );
        }

        if (categoria == null) {
            throw new IllegalArgumentException(
                "El gasto debe pertenecer a una categoría."
            );
        }

        BigDecimal importeValidado;

        try {
            importeValidado = importe.setScale(
                2, RoundingMode.UNNECESSARY
            );
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                "El importe no puede tener fracciones de céntimo."
            );
        }

        this.importe = importeValidado;
        this.fecha = fecha;
        this.categoria = categoria;
    }
    
    public UUID getId() {
        return id;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    @Override
    public String toString() {
        return fecha + " | " + categoria.getNombre()
            + " | " + importe.toPlainString() + " €";
    }
}