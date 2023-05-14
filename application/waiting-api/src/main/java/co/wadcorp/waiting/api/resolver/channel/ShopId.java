package co.wadcorp.waiting.api.resolver.channel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 이 어노테이션은 Controller parameter 에서 매장ID(shopId)를 가져와야 한다는 것을 나타낸다.
 *
 * @see ChannelShopIdArgumentResolver ShopIdArgumentResolver를 참고.
 */
@Target(value = {ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ShopId {

}
