package mivet.service;

import mivet.model.Gasto;
import mivet.model.Mascota;
import mivet.repository.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GastoService {

    private final GastoRepository gastoRepository;

    @Autowired
    public GastoService(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    public List<Gasto> findAll() {
        return gastoRepository.findAll();
    }

    public Optional<Gasto> findById(Long id) {
        return gastoRepository.findById(id);
    }

    public Gasto save(Gasto gasto) {
        return gastoRepository.save(gasto);
    }

    public void delete(Long id) {
    Optional<Gasto> gastoOpt = gastoRepository.findById(id);
    if (gastoOpt.isPresent()) {
        Gasto gasto = gastoOpt.get();
        Mascota mascota = gasto.getMascota();

        if (mascota != null) {
            mascota.getGastos().remove(gasto);
        }

        gastoRepository.deleteById(id);
    }
}


    public List<Gasto> findByMascotaId(Long mascotaId) {
        return gastoRepository.findByMascotaId(mascotaId);
    }
}
