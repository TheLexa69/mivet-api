package mivet.service;

import mivet.model.Protectora;
import mivet.repository.ProtectoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class ProtectoraService {

    private final ProtectoraRepository protectoraRepository;

    @Autowired
    public ProtectoraService(ProtectoraRepository protectoraRepository) {
        this.protectoraRepository = protectoraRepository;
    }

    public List<Protectora> findAll() {
        return protectoraRepository.findAll();
    }

    public Optional<Protectora> findById(Long id) {
        return protectoraRepository.findById(id);
    }

    public Optional<Protectora> findByUsuarioId(Integer idUsuario) {
        return protectoraRepository.findByUsuarioId(idUsuario);
    }

    public Protectora save(Protectora protectora) {
        return protectoraRepository.save(protectora);
    }

    public void deleteById(Long id) {
        protectoraRepository.deleteById(id);
    }
}
