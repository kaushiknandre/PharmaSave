package pharmasave.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pharmasave.dto.UserInteractionRequest;
import pharmasave.entity.Medicine;
import pharmasave.entity.User;
import pharmasave.entity.UserInteraction;
import pharmasave.exception.ResourceNotFoundException;
import pharmasave.repository.MedicineRepository;
import pharmasave.repository.UserInteractionRepository;
import pharmasave.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserInteractionService {

    private final UserInteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;

    public void saveInteraction(UserInteractionRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Medicine not found"));

        UserInteraction interaction = UserInteraction.builder()
                .user(user)
                .medicine(medicine)
                .category(medicine.getCategory())
                .interactionType(
                        UserInteraction.InteractionType.valueOf(
                                request.getInteractionType().name()))
                .build();

        interactionRepository.save(interaction);
    }
}