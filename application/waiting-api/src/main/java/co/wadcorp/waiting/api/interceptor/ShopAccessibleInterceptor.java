package co.wadcorp.waiting.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 현재 로그인 된 이용자가 API 호출 대상 매장에 대해 액세스 권한이 있는지를 확인하는 인터셉터.
 */
public class ShopAccessibleInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    // TODO: 다음 번 구현 내용
    //  1. 현재 로그인 된 이용자가 액세스 할 수 있는 매장 리스트를 가져와서,
    //  2. 현재 API에 지정된 매장(shopSeq or shopId)이 해당 리스트에 포함되어 있는지 확인한다.

    try {
      // 캐치테이블 포스 인증을 거쳤을 경우, X-GW-POS-USER-SEQ 헤더가 설정된다.
      int intHeader = request.getIntHeader("X-GW-POS-USER-SEQ");
    } catch (NumberFormatException e) {
      // TODO: 로그인 된 이용자가 없는 경우
    }

    return HandlerInterceptor.super.preHandle(request, response, handler);
  }
}
