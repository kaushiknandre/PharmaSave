package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DemoDataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PromotionIngestionService ingestionService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initialize() {
        if (!userRepository.existsByUsername("demo")) {
            userRepository.save(User.builder()
                    .username("demo")
                    .email("demo@pharmasave.local")
                    .fullName("Demo Customer")
                    .phoneNumber("0000000000")
                    .password(passwordEncoder.encode("Demo@123"))
                    .role(User.Role.CUSTOMER)
                    .isActive(true)
                    .build());
        }
        ingestionService.ingestDemoPayload();
    }
}
