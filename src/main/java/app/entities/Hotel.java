package app.entities;

import jakarta.persistence.*;
import org.hibernate.persister.collection.mutation.UpdateRowsCoordinatorOneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString (exclude = "rooms") // undgår recusion
@Builder

@Entity
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    // skal ikke have en  "@Column(nullable = false)" da det er en oneToMany relation
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)//orphanRemoval sletter direkte i DB
    private List<Room> rooms = new ArrayList<>();

    public Hotel(String name, String address) {
        this.name = name;
        this.address = address;
        this.rooms = new ArrayList<>();

    }
}
