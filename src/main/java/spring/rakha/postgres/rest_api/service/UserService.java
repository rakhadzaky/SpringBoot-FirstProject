package spring.rakha.postgres.rest_api.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import spring.rakha.postgres.rest_api.entity.User;
import spring.rakha.postgres.rest_api.model.RegisterUserRequest;
import spring.rakha.postgres.rest_api.model.UpdateUserRequest;
import spring.rakha.postgres.rest_api.model.UserResponse;
import spring.rakha.postgres.rest_api.repository.UserRepository;
import spring.rakha.postgres.rest_api.security.BCrypt;

import java.util.Objects;
import java.util.Set;

@Slf4j

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void register(RegisterUserRequest request){
        validationService.validate(request);

        if (userRepository.findOneByUsername(request.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);
    }

    public UserResponse get(User user){
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserRequest request) {
        validationService.validate(request);

        log.info(user.getName());

        if (Objects.nonNull(request.getName())) {
            user.setName(request.getName());
        }

        if (Objects.nonNull(request.getPassword())) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }
}
