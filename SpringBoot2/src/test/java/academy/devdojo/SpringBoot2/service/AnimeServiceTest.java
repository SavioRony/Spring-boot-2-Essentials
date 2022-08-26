package academy.devdojo.SpringBoot2.service;

import academy.devdojo.SpringBoot2.domain.Anime;
import academy.devdojo.SpringBoot2.exception.BadRequestException;
import academy.devdojo.SpringBoot2.mapper.AnimeMapper;
import academy.devdojo.SpringBoot2.repository.AnimeRepository;
import academy.devdojo.SpringBoot2.requests.AnimePostRequestBody;
import academy.devdojo.SpringBoot2.requests.AnimePutRequestBody;
import academy.devdojo.SpringBoot2.util.AnimeCreator;
import academy.devdojo.SpringBoot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.SpringBoot2.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks //Usado para as classe que vc esta testando.
    private AnimeService animeService;

    @Mock // Usado nas classes que contem dentro da classe que vc quer testar.
    private AnimeRepository animeRepository;

    @Mock // Usado nas classes que contem dentro da classe que vc quer testar.
    private AnimeMapper animeMapper;


    @BeforeEach
    void setUp(){
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        List<Anime> animeList = List.of(AnimeCreator.createValidAnime());
        Anime anime = AnimeCreator.createValidAnime();

        BDDMockito.when(animeRepository.findAll(any(PageRequest.class))).thenReturn(animePage);

        BDDMockito.when(animeRepository.findAll()).thenReturn(animeList);

        BDDMockito.when(animeRepository.findById(1L)).thenReturn(Optional.of(anime));

        BDDMockito.when(animeRepository.findByName(any())).thenReturn(animeList);

        BDDMockito.when(animeRepository.save(any(Anime.class))).thenReturn(anime);

        BDDMockito.doNothing().when(animeRepository).delete(any(Anime.class));

        BDDMockito.when(animeMapper.toAnime(any(AnimePostRequestBody.class))).thenReturn(anime);

        BDDMockito.when(animeMapper.toAnime(any(AnimePutRequestBody.class))).thenReturn(anime);

    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void listAll_ReturnListOfAnimesinsidePageObject_whenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();

        Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void listAllNonPageable_ReturnListAllOfAnimesInsidePageObject_whenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animePage = animeService.listAllNonPageable();

        Assertions.assertThat(animePage)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("Return anime by id when successful")
    void findbyIdOrThrowBadRequestException_ReturnAnimeById_whenSuccessful(){
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeService.findbyIdOrThrowBadRequestException(1L);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findbyIdOrThrowBadRequestException throws BadRequestException when is not found")
    void findbyIdOrThrowBadRequestException_ThrowsBadRequestException_whenSuccessful(){
        BDDMockito.when(animeRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> animeService.findbyIdOrThrowBadRequestException(1L));

    }

    @Test
    @DisplayName("findByName returns list of anime by name when successful")
    void findByName_ReturnAnimeByName_whenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animePage = animeService.findByName("Naruto");

        Assertions.assertThat(animePage)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findByName returns an empty list of anime is not found")
    void findByName_ReturnEmptyListOfAnime_whenAnimeIsNotFound(){
        BDDMockito.when(animeRepository.findByName(any())).thenReturn(Collections.emptyList());

        List<Anime> animePage = animeService.findByName("Naruto");

        Assertions.assertThat(animePage)
                .isNotNull()
                .isEmpty();

    }

    @Test
    @DisplayName("Save return anime when successful")
    void save_ReturnAnime_whenSuccessful(){

        Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimeTobeSaved());

        Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());

    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful(){

        Assertions.assertThatCode(() ->animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                .doesNotThrowAnyException();

    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_whenSuccessful(){

        Assertions.assertThatCode(() -> animeService.delete(1L))
                .doesNotThrowAnyException();
    }

}