package co.wadcorp.waiting.data.domain.waiting;

public enum WebProgressMessage {

  REGISTER("순서가 다가오고 있어요!"),
  READY("곧 입장이니 매장 앞에서 대기해주세요!"),
  ENTER("드디어 입장! 매장에 방문해주세요.")
  ;

  private final String message;

  WebProgressMessage(String message) {
    this.message = message;
  }

  public static String getMessage(WaitingDetailStatus status) {
    WebProgressMessage webProgressMessage;

    switch (status) {
      case READY_TO_ENTER: webProgressMessage = READY; break;
      case CALL: webProgressMessage = ENTER; break;
      default: webProgressMessage = REGISTER;
    }
    return webProgressMessage.message;
  }
}
