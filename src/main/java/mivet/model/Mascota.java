package mivet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import mivet.enums.TipoMascota;

import java.time.LocalDate;
import java.util.List;

@Data
@Table(name = "Mascota")
@Entity
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoMascota tipo;

    private String raza;

    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<HistorialClinico> historialesClinicos;


    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PruebaImagen> pruebasImagen;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Analitica> analiticas;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Tratamiento> tratamientos;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoMascota getTipo() {
        return tipo;
    }

    public void setTipo(TipoMascota tipo) {
        this.tipo = tipo;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<HistorialClinico> getHistorialesClinicos() {
        return historialesClinicos;
    }

    public void setHistorialesClinicos(List<HistorialClinico> historialesClinicos) {
        this.historialesClinicos = historialesClinicos;
    }

    public List<PruebaImagen> getPruebasImagen() {
        return pruebasImagen;
    }

    public void setPruebasImagen(List<PruebaImagen> pruebasImagen) {
        this.pruebasImagen = pruebasImagen;
    }

    public List<Analitica> getAnaliticas() {
        return analiticas;
    }

    public void setAnaliticas(List<Analitica> analiticas) {
        this.analiticas = analiticas;
    }

    public List<Tratamiento> getTratamientos() {
        return tratamientos;
    }

    public void setTratamientos(List<Tratamiento> tratamientos) {
        this.tratamientos = tratamientos;
    }

    public List<Citopatologia> getCitopatologias() {
        return citopatologias;
    }

    public void setCitopatologias(List<Citopatologia> citopatologias) {
        this.citopatologias = citopatologias;
    }

    public List<Gasto> getGastos() {
        return gastos;
    }

    public void setGastos(List<Gasto> gastos) {
        this.gastos = gastos;
    }

    public List<Cita> getCitas() {
        return citas;
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<HistorialDueno> getHistorialDuenos() {
        return historialDuenos;
    }

    public void setHistorialDuenos(List<HistorialDueno> historialDuenos) {
        this.historialDuenos = historialDuenos;
    }

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Citopatologia> citopatologias;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Gasto> gastos;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Cita> citas;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<HistorialDueno> historialDuenos;

}
