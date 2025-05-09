package mivet.service;

import mivet.model.Analitica;
import mivet.repository.AnaliticaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnaliticaService {

    private final AnaliticaRepository analiticaRepository;

    @Autowired
    public AnaliticaService(AnaliticaRepository analiticaRepository) {
        this.analiticaRepository = analiticaRepository;
    }

    public List<Analitica> findAll() {
        return analiticaRepository.findAll();
    }

    public Optional<Analitica> findById(Long id) {
        return analiticaRepository.findById(id);
    }

    public List<Analitica> findByMascotaId(Long mascotaId) {
        return analiticaRepository.findByMascotaId(mascotaId);
    }

    public Analitica save(Analitica analitica) {
        return analiticaRepository.save(analitica);
    }

    public void delete(Long id) {
        analiticaRepository.deleteById(id);
    }
}
