package academy.devdojo.SpringBoot2.util;

import academy.devdojo.SpringBoot2.requests.AnimePostRequestBody;

public class AnimePostRequestBodyCreator {

    public static AnimePostRequestBody createAnimeTobeSaved(){
        return AnimePostRequestBody.builder()
                .name(AnimeCreator.createAnimeTobeSaved().getName())
                .build();
    }
}
