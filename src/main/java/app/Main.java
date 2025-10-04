package app;


import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.daos.HotelDAO;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.exceptions.ValidationException;
import app.security.ISecurityDAO;
import app.security.Role;
import app.security.SecurityDAO;
import app.security.User;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public class Main {
    public static void main(String[] args)
        {EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        ISecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
        HotelDAO hotelDAO = HotelDAO.getInstance(emf);

        // Lav 3 Hotels med 1 room hver
        List<HotelDTO> hotelList = List.of(
                HotelDTO.builder()
                        .name("Hotel California")
                        .address("Sunset Boulevard 1")
                        .rooms(List.of(RoomDTO.builder()
                                .number("101")
                                .price(1200)
                                .build()))
                        .build(),
                HotelDTO.builder()
                        .name("Grand Royal")
                        .address("King's Street 42")
                        .rooms(List.of(RoomDTO.builder()
                                .number("201")
                                .price(900)
                                .build()))
                        .build(),
                HotelDTO.builder()
                        .name("Cozy Inn")
                        .address("Maple Road 5")
                        .rooms(List.of(RoomDTO.builder()
                                .number("301")
                                .price(750)
                                .build()))
                        .build()
        );

        // bruges til at lave og gemme DTO-> Entiteter i DB
        for (HotelDTO dto : hotelList) {
            Hotel hotelEntity = dto.toEntity();
            hotelDAO.createHotel(hotelEntity);
        }

        // Start server
        final Javalin app = ApplicationConfig.startServer(7011);


         User user = dao.createUser("user1", "pass123");
         System.out.println(user.getUsername() +" + " + user.getPassword());

        Role role = dao.createRole("User");
        try {
            User updatedUser = dao.addUserRole("user1", "User");
            System.out.println(updatedUser);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        try {
            User validatedUser = dao.getVerifiedUser("user1", "pass123");
            System.out.println("User was validated " + validatedUser.getUsername());
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
        // shutdown hook – så porten frigives hvis du lukker programmet
        Runtime.getRuntime().addShutdownHook(new Thread(() -> ApplicationConfig.stopServer(app)));
    }

}