//package load;


public class RnEx extends RuntimeException {
    private static final long serialVersionUID = 5224696788505678598L;
    public RnEx() {
      super();
    }
    public RnEx(String message) {
      super(message);
    }
    public RnEx(String message, Throwable cause) {
      super(message, cause);
    }
    public RnEx(Throwable cause) {
      super(cause);
    }
  }
