package app.daos;

import app.entities.Hotel;
import app.entities.Room;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.temporal.TemporalField;
import java.util.List;

public class HotelDAO implements IHotelDAO{
    private static EntityManagerFactory emf;
    private static HotelDAO instance;

    public HotelDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static HotelDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HotelDAO(emf);
        }
        return instance;
    }

    @Override
    public Hotel createHotel(Hotel hotel) {
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(hotel);
            em.getTransaction().commit();
            return hotel;
        }
    }

    @Override
    public List<Hotel> getAllHotels() {
        try (EntityManager em = emf.createEntityManager()){
            TypedQuery<Hotel> query = em.createQuery("SELECT h FROM Hotel h", Hotel.class);
            return query.getResultList();
        }
    }

    @Override
    public Hotel getHotelById(Integer id) {
        try (EntityManager em = emf.createEntityManager()){
            return em.find(Hotel.class, id);
        }
    }

    @Override
    public Hotel updateHotel(Hotel hotel) {
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Hotel updatedHotel = em.merge(hotel);
            em.getTransaction().commit();
            return updatedHotel;
        }
    }

    @Override
    public boolean deleteHotel(Integer id) {
        try (EntityManager em = emf.createEntityManager()){
            Hotel hotelToDelete = em.find(Hotel.class, id);
            if(hotelToDelete != null){
                em.getTransaction().begin();
                em.remove(hotelToDelete);
                em.getTransaction().commit();
                return true;
            }else{
                return false;
            }
        }
    }

    @Override
    public void addRoom(Hotel hotel, Room room) {
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Hotel h = em.getReference(Hotel.class, hotel.getId());
            room.setHotel(h);
            em.persist(room);
            h.getRooms().add(room);
            em.getTransaction().commit();
        }
    }

    @Override
    public void removeRoom(Hotel hotel, Room room) {
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Room r = em.find(Room.class, room.getId());
            if (r != null) em.remove(r);
            em.getTransaction().commit();
        }
    }

    @Override
    public List<Room> getRoomsForHotel(Hotel hotel) {
        try (EntityManager em = emf.createEntityManager()){
            return em.createQuery("SELECT r FROM Room r WHERE r.hotel.id = :id", Room.class)
                    .setParameter("id", hotel.getId())
                    .getResultList();
        }
    }

}
