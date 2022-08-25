package academy.devdojo.SpringBoot2.client;

import academy.devdojo.SpringBoot2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity(
                "http://localhost:8080/animes/{id}",
                Anime.class,
                5);
        log.info(entity);

        Anime object = new RestTemplate().getForObject(
                "http://localhost:8080/animes/{id}",
                Anime.class,
                5);
        log.info(object);

        Anime[] animes = new RestTemplate().getForObject(
                "http://localhost:8080/animes/all",
                Anime[].class);
        log.info(Arrays.toString(animes));

        ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange(
                "http://localhost:8080/animes/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        log.info(exchange.getBody());

        Anime pokemon = Anime.builder().name("Pokemon").build();
        Anime pokemonSaved = new RestTemplate().postForObject(
                "http://localhost:8080/animes/",
                pokemon,
                Anime.class);
        log.info("Anime Saved {}", pokemonSaved);

        Anime pikachu = Anime.builder().name("pikachu").build();
        ResponseEntity<Anime> pikachuSaved = new RestTemplate().exchange(
                "http://localhost:8080/animes/",
                HttpMethod.POST,
                new HttpEntity<>(pikachu, createJsonHeader()),
                Anime.class);
        log.info("Anime Saved {}", pikachuSaved);


        Anime pikachuUpdate = pikachuSaved.getBody();
        pikachuUpdate.setName("Pikachuu");
        ResponseEntity<Void> pikachuUpdateSaved = new RestTemplate().exchange(
                "http://localhost:8080/animes/",
                HttpMethod.PUT,
                new HttpEntity<>(pikachuUpdate, createJsonHeader()),
                Void.class);
        log.info("Anime Update {}", pikachuUpdateSaved);

        ResponseEntity<Void> pikachuDelete = new RestTemplate().exchange(
                "http://localhost:8080/animes/{id}",
                HttpMethod.DELETE,
                null,
                Void.class, pikachuUpdate.getId());

        log.info("Anime delete {}", pikachuDelete);


    }

    private static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
