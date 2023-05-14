package co.wadcorp.waiting.shared.util;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListUtils {

  public static <T> List<List<T>> partition(List<T> items, int size) {
    return Lists.partition(items, size);
  }

}
