package app.security;

import app.config.HibernateConfig;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.bugelhartmann.TokenSecurity;
import dk.bugelhartmann.TokenVerificationException;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.*;
import org.eclipse.jetty.server.Authentication;

import javax.print.DocFlavor;
import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;


public class SecurityController  implements ISecurityController{
    ISecurityDAO securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
    ObjectMapper objectMapper = new Utils().getObjectMapper();
    TokenSecurity tokenSecurity = new TokenSecurity();

    public Handler login(){
        return (Context ctx) -> {
            //læser userName og password fra body
            User user = ctx.bodyAsClass(User.class);
            try{
                // tjekker bruger i db
                User verified = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());

                // lav liste af roller som tekst
                Set<String> stringRoles = verified.getRoles()
                        .stream()
                        .map(role -> role.getRoleName())
                        .collect(Collectors.toSet());

                // pakker uderName og roller i en DTO
                UserDTO userDTO = new UserDTO(verified.getUsername(), stringRoles);

                //laver token
                String token = createToken(userDTO);

                //send Json med token og brugernavn
                ObjectNode on = objectMapper
                        .createObjectNode()
                        .put("token", token)
                        .put("username", userDTO.getUsername());
                ctx.json(on).status(200);
            }catch (ValidationException ex){
                ObjectNode on = objectMapper.createObjectNode()
                        .put("msg", "login faild, wrong userName og password");
                ctx.json(on).status(401);
            }
        };
    }


    @Override
    public Handler register() {
        return null;
    }

    @Override
    public Handler authenticate() {
        return (Context ctx) -> {

            //Preflight request (cors) skal ikke authenticates
            if (ctx.method().toString().equals("OPTIONS")){
                ctx.status(200);
                return;
            }

            //henter de roller som routen kræver
            Set<String> allowedRoles = ctx.routeRoles()
                    .stream().map(role -> role.toString().toUpperCase()).collect(Collectors.toSet());

            // hvis ruten er "åben" ( ingen roller/ANYONE) spring auth over
            if (isOpenEndpoint(allowedRoles)){
                return;
            }

            // Ellers kræves token: læs header, verificer, udpak bruger
            UserDTO verifiedTokenUser = validateAndGetUserFromToken(ctx);

            //Gør brugeren tilgængelig for downstream( fx i authorize() eller selve handeleresn)
            ctx.attribute("user", verifiedTokenUser);
        };
    }

    // Hjælpe methode: finder ud af om et endpoint er åbent ( ingen roller eller ANYONE)
    private boolean isOpenEndpoint(Set<String> allowedRoles) {
        // Ingen roller sat -> åben
        if(allowedRoles.isEmpty())
            return true;

        //ANYONE.rollen betyder eksplicit åben for alle
        if(allowedRoles.contains("ANYONE")){
            return true;
        }
        return false;
    }

    @Override
    public Handler authorize() {
        return (Context ctx) -> {
            // Hent roller for ruten
            Set<String> allowedRoles = ctx.routeRoles()
                    .stream()
                    .map(role -> role.toString().toUpperCase())
                    .collect(Collectors.toSet());

            //skip hvis åben rute
            if(isOpenEndpoint(allowedRoles)){
                return;
            }

            // Hent brugeren, som authenticate() lagde på ctx
            UserDTO user = ctx.attribute("user");

            if(user == null){
                // hvis vi ender her, er authenticate() sandsynligvis  ikke før denne
                throw new ForbiddenResponse("No user was added from this token");
            }

            // tjek om brugerens rolle/roller matcher med mindst en af de krævende
            if(!userHasAllowedRole(user,allowedRoles)){
                throw new ForbiddenResponse("User was not authorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
            }
        };
    }

    //retunere true, hvis brugeren har mindst en rolle som endpointet kræver
    public static boolean userHasAllowedRole(UserDTO user, Set<String> allowedRoles){
        return user.getRoles().stream()
                .anyMatch(role -> allowedRoles.contains(role.toUpperCase()));
    }

    private String createToken(UserDTO user) {

        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            // Skeln mellem container/deploy og lokal kørsel
            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }

            // Brug TokenSecurity-lib til at generere token (inkl. roller)
            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);

        } catch (Exception e) {
            // Generisk 500 hvis noget går galt under token-oprettelse
            throw new ApiException(500, "Could not create token");
        }
    }


    //læser Authorization-header og retunere rå JWT
    private static String getToken(Context ctx){
        String header = ctx.header("Authorization");
        if (header == null){
            throw new UnauthorizedResponse("Authorization header is malformed");
        }
        // Forventet format: "Bearer <token>"
        String token = header.split(" ")[1];
        if (header == null){
            throw  new UnauthorizedResponse("Authorization header is malformed");
        }
        return token;
    }

    private UserDTO verifyToken(String token){
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);

        // SECRET afhænger af miljø (ENV i prod, properties lokalt)
        String SECRET = IS_DEPLOYED ? System.getenv("SECRET_KEY") : Utils.getPropertyValue("SECRET_KEY", "config.properties");

        try {
            // Tjek signatur + udløb
            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                // Udpak brugernavn + roller som UserDTO
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                // Ugyldigt/udløbet token
                throw new UnauthorizedResponse("Token not valid");
            }
        } catch (ParseException | TokenVerificationException e) {
            // Kunne ikke parse/verify token → 401
            throw new ApiException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }

    private UserDTO validateAndGetUserFromToken(Context ctx) {
        String token = getToken(ctx);
        UserDTO verifiedTokenUser = verifyToken(token);
        if(verifiedTokenUser == null){
            throw new UnauthorizedResponse("Invalid user or token");
        }
        return verifiedTokenUser;
    }
}