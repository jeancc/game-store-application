package com.dynacode.store.whishlist;

import com.dynacode.store.common.BaseEntity;
import com.dynacode.store.game.Game;
import com.dynacode.store.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WishList extends BaseEntity
{
    private String name;

    @OneToOne
    private User user;

    @ManyToMany(mappedBy = "wishLists", fetch = FetchType.EAGER)
    private List<Game> games;
}
