package com.dynacode.store.gamerequest;

import com.dynacode.store.common.BaseEntity;
import com.dynacode.store.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GameRequestEntity extends BaseEntity {

    private String title;

    @Enumerated(EnumType.STRING)
    private RequestStatus status; //pending, requested...

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
