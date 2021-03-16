package ink.ckx.rabbit.api.exception;

/**
 * @auth chenkaixin
 * @date 2021/03/16
 */
public class MessageRunTimeException extends RuntimeException {

    private static final long serialVersionUID = 6075481971885443126L;

    public MessageRunTimeException() {
        super();
    }

    public MessageRunTimeException(String message) {
        super(message);
    }

    public MessageRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageRunTimeException(Throwable cause) {
        super(cause);
    }
}
