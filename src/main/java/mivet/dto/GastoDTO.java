package mivet.dto;

import lombok.Data;
import mivet.enums.TipoGasto;

import java.time.LocalDate;

@Data
public class GastoDTO {
    private Long id;
    private Long idMascota;
    private String descripcion;
    private Double cantidad;
    private LocalDate fecha;
    private TipoGasto tipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoGasto getTipo() {
        return tipo;
    }

    public void setTipo(TipoGasto tipo) {
        this.tipo = tipo;
    }

    public Long getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(Long idMascota) {
        this.idMascota = idMascota;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
