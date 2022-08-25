package academy.devdojo.SpringBoot2.service;

import academy.devdojo.SpringBoot2.domain.Anime;
import academy.devdojo.SpringBoot2.exception.BadRequestException;
import academy.devdojo.SpringBoot2.mapper.AnimeMapper;
import academy.devdojo.SpringBoot2.repository.AnimeRepository;
import academy.devdojo.SpringBoot2.requests.AnimePostRequestBody;
import academy.devdojo.SpringBoot2.requests.AnimePutRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;
    private final AnimeMapper animeMapper;

    public Page<Anime> listAll(Pageable pageable){
        return animeRepository.findAll(pageable);
    }

    public List<Anime> listAllNonPageable() {
        return animeRepository.findAll();
    }

    public List<Anime> findByName(String name){
        return animeRepository.findByName(name);
    }

    public Anime findbyIdOrThrowBadRequestException(long id){
        return animeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Anime not found"));
    }

    @Transactional
    public Anime save(AnimePostRequestBody animePostRequestBody) {
        return animeRepository.save(animeMapper.toAnime(animePostRequestBody));
    }

    public void delete(long id) {
        animeRepository.delete(findbyIdOrThrowBadRequestException(id));
    }

    public void replace(AnimePutRequestBody animePutRequestBody) {
        Anime savedAnime = findbyIdOrThrowBadRequestException(animePutRequestBody.getId());
        Anime anime = animeMapper.toAnime(animePutRequestBody);
        anime.setId(savedAnime.getId());
        animeRepository.save(anime);
    }

}
