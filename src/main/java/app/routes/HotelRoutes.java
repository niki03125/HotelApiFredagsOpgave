package app.routes;

import app.controllers.HotelController;
import app.controllers.RoomController;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.router.Endpoint;
import static io.javalin.apibuilder.ApiBuilder.*;

public class HotelRoutes {
    private final HotelController hotelController = new HotelController();
    private final RoomController roomController = new RoomController();

    public EndpointGroup getRoutes(){
        return () -> {
            get("/", hotelController::getAllHotels);
            get("/{id}", hotelController::getHotelById);
            get("/{id}/rooms", roomController::getRoomsForHotel);
            post("/",         hotelController::createHotel);
            put("/{id}",      hotelController::updateHotel);
            delete("/{id}",   hotelController::deleteHotel);

        };
    }
}
