package academy.devdojo.SpringBoot2.util;

import academy.devdojo.SpringBoot2.requests.AnimePutRequestBody;

public class AnimePutRequestBodyCreator {

    public static AnimePutRequestBody createAnimeTobeSaved(){
        return AnimePutRequestBody.builder()
                .id(AnimeCreator.createAnimeTobeSaved().getId())
                .name(AnimeCreator.createAnimeTobeSaved().getName())
                .build();
    }
}
