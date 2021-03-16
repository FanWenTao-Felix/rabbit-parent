package ink.ckx.rabbit.api.exception;

/**
 * @auth chenkaixin
 * @date 2021/03/16
 */
public class MessageException extends Exception {

    private static final long serialVersionUID = -5476311072534046234L;

    public MessageException() {
        super();
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }

}
