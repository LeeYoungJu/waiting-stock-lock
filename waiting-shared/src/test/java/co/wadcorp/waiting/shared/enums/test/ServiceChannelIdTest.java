package co.wadcorp.waiting.shared.enums.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import org.junit.jupiter.api.Test;

public class ServiceChannelIdTest {

  @Test
  public void test() {
    assertEquals("CATCH-WAITING", ServiceChannelId.CATCH_WAITING.getValue());
    assertEquals("CATCHTABLE-B2C", ServiceChannelId.CATCHTABLE_B2C.getValue());

    assertEquals("CATCH_WAITING", ServiceChannelId.CATCH_WAITING.toString());
    assertEquals("CATCHTABLE_B2C", ServiceChannelId.CATCHTABLE_B2C.toString());
  }

}
