package com.dynacode.store.notification;

import com.dynacode.store.common.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification  extends BaseEntity {

    private String message;
    private String receiver;
    private NotificationLevel level;//high, medium, low
    private NotificationStatus status;//sended, reader...
}
