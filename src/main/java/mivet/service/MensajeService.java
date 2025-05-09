package mivet.service;

import mivet.model.Mensaje;
import mivet.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MensajeService {

    private final MensajeRepository mensajeRepository;

    @Autowired
    public MensajeService(MensajeRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    public List<Mensaje> findAll() {
        return mensajeRepository.findAll();
    }

    public Optional<Mensaje> findById(Long id) {
        return mensajeRepository.findById(id);
    }

    public Mensaje save(Mensaje mensaje) {
        return mensajeRepository.save(mensaje);
    }

    public void delete(Long id) {
        mensajeRepository.deleteById(id);
    }

    public List<Mensaje> findByUsuarioId(Long usuarioId) {
        return mensajeRepository.findByUsuarioId(usuarioId);
    }
}
