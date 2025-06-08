package mivet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import mivet.enums.TipoGasto;

import java.time.LocalDate;

@Entity
@Table(name = "Gasto")
@Data
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;
    private Double cantidad;
    private LocalDate fecha;
    @Enumerated(EnumType.STRING)
    private TipoGasto tipo;

    public TipoGasto getTipo() {
        return tipo;
    }

    public void setTipo(TipoGasto tipo) {
        this.tipo = tipo;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "id_mascota")
    @JsonBackReference
    private Mascota mascota;

}
