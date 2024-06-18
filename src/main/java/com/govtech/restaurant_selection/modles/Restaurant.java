package com.govtech.restaurant_selection.modles;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="RESTAURANTS")
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String restaurantName;
    @Transient
    private boolean selected;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "userId")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="session_id", nullable=false)
    private RestaurantSelectionSession restaurantSelectionSession;

}
