package com.dynacode.store.game;


import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameResponse {

    private String id;
    private String title;
    private Set<String> platforms;
    private String imageUrl;//the CDN url
}
