package academy.devdojo.SpringBoot2.controller;

import academy.devdojo.SpringBoot2.domain.Anime;
import academy.devdojo.SpringBoot2.requests.AnimePostRequestBody;
import academy.devdojo.SpringBoot2.requests.AnimePutRequestBody;
import academy.devdojo.SpringBoot2.service.AnimeService;
import academy.devdojo.SpringBoot2.util.AnimeCreator;
import academy.devdojo.SpringBoot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.SpringBoot2.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks //Usado para as classe que vc esta testando.
    private AnimeController animeController;

    @Mock // Usado nas classes que contem dentro da classe que vc quer testar.
    private AnimeService animeService;


    @BeforeEach
    void setUp(){
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        List<Anime> animeList = List.of(AnimeCreator.createValidAnime());
        Anime anime = AnimeCreator.createValidAnime();

        BDDMockito.when(animeService.listAll(any())).thenReturn(animePage);

        BDDMockito.when(animeService.listAllNonPageable()).thenReturn(animeList);
        
        BDDMockito.when(animeService.findbyIdOrThrowBadRequestException(1L)).thenReturn(anime);

        BDDMockito.when(animeService.findByName(any())).thenReturn(animeList);

        BDDMockito.when(animeService.save(any(AnimePostRequestBody.class))).thenReturn(anime);

        BDDMockito.doNothing().when(animeService).replace(any(AnimePutRequestBody.class));

        BDDMockito.doNothing().when(animeService).delete(anyLong());
    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnListOfAnimesinsidePageObject_whenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeController.list(null).getBody();

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnListAllOfAnimesInsidePageObject_whenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animePage = animeController.listAll().getBody();

        Assertions.assertThat(animePage)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("Return anime by id when successful")
    void findById_ReturnAnimeById_whenSuccessful(){
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeController.findById(1L).getBody();

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findByName returns list of anime by name when successful")
    void findByName_ReturnAnimeByName_whenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animePage = animeController.findByName("Naruto").getBody();

        Assertions.assertThat(animePage)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findByName returns an empty list of anime is not found")
    void findByName_ReturnEmptyListOfAnime_whenAnimeIsNotFound(){
        BDDMockito.when(animeService.findByName(any())).thenReturn(Collections.emptyList());

        List<Anime> animePage = animeController.findByName("Naruto").getBody();

        Assertions.assertThat(animePage)
                .isNotNull()
                .isEmpty();

    }

    @Test
    @DisplayName("Save return anime when successful")
    void save_ReturnAnime_whenSuccessful(){

        Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimeTobeSaved()).getBody();

        Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());

    }

    @Test
    @DisplayName("replace anime when successful")
    void replace_UpdateAnime_whenSuccessful(){

        Assertions.assertThatCode(() -> animeController.replace(AnimePutRequestBodyCreator.createAnimeTobeSaved()).getBody())
                .doesNotThrowAnyException();

        ResponseEntity<Anime> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimeTobeSaved());

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_whenSuccessful(){

        Assertions.assertThatCode(() -> animeController.delete(1L))
                .doesNotThrowAnyException();

        ResponseEntity<Void> entity = animeController.delete(1L);

        Assertions.assertThat(entity).isNotNull();

        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}