package com.govtech.restaurant_selection.modles;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="USERS")
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(unique = true)
    private String username;
    private String firstName;
    private String lastName;
    private String password;

}
