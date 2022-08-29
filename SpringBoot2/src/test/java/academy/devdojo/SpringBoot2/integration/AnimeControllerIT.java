package academy.devdojo.SpringBoot2.integration;

import academy.devdojo.SpringBoot2.domain.Anime;
import academy.devdojo.SpringBoot2.domain.AnimeUser;
import academy.devdojo.SpringBoot2.repository.AnimeRepository;
import academy.devdojo.SpringBoot2.repository.AnimeUserRepository;
import academy.devdojo.SpringBoot2.util.AnimeCreator;
import academy.devdojo.SpringBoot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.SpringBoot2.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
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
    @Qualifier(value = "testeRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testeRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private AnimeUserRepository animeUserRepository;

    private static final AnimeUser USER = AnimeUser.builder()
            .name("Dev dojo")
            .password("{bcrypt}$2a$10$QYYPn3Py.XLIa6cIL8M4SeOf3J/8iCeV/lYSCyrGg5f6urmvNzA36")
            .username("devdojo")
            .authorities("ROLE_USER")
            .build();

    private static final AnimeUser ADMIN = AnimeUser.builder()
            .name("Dev dojo")
            .password("{bcrypt}$2a$10$QYYPn3Py.XLIa6cIL8M4SeOf3J/8iCeV/lYSCyrGg5f6urmvNzA36")
            .username("savio")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testeRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("devdojo","123456");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testeRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("savio","123456");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }
    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnListOfAnimesinsidePageObject_whenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeTobeSaved());

        animeUserRepository.save(USER);

        String expectedName = savedAnime.getName();

        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
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

        animeUserRepository.save(USER);

        String expectedName = savedAnime.getName();
        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);


    }

    @Test
    @DisplayName("Return anime by id when successful")
    void findById_ReturnAnimeById_whenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeTobeSaved());

        animeUserRepository.save(USER);

        Long expectedId = savedAnime.getId();

        Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findByName returns list of anime by name when successful")
    void findByName_ReturnAnimeByName_whenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeTobeSaved());
        animeUserRepository.save(USER);

        String expectedName = savedAnime.getName();
        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animePage = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
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
        animeUserRepository.save(USER);
        List<Anime> animePage = testRestTemplateRoleUser.exchange("/animes/find?name=Naruto",
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
        animeUserRepository.save(USER);
        ResponseEntity<Anime> entity = testRestTemplateRoleUser
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

        animeUserRepository.save(USER);

        ResponseEntity<Void> entity = testRestTemplateRoleUser
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

        animeUserRepository.save(ADMIN);

        ResponseEntity<Void> entity = testRestTemplateRoleAdmin
                .exchange("/animes/admin/{id}",
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        savedAnime.getId());

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_RemovesAnime_whenUserIsNotAdmin(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeTobeSaved());

        animeUserRepository.save(USER);

        ResponseEntity<Void> entity = testRestTemplateRoleUser
                .exchange("/animes/admin/{id}",
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        savedAnime.getId());

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
