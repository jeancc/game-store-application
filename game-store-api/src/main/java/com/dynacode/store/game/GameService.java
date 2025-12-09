package com.dynacode.store.game;

import com.dynacode.store.category.CategoryRepository;
import com.dynacode.store.common.PageResponse;
import com.dynacode.store.platform.Console;
import com.dynacode.store.platform.Platform;
import com.dynacode.store.platform.PlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor //con esto, crea constructor con los campos final
@Slf4j
public class GameService
{
    //inyecciones
    private final GameRepository gameRepository;
    private final PlatformRepository platformRepository;
    private final CategoryRepository categoryRepository;
    private final GameMapper gameMapper;


    public String saveGame(final GameRequest gameRequest)
    {
        if ( gameRepository.existsByTitle(gameRequest.title()) )
        {
            log.warn("Title '{}' already exists", gameRequest.title());
            throw new RuntimeException("Title already exists");//TODO: ya se creará nuestra propia excepción
        }

        //option 1: loop over the list of platforms inside gameRequest and fetch
        //one by one from DB:
        //select * from platform where console = 'PS'

        //option 2: mapping the platforms (from the request) to Platform objects and fetch all of them:
        //select * from platform where console in ('PS', 'XBOX')
        final List<Console> selectedConsoles = gameRequest.platforms()
                                                            .stream()
                                                            .map( p -> Console.valueOf(p))
                                                            .toList();

        final List<Platform> platforms = platformRepository.findAllByConsoleIn(selectedConsoles);

        if (selectedConsoles.size() != platforms.size())
        {
            log.warn("Received non supported Platforms" +
                        "Received: {} - Stored: {}", selectedConsoles, platforms);
            throw new RuntimeException("Platform not supported");//TODO: ya se creará nuestra propia excepción
        }


        if( ! categoryRepository.existsById(gameRequest.categoryId()))
        {
            log.warn("Category '{}' not exists", gameRequest.categoryId());
            throw new RuntimeException("Category not exists");//TODO: ya se creará nuestra propia excepción
        }


        final Game game = gameMapper.toGame(gameRequest);
        game.setPlatforms(platforms);
        final Game savedGame = gameRepository.save(game);

        return savedGame.getId();
    }

    public void updateGame(String gameId, GameRequest gameRequest)
    {

    }

    public String uploadGameImage(MultipartFile file, String gameId)
    {
        return null;
    }

    //El resultado será paginado
    public PageResponse<GameResponse> findAllGames(int page, int size)
    {
        return null;
    }

    public void deleteGame(String gameId)
    {

    }

}
