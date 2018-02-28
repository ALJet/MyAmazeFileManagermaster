package indi.aljet.myamazefilemanager_master.exceptions;

/**
 * Created by PC-LJL on 2018/1/9.
 */

public class StreamNotFoundException extends Exception {
    private static final String MESSASGE = "Can't get stream";

    public StreamNotFoundException() {
        super(MESSASGE);
    }

    public StreamNotFoundException(String message){
        super(message);
    }

    public StreamNotFoundException(String message,
                                   Throwable cause){
        super(message,cause);
    }

    public StreamNotFoundException(Throwable cause){
        super(MESSASGE,cause);
    }


}
