package mivet.service;

import mivet.model.HistorialClinico;
import mivet.repository.HistoriaClinicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistoriaClinicaService {

    private final HistoriaClinicaRepository historiaClinicaRepository;

    @Autowired
    public HistoriaClinicaService(HistoriaClinicaRepository historiaClinicaRepository) {
        this.historiaClinicaRepository = historiaClinicaRepository;
    }

    public List<HistorialClinico> findAll() {
        return historiaClinicaRepository.findAll();
    }

    public Optional<HistorialClinico> findById(Long id) {
        return historiaClinicaRepository.findById(id);
    }

    public HistorialClinico save(HistorialClinico historialClinico) {
        return historiaClinicaRepository.save(historialClinico);
    }

    public void delete(Long id) {
        historiaClinicaRepository.deleteById(id);
    }

    public List<HistorialClinico> findByMascotaId(Long mascotaId) {
        return historiaClinicaRepository.findByMascotaId(mascotaId);
    }
}
