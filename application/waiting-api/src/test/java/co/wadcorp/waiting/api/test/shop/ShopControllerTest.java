package co.wadcorp.waiting.api.test.shop;

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

import co.wadcorp.waiting.api.controller.shop.ShopController;
import co.wadcorp.waiting.api.controller.shop.dto.UpdateShopRequest;
import co.wadcorp.waiting.api.model.shop.response.ShopResponse;
import co.wadcorp.waiting.api.model.shop.response.ShopsResponse;
import co.wadcorp.waiting.api.model.shop.vo.BusinessInfoVO;
import co.wadcorp.waiting.api.model.shop.vo.ShopInfoVO;
import co.wadcorp.waiting.api.service.shop.ShopApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class ShopControllerTest  extends RestDocsSupport {

  private final ShopApiService shopApiService = mock(ShopApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new ShopController(shopApiService);
  }

  @Test
  @DisplayName("매장 목록 조회")
  void getShops() throws Exception {
    // given
    List<ShopInfoVO> shopInfo = List.of(createShopInfo("SHOP_UUID1", "매장1"),
        createShopInfo("SHOP_UUID2", "매장2"));

    final String ctmAuth = "Bearer accessToken";
    ShopsResponse shopsResponse = new ShopsResponse(shopInfo);


    // when
    when(shopApiService.getShops(any())).thenReturn(shopsResponse);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth))
        .andExpect(status().isOk())
        .andDo(document("shop-shops",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopList").type(JsonFieldType.ARRAY).description("매장 목록"),
                fieldWithPath("shopList[].shopId").type(JsonFieldType.STRING).description("매장 아이디"),
                fieldWithPath("shopList[].shopName").type(JsonFieldType.STRING).description("매장 이름"),
                fieldWithPath("shopList[].isRemoveProcessing").type(JsonFieldType.BOOLEAN).description("삭제처리중 여부"),
                fieldWithPath("shopList[].isCatchWaiting").type(JsonFieldType.BOOLEAN).description("캐치테이블 웨이팅 사용 여부")
            )
        ));
  }

  @Test
  @DisplayName("단일 매장 조회")
  void getShop() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    final ShopResponse response = ShopResponse.builder()
        .shopId("shopId")
        .shopName("아무개")
        .shopAddress("서울특별시 어딘가")
        .shopTelNumber("010-1234-5657")
        .build();

    // when
    when(shopApiService.getShop(any())).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth))
        .andExpect(status().isOk())
        .andDo(document("shop-shop",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopId").type(JsonFieldType.STRING).description("매장 아이디"),
                fieldWithPath("shopName").type(JsonFieldType.STRING).description("매장 이름"),
                fieldWithPath("shopAddress").type(JsonFieldType.STRING).description("매장 주소"),
                fieldWithPath("shopTelNumber").type(JsonFieldType.STRING).description("매장 연락처")
            )
        ));
  }


  @Test
  @DisplayName("단일 매장 수정")
  void updateShop() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    ShopResponse response = ShopResponse.builder()
        .shopId("shopId")
        .shopName("아무개")
        .shopAddress("서울특별시 어딘가")
        .shopTelNumber("010-1234-5657")
        .build();

    UpdateShopRequest request = new UpdateShopRequest("010-1234-5657");


    // when
    when(shopApiService.updateShop(any(), any(UpdateShopRequest.class))).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/update", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("shop-update",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("shopTelNumber").type(JsonFieldType.STRING).description("변경할 매장 연락처")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopId").type(JsonFieldType.STRING).description("매장 아이디"),
                fieldWithPath("shopName").type(JsonFieldType.STRING).description("매장 이름"),
                fieldWithPath("shopAddress").type(JsonFieldType.STRING).description("매장 주소"),
                fieldWithPath("shopTelNumber").type(JsonFieldType.STRING).description("매장 연락처")
            )
        ));
  }

  private ShopInfoVO createShopInfo(String shopId, String shopName) {
    return ShopInfoVO.builder()
        .shopId(shopId)
        .shopName(shopName)
        .isRemoveProcessing(false)
        .isCatchWaiting(true)
        .build();
  }
}
