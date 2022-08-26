package academy.devdojo.SpringBoot2.mapper;

import academy.devdojo.SpringBoot2.domain.Anime;
import academy.devdojo.SpringBoot2.requests.AnimePostRequestBody;
import academy.devdojo.SpringBoot2.requests.AnimePutRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AnimeMapper {
    AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);
    Anime toAnime(AnimePostRequestBody animePostRequestBody);
    Anime toAnime(AnimePutRequestBody animePutRequestBody);
}
