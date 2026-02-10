package com.dynacode.store.game;

import com.dynacode.store.category.CategoryRepository;
import com.dynacode.store.comment.CommentRepository;
import com.dynacode.store.common.PageResponse;
import com.dynacode.store.platform.Console;
import com.dynacode.store.platform.Platform;
import com.dynacode.store.platform.PlatformRepository;
import com.dynacode.store.whishlist.WishListRepository;
import jakarta.transaction.Transactional;
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
    private final CommentRepository commentRepository;
    private final WishListRepository wishListRepository;
    private final GameMapper gameMapper;


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


    /**
     *
     * @param gameId
     * @param confirm
     */
    @Transactional
    public void deleteGame(String gameId, boolean confirm)
    {
        //Cogemos de la base de datos el objeto 'Game'
        Game game = gameRepository.findById(gameId)
                .orElseThrow(()-> new RuntimeException("Game not found"));

        //Hay que recordar que antes de eliminar el juego, tenemos que hacer varias cosas:
        // tendremos que eliminar los comentarios asociados a ese juego.
        // tendremos que eliminar el juego de las listas de deseo donde esté.

        long commentsCount = commentRepository.countByGameId(gameId);
        long wishListCount = wishListRepository.countByGameId(gameId);

        final List<String> warnings = new ArrayList<>();

        if ( commentsCount > 0 )
        {
            warnings.add("Comments count is greater than 0");
            System.out.println("The current game has comments: " + commentsCount);
        }
        if ( wishListCount > 0 )
        { 
            warnings.add("Wishlists count is greater than 0");
            System.out.println("The current game has wishlist: " + wishListCount);
        }

        if (warnings.size() > 0 && ! confirm)
        {
            //TODO: Crear pedo personalizado
            throw new RuntimeException("One or more warnings were found");
        }
        else
        {
            //Esto elimina el juego y los comentarios del juego, porque
            //en la entidad Game la relación con comentarios tiene esto: orphanRemoval = true
            gameRepository.deleteById(gameId);

            //TODO: quitar el juego de las wishlist



        }
    }//fin método 'deleteGame'

}
