package app.exceptions;

public class ServerErrorException extends ApiException{
    public ServerErrorException(String msg){
        super(500,msg);
    }

}
