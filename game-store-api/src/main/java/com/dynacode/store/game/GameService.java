package com.dynacode.store.game;

import com.dynacode.store.category.CategoryRepository;
import com.dynacode.store.common.PageResponse;
import com.dynacode.store.platform.Console;
import com.dynacode.store.platform.Platform;
import com.dynacode.store.platform.PlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final ListableBeanFactory listableBeanFactory;


    public String saveGame(final GameRequest gameRequest)
    {
        if ( gameRepository.existsByTitle(gameRequest.title()) )
        {
            log.warn("Title '{}' already exists", gameRequest.title());
            throw new RuntimeException("Title already exists");//TODO: ya se creará nuestra propia excepción
        }

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


    /**
     *
     * @param gameId
     * @param gameRequest
     */
    public void updateGame(String gameId, GameRequest gameRequest)
    {
        //Cogemos de la base de datos el objeto 'Game'
        Game game = gameRepository.findById(gameId)
                .orElseThrow(()-> new RuntimeException("Game not found"));

        if ( ! game.getTitle ( ).equals ( gameRequest.title ( ) ) &&
                gameRepository.existsByTitle(gameRequest.title ( ) ) )
        {
            log.warn("Title '{}' already exists", gameRequest.title());
            throw new RuntimeException("Title already exists");//TODO: ya se creará nuestra propia excepción
        }

        //region
        //coger la lsita de consolas de la petición
        final List<Console> selectedConsoles = gameRequest.platforms()
                .stream()
                .map( p -> Console.valueOf(p))
                .toList();

        //buscar en bd la plataforma de esas consolas
        final List<Platform> platforms = platformRepository.findAllByConsoleIn(selectedConsoles);

        if (selectedConsoles.size() != platforms.size())
        {
            log.warn("Received non supported Platforms" +
                    "Received: {} - Stored: {}", selectedConsoles, platforms);
            throw new RuntimeException("Platform not supported");//TODO: ya se creará nuestra propia excepción
        }

        //obtener el ID de esas plataformas
        final List<String> platformIds = platforms.stream()
                .map(Platform::getId)
                .collect(Collectors.toList());
        //endregion

        List<Platform> currentPlatforms = game.getPlatforms();
        List<Platform> newPlatforms = platformRepository.findAllById(platformIds);

        List<Platform> platformToAdd = new ArrayList<>(newPlatforms);
        platformToAdd.removeAll(currentPlatforms);

        List<Platform> platformToRemove = new ArrayList<>(currentPlatforms);
        platformToRemove.removeAll(newPlatforms);

        for(Platform platform: platformToAdd)
        {
            game.addPlatform(platform);
        }
        for(Platform platform: platformToRemove)
        {
            game.removePlatform(platform);
        }

        game.setTitle(gameRequest.title());

        //¿Category?

        gameRepository.save(game);
    }

    public String uploadGameImage(MultipartFile file, String gameId)
    {
        return null;
    }

    /**
     * El resultado será paginado
     * @param page
     * @param size
     * @return
     */
    public PageResponse<GameResponse> findAllGames ( int page, int size )
    {
        Pageable pageable = PageRequest.of ( page, size );

        Page<Game> gamesPage = gameRepository.findAll(pageable);

        List<GameResponse> gameResponses = gamesPage.stream()
                                                    .map(this.gameMapper::toGameResponse)
                                                    .toList();

        //realmente se ha creado el objeto 'PageResponse' para no devovler el
        //objeto 'Page' completo, que tiene muchas propiedades que no las necesitamos
        return PageResponse.<GameResponse>builder()
                .content(gameResponses)
                .pageNumber(gamesPage.getNumber())
                .size(gamesPage.getSize())
                .totalElements(gamesPage.getTotalElements())
                .totalPages(gamesPage.getTotalPages())
                .isFirst(gamesPage.isFirst())
                .isLast(gamesPage.isLast())
                .build();
    }

    public void deleteGame(String gameId)
    {

    }

}
