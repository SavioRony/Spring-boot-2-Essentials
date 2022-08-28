package academy.devdojo.SpringBoot2.repository;

import academy.devdojo.SpringBoot2.domain.Anime;
import academy.devdojo.SpringBoot2.domain.AnimeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AnimeUserRepository extends JpaRepository<AnimeUser, Long> {
    AnimeUser findByUsername(String username);
}
