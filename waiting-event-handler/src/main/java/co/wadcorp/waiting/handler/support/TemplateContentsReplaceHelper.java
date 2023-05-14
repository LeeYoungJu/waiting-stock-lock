package co.wadcorp.waiting.handler.support;

import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TemplateContentsReplaceHelper {

  public static String replace(String it, Map<String, String> map) {
    StringBuilder sb = new StringBuilder();

    Set<String> keys = map.keySet();
    next: while (it.length() > 0) {
      for (String k : keys) {
        String prefix = "#{" + k + "}";
        if (it.startsWith(prefix)) {
          // we have a match!
          sb.append(map.get(k));
          it = it.substring(prefix.length());
          continue next;
        }
      }
      // no match, advance one character
      sb.append(it.charAt(0));
      it = it.substring(1);
    }
    return sb.toString();
  }
}
