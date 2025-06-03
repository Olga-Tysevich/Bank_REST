package com.example.bankcards.entity.notifications;

import com.example.bankcards.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static com.example.bankcards.util.Constants.*;
import static com.example.bankcards.util.Constants.UPDATED_AT_DATE_CANNOT_BE_NULL;

/**
 * This abstract class represents notifications intended for processing by the admin.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class AdminNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_notification_seq")
    @SequenceGenerator(name = "admin_notification_seq", sequenceName = "admin_notification_id_seq", allocationSize = 1)
    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    @NotNull(message = CREATED_AT_DATE_CANNOT_BE_NULL)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    @NotNull(message = UPDATED_AT_DATE_CANNOT_BE_NULL)
    private LocalDateTime updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_user_id", nullable = false)
    @NotNull(message = USER_CANNOT_BE_NULL)
    private User fromUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointed_admin_id", nullable = false)
    @NotNull(message = USER_CANNOT_BE_NULL)
    private User appointedAdmin;

    @Column(name = "note", nullable = false)
    @Builder.Default
    private String note = "";

}
