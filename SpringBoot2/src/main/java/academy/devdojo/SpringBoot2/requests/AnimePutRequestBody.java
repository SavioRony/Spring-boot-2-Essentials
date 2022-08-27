package academy.devdojo.SpringBoot2.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePutRequestBody {

    private Long id;

    @NotEmpty(message = "The anime name cannot be empty")
    private String name;
}
