package secondlab.candy.error;

public class CandyException extends RuntimeException {
    public CandyException(String message) {
        super(message);
    }

    public CandyException(String message, Throwable cause) {
        super(message, cause);
    }
}
