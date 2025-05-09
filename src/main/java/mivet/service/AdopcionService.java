package mivet.service;

import mivet.enums.EstadoAdopcion;
import mivet.model.Adopcion;
import mivet.model.Mascota;
import mivet.model.Usuario;
import mivet.repository.AdopcionRepository;
import mivet.repository.MascotaRepository;
import mivet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdopcionService {

    private final AdopcionRepository adopcionRepository;
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AdopcionService(AdopcionRepository adopcionRepository, MascotaRepository mascotaRepository, UsuarioRepository usuarioRepository) {
        this.adopcionRepository = adopcionRepository;
        this.mascotaRepository = mascotaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Adopcion> findAll() {
        return adopcionRepository.findAll();
    }

    public Optional<Adopcion> findById(Long id) {
        return adopcionRepository.findById(id);
    }

    public Adopcion save(Adopcion adopcion) {
        return adopcionRepository.save(adopcion);
    }

    public void delete(Long id) {
        adopcionRepository.deleteById(id);
    }

    public String solicitarAdopcion(Long idUsuario, Long idMascota, String mensaje) {
        Optional<Mascota> mascotaOpt = mascotaRepository.findById(idMascota);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (mascotaOpt.isEmpty() || usuarioOpt.isEmpty()) {
            return "not_found";
        }

        Mascota mascota = mascotaOpt.get();
        Usuario usuario = usuarioOpt.get();

        if (!"protectora".equalsIgnoreCase(mascota.getUsuario().getTipoUsuario().toString())) {
            return "not_protectora";
        }

        boolean yaSolicitada = adopcionRepository.existsByMascotaAndUsuario(mascota, usuario);
        if (yaSolicitada) {
            return "duplicate";
        }

        Adopcion adopcion = new Adopcion();
        adopcion.setMascota(mascota);
        adopcion.setUsuario(usuario);
        adopcion.setMensaje(mensaje);
        adopcion.setEstado(EstadoAdopcion.pendiente);
        adopcion.setFechaSolicitud(LocalDateTime.now());

        adopcionRepository.save(adopcion);
        return "ok";
    }
}
