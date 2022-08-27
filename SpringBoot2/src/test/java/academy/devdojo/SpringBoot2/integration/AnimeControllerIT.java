package academy.devdojo.SpringBoot2.integration;

import academy.devdojo.SpringBoot2.domain.Anime;
import academy.devdojo.SpringBoot2.repository.AnimeRepository;
import academy.devdojo.SpringBoot2.util.AnimeCreator;
import academy.devdojo.SpringBoot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.SpringBoot2.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort //Pega a porta atual usada para teste
    private int port;

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnListOfAnimesinsidePageObject_whenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeTobeSaved());

        String expectedName = savedAnime.getName();

        PageableResponse<Anime> animePage = testRestTemplate.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnListAllOfAnimesInsidePageObject_whenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeTobeSaved());

        String expectedName = savedAnime.getName();
        List<Anime> animes = testRestTemplate.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);


    }

    @Test
    @DisplayName("Return anime by id when successful")
    void findById_ReturnAnimeById_whenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeTobeSaved());

        Long expectedId = savedAnime.getId();

        Anime anime = testRestTemplate.getForObject("/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findByName returns list of anime by name when successful")
    void findByName_ReturnAnimeByName_whenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeTobeSaved());
        String expectedName = savedAnime.getName();
        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animePage = testRestTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody() ;

        Assertions.assertThat(animePage)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findByName returns an empty list of anime is not found")
    void findByName_ReturnEmptyListOfAnime_whenAnimeIsNotFound(){
        List<Anime> animePage = testRestTemplate.exchange("/animes/find?name=Naruto",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody() ;

        Assertions.assertThat(animePage)
                .isNotNull()
                .isEmpty();

        Assertions.assertThat(animePage)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Save return anime when successful")
    void save_ReturnAnime_whenSuccessful(){
        ResponseEntity<Anime> entity = testRestTemplate
                .postForEntity("/animes",
                        AnimePostRequestBodyCreator.createAnimeTobeSaved(),
                        Anime.class);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(entity.getBody()).isNotNull();
        Assertions.assertThat(entity.getBody().getId()).isNotNull();

    }

    @Test
    @DisplayName("replace anime when successful")
    void replace_UpdateAnime_whenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeTobeSaved());
        savedAnime.setName("new name");

        ResponseEntity<Void> entity = testRestTemplate
                .exchange("/animes",
                        HttpMethod.PUT,
                        new HttpEntity<>(savedAnime),
                        Void.class);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_whenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeTobeSaved());

        ResponseEntity<Void> entity = testRestTemplate
                .exchange("/animes/{id}",
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        savedAnime.getId());

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
