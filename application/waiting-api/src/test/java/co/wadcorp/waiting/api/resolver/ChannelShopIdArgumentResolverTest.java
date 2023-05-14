package co.wadcorp.waiting.api.resolver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdArgumentResolver;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.resolver.channel.ShopId;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;

/**
 * ChannelShopIdArgumentResolver 자체를 테스트한다.
 */
public class ChannelShopIdArgumentResolverTest {

  /*
   * @ShopId 어노테이션이 붙은 파라미터가 있는 메서드 탐지 테스트.
   */
  @Test
  public void testSupportParameter() throws NoSuchMethodException {
    ChannelShopIdArgumentResolver resolver = new ChannelShopIdArgumentResolver();

    Method method1 = getClass().getMethod("method1", ChannelShopIdMapping.class);
    Method method2 = getClass().getMethod("method2", ChannelShopIdMapping.class);

    MethodParameter shopIdParamFor1 = new MethodParameter(method1, 0);
    MethodParameter shopIdParamFor2 = new MethodParameter(method2, 0);

    assertTrue(resolver.supportsParameter(shopIdParamFor1));
    assertFalse(resolver.supportsParameter(shopIdParamFor2));
  }


  public int method1(@ShopId ChannelShopIdMapping shopId) {
    return 1;
  }

  public int method2(ChannelShopIdMapping shopId) {
    return 2;
  }

}
