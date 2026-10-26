package secondlab.candy.error;

public class XmlValidationException extends CandyException {
    public XmlValidationException(String message) {
        super(message);
    }

    public XmlValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
