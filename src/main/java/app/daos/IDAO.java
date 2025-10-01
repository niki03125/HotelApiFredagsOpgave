package app.daos;

import java.util.List;

public interface IDAO<T, I> {

    T createHotel(T t);
    List<T> getAllHotels();
    T getHotelById(I id);
    T updateHotel(T t);
    boolean deleteHotel(I id);
}