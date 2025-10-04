package app.security;

import app.config.HibernateConfig;
import app.exceptions.ValidationException;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.eclipse.jetty.server.Authentication;


public class SecurityController {
    ISecurityDAO securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
    ObjectMapper objectMapper = new Utils().getObjectMapper();

    public Handler login(){
        return (Context ctx) -> {
            User user = ctx.bodyAsClass(User.class);
            try{
            User verified = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());
                ObjectNode on = objectMapper
                        .createObjectNode()
                        .put("msg", "Successfull login for user: " +verified.getUsername());
                ctx.json(on).status(200);
            }catch (ValidationException ex){
                ObjectNode on = objectMapper.createObjectNode().put("msg", "login faild, wrong userName og password");
                ctx.json(on).status(401);
            }
        };
    }
}
