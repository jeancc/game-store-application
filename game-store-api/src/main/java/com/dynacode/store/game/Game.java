package com.dynacode.store.game;

import com.dynacode.store.category.Category;
import com.dynacode.store.comment.Comment;
import com.dynacode.store.common.BaseEntity;
import com.dynacode.store.platform.Console;
import com.dynacode.store.platform.Platform;
import com.dynacode.store.whishlist.WishList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
public class Game extends BaseEntity
{
    @Column(nullable = false, unique = true)
    private String title;

    @ManyToMany(cascade =  {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<Platform> platforms;

    private String coverPicture;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "game")
    ///@OrderBy(value = "content")
    private List<Comment> comments;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "game_wishlist",
            joinColumns ={
                    @JoinColumn( name = "game_id" )//primero la columna de esta clase
            },
            inverseJoinColumns = {
                    @JoinColumn( name = "wishlist_id" )
            }
    )
    private List<WishList> wishLists;

    /**
     * Hay que añadir al array, y enlazr el objeto simple. Es como se enlazan
     * objetos en relaciones N-N
     * @param wishList
     */
    public void addWishList(WishList wishList)
    {
        if( ! this.wishLists.contains(wishList)) {

            //Añadir las dos partes de la tabla que representa la relación N-N
            this.wishLists.add(wishList);
            wishList.getGames().add(this);
        }
    }


    public void removeWishList(WishList wishList)
    {
        if(this.wishLists.contains(wishList))
        {
            this.wishLists.remove(wishList);
            wishList.getGames().remove(this);
        }
    }

    public void addPlatform(Platform platform)
    {
        this.platforms.add(platform);
        platform.getGames().add(this);
    }

    public void removePlatform(Platform platform)
    {
        this.platforms.remove(platform);
        platform.getGames().remove(this);
    }

}
