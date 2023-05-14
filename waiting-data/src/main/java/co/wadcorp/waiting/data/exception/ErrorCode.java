package co.wadcorp.waiting.data.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

  /**
   * 웨이팅 공통
   */
  DO_NOT_MATCH_REGISTER_CUSTOMER_SEQ("DO_NOT_MATCH_REGISTER_CUSTOMER_SEQ", "웨이팅을 등록한 유저와 일치하지 않습니다."),
  NOT_FOUND_WAITING("NOT_FOUND_WAITING", "웨이팅을 찾을 수 없습니다."),
  NOT_FOUND_WAITING_HISTORY("NOT_FOUND_WAITING", "웨이팅 이력을 찾을 수 없습니다."),
  EXPIRED_WAITING("EXPIRED_WAITING", "유효기간이 만료된 웨이팅 내역입니다. 새로운 웨이팅으로 만나요!"),

  /**
   * 웨이팅 운영 정보
   */
  OUT_OF_RANGE_PAUSE_PERIOD("OUT_OF_RANGE_PAUSE_PERIOD", "일시정지를 설정할 수 있는 시간의 범위를 벗어났습니다."),
  NOT_OPEN_WAITING_OPERATION("NOT_OPEN_WAITING_OPERATION", "현재 웨이팅 접수 중이 아니라 등록할 수 없습니다."),
  NOT_FOUND_OPERATION("NOT_FOUND_OPERATION", "대기 운영 정보를 찾지 못했습니다."),
  ALREADY_CREATED_OPERATION("ALREADY_CREATED_OPERATION", "이미 등록되어 있습니다."),

  /**
   * 웨이팅 등록
   */
  ALREADY_REGISTERED_WAITING("ALREADY_REGISTERED_WAITING", "이미 웨이팅 등록된 매장입니다."),
  NO_MORE_THAN_THREE_TIMES("NO_MORE_THAN_THREE_TIMES", "웨이팅은 동시에 3회까지만 등록할 수 있습니다."),
  EXCEED_AVAILABLE_SEATS("EXCEED_AVAILABLE_SEATS", "입장 가능 인원을 초과하였습니다."),
  UNDER_AVAILABLE_SEATS("UNDER_AVAILABLE_SEATS", "최소 입장 가능 인원보다 작게 입력했습니다."),
  EXCEEDING_ORDER_QUANTITY_PER_TEAM("EXCEEDING_ORDER_QUANTITY_PER_TEAM", "팀당 주문 가능 수량을 초과했어요."),

  /**
   * 웨이팅 미루기
   */
  COULD_NOT_PUT_OFF("COULD_NOT_PUT_OFF", "현재 마지막 순서이기 때문에 미루기가 불가해요"),
  ALL_USED_PUT_OFF_COUNT("ALL_USED_PUT_OFF_COUNT", "미루기 횟수를 이미 모두 사용했습니다."),
  CANNOT_NOT_PUT_OFF_AFTER_CALL("CANNOT_NOT_PUT_OFF_AFTER_CALL", "입장 호출 후에는 미루기가 불가해요."),
  DISABLE_PUT_OFF("DISABLE_PUT_OFF", "미루기가 불가한 매장이에요."),

  /**
   * 웨이팅 착석
   */
  SITTING_WAITING("SITTING_WAITING", "착석 완료된 웨이팅입니다."),
  ALREADY_SITTING_WAITING("ALREADY_SITTING_WAITING", "이미 착석 처리된 웨이팅입니다. 다시 확인해주세요."),
  NOT_WAITING_STATUS_BY_SITTING("NOT_WAITING_STATUS_BY_SITTING", "웨이팅 중이 아니라 착석할 수 없습니다. 다시 확인해주세요."),

  /**
   * 웨이팅 취소
   */
  CANCELED_WAITING_BY_SHOP("CANCELED_WAITING_BY_SHOP", "매장의 사정으로 웨이팅이 취소되었습니다."),
  CANCELED_WAITING_BY_CUSTOMER("CANCELED_WAITING_BY_CUSTOMER", "고객님의 요청으로 웨이팅이 취소되었습니다."),
  CANCELED_WAITING_BY_NO_SHOW("CANCELED_WAITING_BY_NO_SHOW", "매장 미방문으로 웨이팅이 취소되었습니다."),
  NOT_WAITING_STATUS("NOT_WAITING_STATUS", "웨이팅 중이 아니라 취소할 수 없습니다. 다시 확인해주세요."),
  ALREADY_CANCELED_WAITING("ALREADY_CANCELED_WAITING", "이미 취소된 웨이팅 입니다. 다시 확인해주세요."),

  /**
   * 웨이팅 되돌리기
   */
  CANNOT_UNDO_CAUSE_MORE_THAN_THREE_WAITINGS("CANNOT_UNDO_CAUSE_MORE_THAN_THREE_WAITINGS", "해당 고객님이 등록할 수 있는 웨이팅 횟수를 초과해 되돌리기가 불가합니다. (최대 3개 매장까지 등록 가능)"),
  SAME_SHOP_WAITING_REGISTERED("SAME_SHOP_WAITING_REGISTERED", "동일 번호로 등록된 웨이팅이 있어 되돌리기가 불가합니다."),
  ALREADY_UNDO_WAITING("ALREADY_UNDO_WAITING", "이미 되돌리기한 웨이팅 입니다. 다시 확인해주세요."),
  TIME_OVER_UNDO_AVAILABLE_TIME("TIME_OVER_UNDO_AVAILABLE_TIME", "되돌리기 할 수 있는 시간이 지났습니다. 다시 웨이팅 등록을 해주세요."),
  CANNOT_UNDO_CAUSE_SEAT_OPTIONS_MODIFIED("CANNOT_UNDO_CAUSE_SEAT_OPTIONS_MODIFIED", "웨이팅 설정이 변경되어 불가해요. 다시 등록해주세요."),
  OUT_OF_STOCK("OUT_OF_STOCK", "재고 소진으로 주문 불가해요."),
  NOT_FOUND_ORDER_LINE_ITEM_MENU("NOT_FOUND_ORDER_LINE_ITEM_MENU", "메뉴가 삭제되어 주문 불가해요."),

  /**
   * 웨이팅 지연
   */
  NOT_WAITING_STATUS_BY_DELAY("NOT_WAITING_STATUS_BY_DELAY", "웨이팅 중이 아니라 지연으로 변경할 수 없습니다."),

  /**
   * 웨이팅 설정
   */
  COULD_NOT_UPDATE_SETTINGS("COULD_NOT_UPDATE_SETTINGS", "등록된 웨이팅이 존재합니다. 설정을 수정할 수 없습니다."),

  /**
   * 웨이팅 고객
   */
  NOT_FOUND_CUSTOMER("NOT_FOUND_CUSTOMER", "고객을 찾을 수 없습니다."),

  /**
   * 매장
   */
  NOT_MATCH_SHOP_ID("NOT_MATCH_SHOP_ID", "잘못된 요청입니다. 다시 시도해주세요."),
  INVALID_SHOP_PHONE("INVALID_SHOP_PHONE", "정확한 전화번호를 입력해 주세요."),
  NOT_FOUND_SHOP("NOT_FOUND_SHOP", "매장 정보를 찾지 못했습니다."),
  ONLY_ONE_SHOP_ID("ONLY_ONE_SHOP_ID", "매장 ID는 1개만 입력할 수 있습니다."),

  /**
   * 공지사항
   */
  NOT_FOUND_NOTICE("NOT_FOUND_NOTICE", "공지사항을 찾을 수 없습니다."),

  /**
   * 주문/카테고리/메뉴
   */
  NOT_FOUND_MENU("NOT_FOUND_MENU", "메뉴 정보를 찾지 못했습니다."),
  NOT_POSSIBLE_ORDER("NOT_POSSIBLE_ORDER", "선주문을 사용할 수 없는 매장입니다."),
  NOT_FOUND_MENU_REMAINING_STOCK("NOT_FOUND_MENU_REMAINING_STOCK", "메뉴 잔여 재고 정보를 찾지 못했습니다."),
  INVALID_LINE_ITEM_PRICE("INVALID_LINE_ITEM_PRICE", "가격 정보가 잘못 된 메뉴가 존재합니다."),
  INVALID_TOTAL_PRICE("INVALID_TOTAL_PRICE", "선주문 최종 가격이 잘못되었습니다."),
  NOT_FOUND_STOCK("NOT_FOUND_STOCK", "재고 정보를 찾지 못했습니다."),

  /**
   * 매장별 메모 키워드 세팅
   */
  NOT_FOUND_MEMO_KEYWORD("NOT_FOUND_MEMO_KEYWORD", "메모 키워드 정보를 찾지 못했습니다."),

  /**
   * 웨이팅 메모 설정
   */
  NOT_FOUND_WAITING_MEMO("NOT_FOUND_WAITING_MEMO", "웨이팅 메모 정보가 존재하지 않습니다."),

  /**
   * POS
   */
  POS_API_ERROR_USER_INFO("POS_API_ERROR_USER_INFO", "유저 정보를 조회하는데 문제가 발생했어요. 다시 시도해주세요."),
  ;

  private final String code;
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
