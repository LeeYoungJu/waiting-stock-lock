package co.wadcorp.waiting.api.test.waiting.management;

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
import co.wadcorp.waiting.api.controller.waiting.management.ManagementStockController;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.ManagementStockUpdateRequest;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.ManagementStockUpdateRequest.ManagementStockCategoryDto;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.ManagementStockUpdateRequest.ManagementStockMenuDto;
import co.wadcorp.waiting.api.service.waiting.management.ManagementStockApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateStockServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementStockListResponse;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class ManagementStockControllerDocsTest extends RestDocsSupport {

  private final ManagementStockApiService managementStockApiService = mock(
      ManagementStockApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new ManagementStockController(managementStockApiService);
  }

  @Test
  @DisplayName("대시보드 - 재고 관리 모달 조회")
  void getStock() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";

    // when
    when(managementStockApiService.getStocks(any(), any(LocalDate.class)))
        .thenReturn(defaultResponse());

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/management/stock",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("management-stock-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장 아이디 shortUUID")
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
                    .description("카테고리 노출 순서"),
                fieldWithPath("categories[].isStockUnderThreshold").type(JsonFieldType.BOOLEAN)
                    .description("부족한 재고 여부 (카테고리)"),
                fieldWithPath("categories[].menus").type(JsonFieldType.ARRAY)
                    .description("메뉴 목록"),
                fieldWithPath("categories[].menus[].id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("categories[].menus[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("categories[].menus[].ordering").type(JsonFieldType.NUMBER)
                    .description("메뉴 노출 순서"),
                fieldWithPath("categories[].menus[].isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("categories[].menus[].remainingQuantity").type(JsonFieldType.NUMBER)
                    .description("메뉴 남은 재고").optional(),
                fieldWithPath("categories[].menus[].isStockUnderThreshold")
                    .type(JsonFieldType.BOOLEAN)
                    .description("부족한 재고 여부"),
                fieldWithPath("categories[].menus[].isOutOfStock").type(JsonFieldType.BOOLEAN)
                    .description("품절 여부")
            )
        ));
  }

  @Test
  @DisplayName("대시보드 - 재고 관리 모달 저장")
  void updateStock() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";

    ManagementStockUpdateRequest request = ManagementStockUpdateRequest.builder()
        .categories(
            List.of(
                ManagementStockCategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("음식")
                    .menus(
                        List.of(
                            ManagementStockMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("돈가스")
                                .additionalQuantity(10)
                                .isOutOfStock(false)
                                .build()
                        )
                    )
                    .build()
            )
        )
        .build();

    // when
    when(managementStockApiService.updateStocks(any(),
        any(UpdateStockServiceRequest.class), any(LocalDate.class),
        any()
        )
    )
        .thenReturn(defaultResponse());

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/management/stock",
                    SHOP_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("management-stock-update",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장 아이디 shortUUID")
            ),
            requestFields(
                fieldWithPath("categories").type(JsonFieldType.ARRAY)
                    .description("카테고리 목록"),
                fieldWithPath("categories[].id").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("categories[].name").type(JsonFieldType.STRING)
                    .description("카테고리 이름"),
                fieldWithPath("categories[].menus[].id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("categories[].menus[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("categories[].menus[].additionalQuantity").type(JsonFieldType.NUMBER)
                    .description("추가 수량"),
                fieldWithPath("categories[].menus[].isOutOfStock").type(JsonFieldType.BOOLEAN)
                    .description("품절 여부")
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
                    .description("카테고리 노출 순서"),
                fieldWithPath("categories[].isStockUnderThreshold").type(JsonFieldType.BOOLEAN)
                    .description("부족한 재고 여부 (카테고리)"),
                fieldWithPath("categories[].menus").type(JsonFieldType.ARRAY)
                    .description("메뉴 목록"),
                fieldWithPath("categories[].menus[].id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("categories[].menus[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("categories[].menus[].ordering").type(JsonFieldType.NUMBER)
                    .description("메뉴 노출 순서"),
                fieldWithPath("categories[].menus[].isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("categories[].menus[].remainingQuantity").type(JsonFieldType.NUMBER)
                    .description("메뉴 남은 재고").optional(),
                fieldWithPath("categories[].menus[].isStockUnderThreshold")
                    .type(JsonFieldType.BOOLEAN)
                    .description("부족한 재고 여부"),
                fieldWithPath("categories[].menus[].isOutOfStock").type(JsonFieldType.BOOLEAN)
                    .description("품절 여부")
            )
        ));
  }


  private ManagementStockListResponse defaultResponse() {
    return ManagementStockListResponse.builder()
        .categories(
            List.of(
                ManagementStockListResponse.ManagementStockCategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("음식")
                    .ordering(1)
                    .isStockUnderThreshold(false)
                    .menus(
                        List.of(
                            ManagementStockListResponse.ManagementStockMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("돈까스")
                                .ordering(1)
                                .isUsedDailyStock(true)
                                .remainingQuantity(1000)
                                .isStockUnderThreshold(false)
                                .isOutOfStock(false)
                                .build(),

                            ManagementStockListResponse.ManagementStockMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("치즈돈까스")
                                .ordering(1)
                                .isUsedDailyStock(true)
                                .remainingQuantity(200)
                                .isStockUnderThreshold(false)
                                .isOutOfStock(false)
                                .build(),

                            ManagementStockListResponse.ManagementStockMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("양념돈까스")
                                .ordering(1)
                                .isUsedDailyStock(true)
                                .remainingQuantity(100)
                                .isStockUnderThreshold(false)
                                .isOutOfStock(false)
                                .build()
                        )
                    )
                    .build(),
                ManagementStockListResponse.ManagementStockCategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("음료")
                    .ordering(2)
                    .isStockUnderThreshold(false)
                    .menus(
                        List.of(
                            ManagementStockListResponse.ManagementStockMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("콜라")
                                .ordering(1)
                                .isUsedDailyStock(true)
                                .remainingQuantity(1000)
                                .isStockUnderThreshold(false)
                                .isOutOfStock(false)
                                .build(),

                            ManagementStockListResponse.ManagementStockMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("사이다")
                                .ordering(2)
                                .isUsedDailyStock(true)
                                .remainingQuantity(1000)
                                .isStockUnderThreshold(false)
                                .isOutOfStock(false)
                                .build()
                        )
                    )
                    .build()
            )
        )
        .build();
  }
}