package app.controllers;

import app.config.HibernateConfig;
import app.daos.HotelDAO;
import app.daos.IHotelDAO;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.exceptions.NotFoundException;
import app.exceptions.ServerErrorException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;

public class HotelController {
    private final IHotelDAO hotelDAO;
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public HotelController(){
        this.hotelDAO = HotelDAO.getInstance(emf);
    }

    //GET /api/hotel
    public void getAllHotels(Context ctx){
        List<Hotel> hotels = hotelDAO.getAllHotels();
        ctx.json(HotelDTO.toBasicList(hotels)); // for at undgå reccursion, og Lazy-problemer
    }

    //GET /api/hotels/{id}
    public void getHotelById(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Hotel hotel = hotelDAO.getHotelById(id);
        if(hotel == null){
            throw new NotFoundException( "Hotel not found");
        }else {
            ctx.json(HotelDTO.basic(hotel));
        }
    }

    //POST /hotels
    public void createHotel(Context ctx){
        HotelDTO newHotel = ctx.bodyAsClass(HotelDTO.class);
        Hotel savedNewHotel = hotelDAO.createHotel(newHotel.toEntity());
        ctx.status(201).json(new HotelDTO(savedNewHotel));
    }

    //PUT /api/hotels/{id}
    public void updateHotel(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Hotel existingHotel = hotelDAO.getHotelById(id);
        if(existingHotel == null){
            throw new NotFoundException("not found");
        }
        HotelDTO body = ctx.bodyAsClass(HotelDTO.class);
        existingHotel.setName(body.getName());
        existingHotel.setAddress(body.getAddress());

        Hotel finalUpdateHotel = hotelDAO.updateHotel(existingHotel);
        ctx.json(new HotelDTO(finalUpdateHotel));
    }

    //DELETE /api/hotels/{id}
    public void deleteHotel(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Hotel toDelete = hotelDAO.getHotelById(id);
        if(toDelete == null){
           throw new NotFoundException("not found");
        }
            HotelDTO deleted = HotelDTO.basic(toDelete);//laver DTO før vi sletter så vi kan retunere den
            boolean deletedSuccess = hotelDAO.deleteHotel(id);
            if(deletedSuccess){
                ctx.json(deleted);
            }else{
                throw new ServerErrorException( "Delete failed");
            }
    }

}
