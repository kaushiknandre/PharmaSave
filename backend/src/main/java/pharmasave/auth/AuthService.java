package pharmasave.auth;

import pharmasave.dto.LoginRequest;
import pharmasave.dto.RegisterRequest;
import pharmasave.entity.User;
import pharmasave.jwt.JwtService;
import pharmasave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role(User.Role.CASHIER)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return "Invalid username";
        }

        System.out.println("==================================");
        System.out.println("Username: " + request.getUsername());
        System.out.println("Entered Password: " + request.getPassword());
        System.out.println("Stored Password: " + user.getPassword());

        boolean matches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword());

        System.out.println("Password Matches: " + matches);
        System.out.println("==================================");

        if (!matches) {
            return "Invalid password";
        }

        String token = jwtService.generateToken(user.getUsername());

        System.out.println("JWT Generated Successfully");

        return token;
    }
}