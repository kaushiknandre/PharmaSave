package pharmasave.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pharmasave.entity.Category;
import pharmasave.entity.UserInteraction;
import pharmasave.repository.UserInteractionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserInteractionRepository interactionRepository;

    public Category getFavouriteCategory(Long userId) {

        List<UserInteraction> interactions =
                interactionRepository.findByUserId(userId);

        if (interactions.isEmpty()) {
            return null;
        }

        Map<Category, Integer> scores = new HashMap<>();

        for (UserInteraction interaction : interactions) {

            Category category = interaction.getCategory();

            scores.put(
                    category,
                    scores.getOrDefault(category, 0) + 1
            );
        }

        Category favourite = null;
        int maxScore = 0;

        for (Map.Entry<Category, Integer> entry : scores.entrySet()) {

            if (entry.getValue() > maxScore) {

                maxScore = entry.getValue();
                favourite = entry.getKey();

            }
        }

        return favourite;
    }
}