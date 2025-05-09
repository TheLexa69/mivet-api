package mivet.service;

import mivet.model.Cita;
import mivet.model.Citopatologia;
import mivet.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    private final CitaRepository citaRepository;

    @Autowired
    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public List<Cita> findAll() {
        return citaRepository.findAll();
    }

    public Optional<Cita> findById(Long id) {
        return citaRepository.findById(id);
    }

    public Cita save(Cita cita) {
        return citaRepository.save(cita);
    }

    public void delete(Long id) {
        citaRepository.deleteById(id);
    }

    public List<Cita> findByUsuarioId(Long usuarioId) {
        return citaRepository.findByUsuarioId(usuarioId);
    }

    public List<Cita> findByMascotaId(Long mascotaId) {
        return citaRepository.findByMascotaId(mascotaId);
    }
}
