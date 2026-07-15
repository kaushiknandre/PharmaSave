package pharmasave.dto;

import lombok.Data;

@Data
public class UserInteractionRequest {

    private Long medicineId;

    private UserInteractionType interactionType;

    public enum UserInteractionType {
        VIEW,
        CLICK,
        SEARCH
    }
}