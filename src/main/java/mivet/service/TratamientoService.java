package mivet.service;

import mivet.model.Tratamiento;
import mivet.repository.TratamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TratamientoService {

    private final TratamientoRepository tratamientoRepository;

    @Autowired
    public TratamientoService(TratamientoRepository tratamientoRepository) {
        this.tratamientoRepository = tratamientoRepository;
    }

    public List<Tratamiento> findAll() {
        return tratamientoRepository.findAll();
    }

    public Optional<Tratamiento> findById(Integer id) {
        return tratamientoRepository.findById(id);
    }

    public Tratamiento save(Tratamiento tratamiento) {
        return tratamientoRepository.save(tratamiento);
    }

    public void delete(Integer id) {
        tratamientoRepository.deleteById(id);
    }

    public List<Tratamiento> findByMascotaId(Integer mascotaId) {
        return tratamientoRepository.findByMascotaId(mascotaId);
    }
}
