package app.routes;

import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {
    private final HotelRoutes hotelRoutes = new HotelRoutes();

    public EndpointGroup getRoutes(){
        return () -> {
            get("/", ctx -> ctx.result("API is running")); //remember this one to make it work
            path("/hotel", hotelRoutes.getRoutes());
        };
    }
}
