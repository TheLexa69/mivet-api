package mivet.service;

import mivet.model.PruebaImagen;
import mivet.repository.PruebaImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PruebaImagenService {

    private final PruebaImagenRepository pruebaImagenRepository;

    @Autowired
    public PruebaImagenService(PruebaImagenRepository pruebaImagenRepository) {
        this.pruebaImagenRepository = pruebaImagenRepository;
    }

    public List<PruebaImagen> findAll() {
        return pruebaImagenRepository.findAll();
    }

    public Optional<PruebaImagen> findById(Long id) {
        return pruebaImagenRepository.findById(id);
    }

    public PruebaImagen save(PruebaImagen pruebaImagen) {
        return pruebaImagenRepository.save(pruebaImagen);
    }

    public void delete(Long id) {
        pruebaImagenRepository.deleteById(id);
    }

    public List<PruebaImagen> findByMascotaId(Long mascotaId) {
        return pruebaImagenRepository.findByMascotaId(mascotaId);
    }
}
