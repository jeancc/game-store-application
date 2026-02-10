package com.dynacode.store.whishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WishListRepository extends JpaRepository<WishList, String> {

    @Query("""
    select count(w)
        from WishList w
        join  w.games g /*porque la entidad wishtlist tiene un array 'games'*/
        where g.id = :gameId
    """)
    long countByGameId(String gameId);
}
