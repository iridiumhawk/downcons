package cherkasov.com.exceptions;

public class IncorrectInputParameters extends Exception {
    public IncorrectInputParameters() {

    }

    public IncorrectInputParameters(String message) {

        super(message);
    }

    public IncorrectInputParameters(String message, Throwable cause) {

        super(message, cause);
    }

    public IncorrectInputParameters(Throwable cause) {

        super(cause);
    }

    public IncorrectInputParameters(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }
}
