package co.wadcorp.waiting.api.test.settings;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.settings.OrderSettingsController;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderSettingsRequest;
import co.wadcorp.waiting.api.service.settings.OrderSettingsApiService;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderSettingsServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderSettingsResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderSettingsResponse.MenuDto;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class OrderSettingsControllerDocsTest extends RestDocsSupport {

  private final OrderSettingsApiService orderSettingsApiService = mock(
      OrderSettingsApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new OrderSettingsController(orderSettingsApiService);
  }

  @Test
  @DisplayName("웨이팅 주문 설정 조회")
  void getOptionSettings() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    OrderSettingsResponse response = defaultResponse();

    // when
    when(orderSettingsApiService.getOrderSettings(any(), any(LocalDate.class),
        any(ZonedDateTime.class))).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/settings/orders",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("order-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("isPossibleOrder").type(JsonFieldType.BOOLEAN)
                    .description("선주문 설정 여부"),
                fieldWithPath("menus").type(JsonFieldType.ARRAY)
                    .description("메뉴 목록"),
                fieldWithPath("menus[].id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("menus[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("shopMenus").type(JsonFieldType.ARRAY)
                    .description("매장 메뉴 목록"),
                fieldWithPath("shopMenus[].id").type(JsonFieldType.STRING)
                    .description("매장 메뉴 short UUID"),
                fieldWithPath("shopMenus[].name").type(JsonFieldType.STRING)
                    .description("매장 메뉴 이름"),
                fieldWithPath("takeOutMenus").type(JsonFieldType.ARRAY)
                    .description("포장 매뉴 목록"),
                fieldWithPath("takeOutMenus[].id").type(JsonFieldType.STRING)
                    .description("포장 매뉴 short UUID"),
                fieldWithPath("takeOutMenus[].name").type(JsonFieldType.STRING)
                    .description("포장 메뉴 이름"),
                fieldWithPath("existsWaitingTeam").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅중인 팀 존재여부 (true-설정수정불가)"),
                fieldWithPath("isOpenedOperation").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅 접수 중 여부 (true-설정수정불가)")
            )
        ));
  }


  @Test
  @DisplayName("웨이팅 주문 설정 저장")
  void saveOptionSettings() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    OrderSettingsResponse response = defaultResponse();
    OrderSettingsRequest request = OrderSettingsRequest.builder()
        .isPossibleOrder(true)
        .build();

    // when
    when(orderSettingsApiService.saveOrderSettings(
        any(), any(OrderSettingsServiceRequest.class), any(LocalDate.class), any(ZonedDateTime.class))
    ).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/settings/orders",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("order-settings-post",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("isPossibleOrder").type(JsonFieldType.BOOLEAN)
                    .description("선주문 설정 여부")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("isPossibleOrder").type(JsonFieldType.BOOLEAN)
                    .description("선주문 설정 여부"),
                fieldWithPath("menus").type(JsonFieldType.ARRAY)
                    .description("메뉴 목록"),
                fieldWithPath("menus[].id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("menus[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("shopMenus").type(JsonFieldType.ARRAY)
                    .description("매장 메뉴 목록"),
                fieldWithPath("shopMenus[].id").type(JsonFieldType.STRING)
                    .description("매장 메뉴 short UUID"),
                fieldWithPath("shopMenus[].name").type(JsonFieldType.STRING)
                    .description("매장 메뉴 이름"),
                fieldWithPath("takeOutMenus").type(JsonFieldType.ARRAY)
                    .description("포장 매뉴 목록"),
                fieldWithPath("takeOutMenus[].id").type(JsonFieldType.STRING)
                    .description("포장 매뉴 short UUID"),
                fieldWithPath("takeOutMenus[].name").type(JsonFieldType.STRING)
                    .description("포장 메뉴 이름"),
                fieldWithPath("existsWaitingTeam").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅중인 팀 존재여부 (true-설정수정불가)"),
                fieldWithPath("isOpenedOperation").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅 접수 중 여부 (true-설정수정불가)")
            )
        ));
  }

  private OrderSettingsResponse defaultResponse() {

    return OrderSettingsResponse.builder()
        .isPossibleOrder(true)
        .menus(
            List.of(
                MenuDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("매장1")
                    .build(),
                MenuDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("매장2")
                    .build()
            )
        )
        .shopMenus(
            List.of(
                MenuDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("매장1")
                    .build(),
                MenuDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("매장2")
                    .build()
            )
        )
        .takeOutMenus(
            List.of(
                MenuDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("포장1")
                    .build(),
                MenuDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("포장2")
                    .build(),
                MenuDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("포장3")
                    .build()
            )
        )
        .isOpenedOperation(true)
        .existsWaitingTeam(true)
        .build();
  }

}