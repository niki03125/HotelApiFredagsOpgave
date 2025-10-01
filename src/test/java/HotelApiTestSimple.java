import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
public class HotelApiTestSimple {

    @BeforeAll
    static void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7011;
        RestAssured.basePath = "/api/v1";
    }

    @Test
    public void testGetAllHotels(){
        when()
                .get("/hotel")
        .then()
                .statusCode(200)
                .body("$", not(empty()));//$ betyder Arrayet,
    }

    @Test
    public void testGetHotelByID(){
        when()
                .get("/hotel/1")
        .then()
                .statusCode(200)
                .body("id", is(1));
    }

    @Test
    public void testGetRoomsForHotel(){
        when()
                .get("/hotel/1/rooms")
        .then()
                .statusCode(200)
                .body("$", not(empty()));//$ betyder Arrayet,
    }

    @Test
    public void testCreateHotel(){
        String json = """
                {"name": "Test Hotel", "address": "Test Street"}
                """;
        given()
                .contentType("application/json")
                .body(json)
        .when()
                .post("/hotel")
        .then()
                .statusCode(201)
                .body("name", is("Test Hotel"));
    }

    @Test
    public void testUpdateHotel(){
        String json = """
                {"name": "Updated Hotel", "address": "Updated Street"}
                """;
        given()
                .contentType("application/json")
                .body(json)
        .when()
                .put("/hotel/1")
        .then()
                .statusCode(200)
                .body("name", is("Updated Hotel"));
    }

    @Test
    public void testDeleteHotel(){
        when()
                .delete("/hotel/2")
        .then()
                .statusCode(anyOf(is(200), is(204)));
    }

}
