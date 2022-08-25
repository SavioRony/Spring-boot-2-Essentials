package academy.devdojo.SpringBoot2.util;

import academy.devdojo.SpringBoot2.domain.Anime;

public class AnimeCreator {

    public static Anime createAnimeTobeSaved(){
        return Anime.builder()
                .name("Naruto")
                .build();
    }
    public static Anime createValidAnime(){
        return Anime.builder()
                .name("Naruto")
                .id(1L)
                .build();
    }
    public static Anime createValidUpdatedAnime(){
        return Anime.builder()
                .name("Naruto 2")
                .id(1L)
                .build();
    }
}
