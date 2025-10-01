package app.entities;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private String number;//fx "201A"

    @Column(nullable = false)
    private int price;

    public Room(String number, int price) {
        this.number = number;
        this.price = price;
    }
}
