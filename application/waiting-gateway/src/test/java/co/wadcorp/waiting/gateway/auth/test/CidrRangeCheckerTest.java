package co.wadcorp.waiting.gateway.auth.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.wadcorp.waiting.gateway.auth.CidrRangeChecker;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class CidrRangeCheckerTest {

  @Test
  public void test() {
    Collection<String> cidrs = Set.of("10.1.0.0/16", "192.168.0.0/24", "192.168.1.1/32",
        "1.2.3.4/32");
    CidrRangeChecker checker = new CidrRangeChecker(cidrs);

    assertTrue(checker.isInRange("10.1.2.4"));
    assertFalse(checker.isInRange("10.2.1.1"));
    assertTrue(checker.isInRange("192.168.0.8"));
    assertFalse(checker.isInRange("192.167.0.8"));
    assertFalse(checker.isInRange("192.168.1.2"));
    assertTrue(checker.isInRange("1.2.3.4"));

    assertThrows(IllegalArgumentException.class, () -> {
      InetSocketAddress nullAddress = null;
      checker.isInRange(nullAddress);
    });
    assertFalse(checker.isInRange(""));
    assertFalse(checker.isInRange("  "));
  }

}
