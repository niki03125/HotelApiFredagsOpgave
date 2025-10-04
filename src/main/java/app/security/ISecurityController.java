package app.security;

import io.javalin.http.Handler;

public interface ISecurityController {
    Handler login(); // to get token
    Handler register();// to get user
    Handler authenticate(); // to verify roles inside token
    Handler authorize();
}
