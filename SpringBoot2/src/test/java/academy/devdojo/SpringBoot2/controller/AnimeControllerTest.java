package academy.devdojo.SpringBoot2.controller;

import academy.devdojo.SpringBoot2.domain.Anime;
import academy.devdojo.SpringBoot2.service.AnimeService;
import academy.devdojo.SpringBoot2.util.AnimeCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

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

        BDDMockito.when(animeService.listAll(ArgumentMatchers.any())).thenReturn(animePage);

        BDDMockito.when(animeService.listAll()).thenReturn(animeList);
        
        BDDMockito.when(animeService.findbyIdOrThrowBadRequestException(1L)).thenReturn(anime);
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

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage).isNotEmpty().hasSize(1);

        Assertions.assertThat(animePage.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("List returns list of anime by id when successful")
    void list_ReturnAnimeById_whenSuccessful(){
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeController.findById(1L).getBody();

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);

    }


}