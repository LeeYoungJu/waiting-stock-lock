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
import co.wadcorp.waiting.api.controller.settings.OrderCategorySettingsController;
import co.wadcorp.waiting.api.service.settings.OrderCategorySettingsApiService;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsListServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsListServiceRequest.OrderCategoryServiceDto;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategorySettingsListResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategorySettingsResponse;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class OrderCategorySettingsControllerDocsTest extends RestDocsSupport {

  private final OrderCategorySettingsApiService orderSettingsApiService = mock(
      OrderCategorySettingsApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new OrderCategorySettingsController(orderSettingsApiService);
  }

  @Test
  @DisplayName("카테고리 리스트 조회")
  void getCategory() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    OrderCategorySettingsListResponse response = defaultSaveAllResponse();

    // when
    when(orderSettingsApiService.getCategories(any())).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/settings/orders/categories",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("order-category-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("categories").type(JsonFieldType.ARRAY)
                    .description("카테고리 목록"),
                fieldWithPath("categories[].id").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("categories[].name").type(JsonFieldType.STRING)
                    .description("카테고리 이름"),
                fieldWithPath("categories[].ordering").type(JsonFieldType.NUMBER)
                    .description("카테고리 노출 순서")
            )
        ));
  }


  @Test
  @DisplayName("카테고리 단건 저장")
  void saveCategory() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    String id = UUIDUtil.shortUUID();

    OrderCategorySettingsResponse response = OrderCategorySettingsResponse.builder()
        .id(id)
        .name("카테고리1")
        .ordering(1)
        .build();

    OrderCategorySettingsServiceRequest request = OrderCategorySettingsServiceRequest.builder()
        .id(id)
        .name("카테고리1")
        .build();

    // when
    when(
        orderSettingsApiService.saveCategory(any(), any(OrderCategorySettingsServiceRequest.class)))
        .thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/settings/orders/categories",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("order-category-settings-post",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("name").type(JsonFieldType.STRING)
                    .description("카테고리 이름")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("name").type(JsonFieldType.STRING)
                    .description("카테고리 이름"),
                fieldWithPath("ordering").type(JsonFieldType.NUMBER)
                    .description("카테고리 노출 순서")
            )
        ));
  }

  @Test
  @DisplayName("카테고리 리스트 저장")
  void saveAllCategories() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    OrderCategorySettingsListResponse response = defaultSaveAllResponse();
    OrderCategorySettingsListServiceRequest request = defaultSaveAllRequest();

    // when
    when(orderSettingsApiService.saveAllCategories(any(),
        any(OrderCategorySettingsListServiceRequest.class)))
        .thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post(
                    "/api/v1/shops/{shopId}/settings/orders/categories/list",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("order-category-settings-post-all",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("categories").type(JsonFieldType.ARRAY)
                    .description("카테고리 목록"),
                fieldWithPath("categories[].id").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("categories[].name").type(JsonFieldType.STRING)
                    .description("카테고리 이름"),
                fieldWithPath("categories[].ordering").type(JsonFieldType.NUMBER)
                    .description("카테고리 노출 순서")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("categories").type(JsonFieldType.ARRAY)
                    .description("카테고리 목록"),
                fieldWithPath("categories[].id").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("categories[].name").type(JsonFieldType.STRING)
                    .description("카테고리 이름"),
                fieldWithPath("categories[].ordering").type(JsonFieldType.NUMBER)
                    .description("카테고리 노출 순서")
            )
        ));
  }

  private static OrderCategorySettingsListServiceRequest defaultSaveAllRequest() {
    return OrderCategorySettingsListServiceRequest.builder()
        .categories(List.of(
            OrderCategoryServiceDto
                .builder()
                .id(UUIDUtil.shortUUID())
                .name("카테고리1")
                .ordering(1)
                .build(),
            OrderCategoryServiceDto
                .builder()
                .id(UUIDUtil.shortUUID())
                .name("카테고리2")
                .ordering(2)
                .build(),
            OrderCategoryServiceDto
                .builder()
                .id(UUIDUtil.shortUUID())
                .name("카테고리3")
                .ordering(3)
                .build()
        ))
        .build();
  }

  private OrderCategorySettingsListResponse defaultSaveAllResponse() {
    return OrderCategorySettingsListResponse.builder()
        .categories(
            List.of(
                OrderCategorySettingsListResponse.OrderCategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("카테고리1")
                    .ordering(1)
                    .build(),
                OrderCategorySettingsListResponse.OrderCategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("카테고리2")
                    .ordering(2)
                    .build(),
                OrderCategorySettingsListResponse.OrderCategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("카테고리3")
                    .ordering(3)
                    .build()
            )
        )
        .build();
  }

}