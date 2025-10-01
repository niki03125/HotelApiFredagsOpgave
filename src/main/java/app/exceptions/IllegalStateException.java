package app.exceptions;

public class IllegalStateException extends ApiException{
    public IllegalStateException(String msg){
        super(400, msg);
    }
}
