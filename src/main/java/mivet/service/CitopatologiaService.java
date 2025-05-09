package mivet.service;

import mivet.model.Citopatologia;
import mivet.repository.CitopatologiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitopatologiaService {

    private final CitopatologiaRepository citopatologiaRepository;

    @Autowired
    public CitopatologiaService(CitopatologiaRepository citopatologiaRepository) {
        this.citopatologiaRepository = citopatologiaRepository;
    }

    public List<Citopatologia> findAll() {
        return citopatologiaRepository.findAll();
    }

    public Optional<Citopatologia> findById(Long id) {
        return citopatologiaRepository.findById(id);
    }

    public Citopatologia save(Citopatologia citopatologia) {
        return citopatologiaRepository.save(citopatologia);
    }

    public void delete(Long id) {
        citopatologiaRepository.deleteById(id);
    }

    public List<Citopatologia> findByMascotaId(Long mascotaId) {
        return citopatologiaRepository.findByMascotaId(mascotaId);
    }
}
