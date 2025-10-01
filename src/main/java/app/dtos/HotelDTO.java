package app.dtos;

import app.entities.Hotel;
import app.entities.Room;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelDTO {
    private int id;
    private String name;
    private String address;
    private List<RoomDTO> rooms;

    public HotelDTO(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();

        if ( hotel.getRooms() !=null) {
            this.rooms = new ArrayList<>();
            for (Room room : hotel.getRooms()) {
                this.rooms.add(new RoomDTO(room));

            }
        }
    }

    public static List<HotelDTO> toHotelDTOList(List<Hotel> hotels){
        return hotels.stream().map(HotelDTO::new).collect(Collectors.toList());
    }

    public Hotel toEntity(){
        Hotel hotel = new Hotel();
        hotel.setId(this.id);
        hotel.setName(this.name);
        hotel.setAddress(this.address);
        hotel.setRooms(new ArrayList<>());

        if(this.rooms != null){
            for(RoomDTO roomDTO : this.rooms){
                Room room = roomDTO.toEntity(hotel);
                hotel.getRooms().add(room);
            }
        }
        return hotel;
    }

    public static HotelDTO basic(Hotel h) {
        return HotelDTO.builder()
                .id(h.getId())
                .name(h.getName())
                .address(h.getAddress())
                .build(); // ingen rooms!
    }

    public static List<HotelDTO> toBasicList(List<Hotel> hotels) {
        return hotels.stream().map(HotelDTO::basic).toList();
    }

}
