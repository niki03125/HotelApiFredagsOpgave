package app.dtos;

import app.entities.Hotel;
import app.entities.Room;
import lombok.*;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDTO {
    private int id;
    private int hotelId;
    private String number;//fx "201A"
    private int price;

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.number = room.getNumber();
        this.price = room.getPrice();
        if(room.getHotel() != null){
            this.hotelId = room.getHotel().getId();
        }
    }

    public static List<RoomDTO> toRoomDTOList(List<Room> rooms){
        return rooms.stream().map(RoomDTO::new).toList();
    }

    public Room toEntity(Hotel hotel){
        Room room = new Room();
        room.setId(this.id);
        room.setNumber(this.number);
        room.setPrice(this.price);
        room.setHotel(hotel);
        return room;
    }
}
