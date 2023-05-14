package co.wadcorp.waiting.api.resolver.channel;

import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

/**
 * PATH에 들어있는 {shopId}를 웨이팅의 shopId로 파싱하거나, 각 채널의 channelShopId로 변환한다.
 */
@Slf4j
@Component
public class ChannelShopIdArgumentResolver implements HandlerMethodArgumentResolver {

  @Resource
  private List<ChannelShopIdConverter> shopIdConverters;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(ShopId.class)
        && ChannelShopIdMapping.class.isAssignableFrom(parameter.getParameterType());
  }

  /**
   * 각 채널의 channelShopID를 ChannelShopIdMapping으로 변환한다.
   *
   * <pre>
   * 조건.
   * 1. Controller의 Path Param 으로 shopId가 있어야 한다. 예) /api/v1/shops/{shopId}/blah
   * 2. 해당 파라미터에 @ShopId 어노테이션이 붙어야 있어야 한다.
   * </pre>
   */

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    // URL에 {shopId} 형식의 PathParameter가 있어야 한다.
    Object attribute = webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
        RequestAttributes.SCOPE_REQUEST);
    if (!(attribute instanceof Map)) {
      return null;
    }
    @SuppressWarnings("unchecked")
    Map<String, String> map = (Map<String, String>) attribute;
    if (map.isEmpty()) {
      return null;
    }

    // key: PathParameterName. value: PathParameterValue
    final String key = "shopIds";
    final String channelShopId = map.getOrDefault(key, "");

    // GATEWAY를 통해 들어온 요청은 HTTP HEADER에 X-CHANNEL-ID 가 세팅되어 있다.
    final String channelId = webRequest.getHeader("X-CHANNEL-ID");
    final ServiceChannelId serviceChannelId = ServiceChannelId.find(channelId);

    // 채널 shopId를 ChannelShopIdMapping으로 변환한다.
    ChannelShopIdMapping resolvedShopId = null;
    var optShopIdConverter = shopIdConverters.stream()
        .filter(channelShopIdConverter -> channelShopIdConverter.isSupport(serviceChannelId))
        .findFirst();
    if (optShopIdConverter.isPresent()) {
      resolvedShopId = optShopIdConverter.get().getShopIds(List.of(channelShopId.split(",")));
    }
    if (resolvedShopId == null) {
      throw new AppException(HttpStatus.CONFLICT, "매장을 특정할 수 없습니다.");
    }
    return resolvedShopId;
  }
}
