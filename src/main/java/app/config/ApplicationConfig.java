package app.config;

import app.exceptions.IllegalStateException;
import app.exceptions.ServerErrorException;
import app.routes.Routes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;

import java.util.Map;

public class ApplicationConfig {
    private static Routes routes = new Routes();

    public static void configuration(JavalinConfig config){
        config.showJavalinBanner = false;
        config.bundledPlugins.enableRouteOverview("/routes");
        config.router.contextPath = "/api/v1"; // base path for all endpoints
        config.router.apiBuilder(routes.getRoutes());
    }

    public static Javalin startServer(int port) {
        routes = new Routes();
        var app = Javalin.create(ApplicationConfig::configuration);

        // Exceptions
        app.exception(IllegalStateException.class, (e, ctx) -> {
            ctx.status(400).json(Map.of(
                    "error", "Invalid request body (bad JSON)",
                    "details", e.getMessage()
            ));
        });

        app.exception(ServerErrorException.class, (e, ctx) -> {
            ctx.status(500).json(Map.of(
                    "error", "Internal Server error",
                    "details", e.getMessage()
            ));
        });

        app.start(port);
        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
