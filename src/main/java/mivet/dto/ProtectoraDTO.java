package mivet.dto;

/**
 * DTO para la entidad Protectora.
 * Utilizado para transferir datos entre el cliente y el servidor sin exponer la entidad completa.
 */
public class ProtectoraDTO {
    private Long id;
    private String nombre;
    private String cif;
    private String telefono;
    private String web;
    private String codigoONG;
    private String direccion;
    private String logo;
    private Integer idUsuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getCodigoONG() {
        return codigoONG;
    }

    public void setCodigoONG(String codigoONG) {
        this.codigoONG = codigoONG;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}