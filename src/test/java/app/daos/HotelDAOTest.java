package app.daos;

import app.config.HibernateConfig;
import app.entities.Hotel;
import app.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HotelDAOTest {

    private static EntityManagerFactory emf;
    private static HotelDAO hotelDAO;

    Hotel h1, h2, h3;

    @BeforeAll
    static void setUpAll(){
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        hotelDAO = new HotelDAO(emf);
    }


    @BeforeEach
    void setUp(){
        EntityManager em = emf.createEntityManager();

        //slet alt i db og reset id nr
        em.getTransaction().begin();
        em.createNativeQuery("TRUNCATE TABLE room, hotel RESTART IDENTITY CASCADE").executeUpdate();
        em.getTransaction().commit();

        //populere db
        em.getTransaction().begin();
        h1 = new Hotel("Hotel Califonia", "Sunset Boulevard 1");
        Room r1 = new Room("101",1200);
        r1.setHotel(h1);
        h1.getRooms().add(r1);
        em.persist(h1);

        h2 = new Hotel("Grand Royal", "Kings Street 42");
        Room r2 = new Room("201",900);
        r2.setHotel(h2);
        h2.getRooms().add(r2);
        em.persist(h2);

        h3 = new Hotel("Cozy Inn", "Maple Road 5");
        Room r3 = new Room("301",750);
        r3.setHotel(h3);
        h3.getRooms().add(r3);
        em.persist(h3);

        em.getTransaction().commit();
        em.close();

    }
    @AfterAll
    static void tearDownAll(){
        if(emf!= null && emf.isOpen()){
            emf.close();
        }

    }

    @Test
    void createHotel() {
        Hotel h4 = new Hotel("New Test Hotel", "Testvej 1");
        var created = hotelDAO.createHotel(h4);
        assertNotNull(created.getId());
        assertEquals(4, hotelDAO.getAllHotels().size());
    }

    @Test
    void getAllHotels() {
        assertEquals(3, hotelDAO.getAllHotels().size());
    }

    @Test
    void getHotelById() {
    }

    @Test
    void updateHotel() {
    }

    @Test
    void deleteHotel() {
    }

    @Test
    void addRoom() {
    }

    @Test
    void removeRoom() {
    }

    @Test
    void getRoomsForHotel() {
    }
}