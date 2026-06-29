package ism.examen.badwallet_api.shared.exception;

public class EntityExistException extends RuntimeException{
    public EntityExistException(String message){
        super(message);
    }
}
