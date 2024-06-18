package com.govtech.restaurant_selection.modles;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;

@Setter
@Getter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="SESSIONS")
@Builder
public class RestaurantSelectionSession {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private boolean active;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "userId")
    private UserEntity owner;
    @CreationTimestamp
    private LocalDateTime createdOn;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "selected_restaurant_id", referencedColumnName = "id")
    private Restaurant selectedRestaurant;

    @OneToMany(cascade=ALL, mappedBy="restaurantSelectionSession", fetch = FetchType.EAGER)
    private List<Restaurant> restaurants;

    public void addRestaurant(Restaurant restaurant) {
        if (this.restaurants == null) {
            this.restaurants = new ArrayList<>();
        }
        this.restaurants.add(restaurant);
    }

}
