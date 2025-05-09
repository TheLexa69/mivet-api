package mivet.service;

import mivet.model.Auth;
import mivet.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    @Autowired
    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Auth save(Auth auth) {
        return authRepository.save(auth);
    }

    public Optional<Auth> findById(Long id) {
        return authRepository.findById(id);
    }

    public Optional<Auth> findByToken(String token) {
        return authRepository.findByToken(token);
    }

    public void delete(Long id) {
        authRepository.deleteById(id);
    }
}
