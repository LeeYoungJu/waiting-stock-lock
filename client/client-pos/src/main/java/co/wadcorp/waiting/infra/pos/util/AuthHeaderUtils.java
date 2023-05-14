package co.wadcorp.waiting.infra.pos.util;

import java.util.HashMap;
import java.util.Map;

public class AuthHeaderUtils {


  private static final String X_CTM_AUTH = "X-CTM-AUTH";

  public static Map<String, Object> createAuthHeader(String ctmAuth) {
    Map<String, Object> header = new HashMap<>();
    header.put(X_CTM_AUTH, ctmAuth);
    return header;
  }

}
