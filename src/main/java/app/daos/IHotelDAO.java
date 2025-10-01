package app.daos;

import app.entities.Hotel;
import app.entities.Room;

import java.util.List;

public interface IHotelDAO extends IDAO<Hotel,Integer> {
    void addRoom(Hotel hotel, Room room);
    void removeRoom(Hotel hotel, Room room);
    List<Room> getRoomsForHotel(Hotel hotel);
}
