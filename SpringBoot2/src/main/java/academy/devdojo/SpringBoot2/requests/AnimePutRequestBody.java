package academy.devdojo.SpringBoot2.requests;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class AnimePutRequestBody {

    private Long id;

    @NotEmpty(message = "The anime name cannot be empty")
    private String name;
}
