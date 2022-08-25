package academy.devdojo.SpringBoot2.controller;


import academy.devdojo.SpringBoot2.domain.Anime;
import academy.devdojo.SpringBoot2.requests.AnimePostRequestBody;
import academy.devdojo.SpringBoot2.requests.AnimePutRequestBody;
import academy.devdojo.SpringBoot2.service.AnimeService;
import academy.devdojo.SpringBoot2.util.Dateutil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("animes")
public class AnimeController {

    private final Dateutil dateutil;
    private final AnimeService animeService;

    @GetMapping
    public ResponseEntity<Page<Anime>> list(Pageable pageable){
        return ResponseEntity.ok(animeService.listAll(pageable)) ;
    }
    @GetMapping(path = "/all")
    public ResponseEntity<List<Anime>> listAll(){
        return ResponseEntity.ok(animeService.listAllNonPageable()) ;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById(@PathVariable long id){
        return ResponseEntity.ok(animeService.findbyIdOrThrowBadRequestException(id)) ;
    }

    @GetMapping(path = "/find")
    public ResponseEntity<List<Anime>> findByName(@RequestParam(required = false) String name){
        return ResponseEntity.ok(animeService.findByName(name));
    }

    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody animePostRequestBody){
       return new ResponseEntity<>(animeService.save(animePostRequestBody), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        animeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT) ;
    }

    @PutMapping
    public ResponseEntity<Anime> replace(@RequestBody @Valid AnimePutRequestBody animePutRequestBody){
        animeService.replace(animePutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT) ;
    }
}