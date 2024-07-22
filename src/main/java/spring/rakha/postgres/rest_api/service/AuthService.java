package spring.rakha.postgres.rest_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import spring.rakha.postgres.rest_api.entity.User;
import spring.rakha.postgres.rest_api.model.LoginUserRequest;
import spring.rakha.postgres.rest_api.model.TokenResponse;
import spring.rakha.postgres.rest_api.repository.UserRepository;
import spring.rakha.postgres.rest_api.security.BCrypt;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findOneByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password Wrong"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password Wrong");
        }

        user.setToken(UUID.randomUUID().toString());
        user.setTokenExpiredAt(next30Days());
        userRepository.save(user);

        return TokenResponse.builder()
                .token(user.getToken())
                .expiredAt(user.getTokenExpiredAt())
                .build();
    }

    @Transactional
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }

    private Long next30Days(){
        return System.currentTimeMillis() + (1000 * 60 * 24 * 30);
    }
}
