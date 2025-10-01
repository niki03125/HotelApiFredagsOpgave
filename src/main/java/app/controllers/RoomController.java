package app.controllers;

import app.config.HibernateConfig;
import app.daos.HotelDAO;
import app.daos.IHotelDAO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;

public class RoomController {
    private final IHotelDAO hotelDAO;
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public RoomController() {
         this.hotelDAO = HotelDAO.getInstance(emf);
    }

    //GET /hotel/{id}/rooms
    public void getRoomsForHotel(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Hotel hotel = hotelDAO.getHotelById(id);
        if(hotel == null){
            ctx.status(404).json(Map.of("message","Hotel not found"));
            return;
        }
        List<Room> rooms = hotelDAO.getRoomsForHotel(hotel);
        ctx.json(RoomDTO.toRoomDTOList(rooms));
    }


}
