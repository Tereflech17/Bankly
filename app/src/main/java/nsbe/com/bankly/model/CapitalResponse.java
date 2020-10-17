package nsbe.com.bankly.model;

/**
 * Created by Charlton on 11/10/17.
 */

public class CapitalResponse<T> {
    int code;
    String message;
    T objectCreated;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getObjectCreated() {
        return objectCreated;
    }
}
