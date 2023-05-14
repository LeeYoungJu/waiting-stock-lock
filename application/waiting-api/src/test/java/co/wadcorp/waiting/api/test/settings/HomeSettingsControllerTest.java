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
import co.wadcorp.waiting.api.controller.settings.HomeSettingsController;
import co.wadcorp.waiting.api.model.settings.request.HomeSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.HomeSettingsResponse;
import co.wadcorp.waiting.api.model.settings.vo.HomeSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.SeatOptionSettingVO;
import co.wadcorp.waiting.api.service.settings.HomeSettingsApiService;
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

public class HomeSettingsControllerTest extends RestDocsSupport {

  private final HomeSettingsApiService homeSettingsApiService = mock(HomeSettingsApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new HomeSettingsController(homeSettingsApiService);
  }

  @Test
  @DisplayName("웨이팅_홈_설정_조회")
  public void getHomeSettingsTest() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";
    HomeSettingsVO vo = HomeSettingsVO.builder()
        .waitingModeType("DEFAULT")
        .defaultModeSettings(getDefaultModeSettings())
        .tableModeSettings(getTableModeSettings())
        .build();

    HomeSettingsResponse response = HomeSettingsResponse.builder()
        .homeSettings(vo)
        .existsWaitingTeam(true)
        .isOpenedOperation(true)
        .build();

    // when
    when(homeSettingsApiService.getHomeSettings(
        any(),
        any(LocalDate.class),
        any(ZonedDateTime.class)
    )).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/settings/waiting-home",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("home-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("waitingModeType").type(JsonFieldType.STRING)
                    .description("웨이팅 타입 (DEFAULT/TABLE)"),
                fieldWithPath("defaultModeSettings").type(JsonFieldType.OBJECT)
                    .description("기본모드 설정"),
                fieldWithPath("defaultModeSettings.id").type(JsonFieldType.STRING)
                    .description("기본모드 UUID"),
                fieldWithPath("defaultModeSettings.name").type(JsonFieldType.STRING)
                    .description("기본모드 좌석옵션명"),
                fieldWithPath("defaultModeSettings.minSeatCount").type(JsonFieldType.NUMBER)
                    .description("기본모드 최소 착석인원"),
                fieldWithPath("defaultModeSettings.maxSeatCount").type(JsonFieldType.NUMBER)
                    .description("기본모드 최대 착석인원"),
                fieldWithPath("defaultModeSettings.expectedWaitingPeriod").type(
                    JsonFieldType.NUMBER).description("테이블별 예상 웨이팅 시간"),
                fieldWithPath("defaultModeSettings.isUsedExpectedWaitingPeriod").type(
                    JsonFieldType.BOOLEAN).description("예상 웨이팅 시간 사용여부"),
                fieldWithPath("defaultModeSettings.isDefault").type(JsonFieldType.BOOLEAN)
                    .description("기본값 여부(기본모드는 항상 true)"),
                fieldWithPath("defaultModeSettings.isPickup").type(JsonFieldType.BOOLEAN)
                    .description("포장 여부(true-인원수 비활성화)"),
                fieldWithPath("tableModeSettings").type(JsonFieldType.ARRAY)
                    .description("테이블모드 설정"),
                fieldWithPath("tableModeSettings[].id").type(JsonFieldType.STRING)
                    .description("테이블 UUID"),
                fieldWithPath("tableModeSettings[].name").type(JsonFieldType.STRING)
                    .description("테이블별 좌석옵션명"),
                fieldWithPath("tableModeSettings[].minSeatCount").type(JsonFieldType.NUMBER)
                    .description("테이블별 최소 착석인원"),
                fieldWithPath("tableModeSettings[].maxSeatCount").type(JsonFieldType.NUMBER)
                    .description("테이블별 최대 착석인원"),
                fieldWithPath("tableModeSettings[].expectedWaitingPeriod").type(
                    JsonFieldType.NUMBER).description("테이블별 예상 웨이팅 시간"),
                fieldWithPath("tableModeSettings[].isUsedExpectedWaitingPeriod").type(
                    JsonFieldType.BOOLEAN).description("테이블별 예상 웨이팅 시간 사용여부"),
                fieldWithPath("tableModeSettings[].isDefault").type(JsonFieldType.BOOLEAN)
                    .description("기본값 여부(true-삭제버튼 비활성화)"),
                fieldWithPath("tableModeSettings[].isPickup").type(JsonFieldType.BOOLEAN)
                    .description("포장 여부(true-인원수 비활성화)"),
                fieldWithPath("existsWaitingTeam").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅중인 팀 존재여부 (true-설정수정불가)"),
                fieldWithPath("isOpenedOperation").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅 접수 중 여부 (true-설정수정불가)")
            )
        ));
  }

  @Test
  @DisplayName("웨이팅_홈_설정_등록")
  public void saveHomeSettingsTest() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";
    HomeSettingsRequest request = HomeSettingsRequest.builder()
        .waitingModeType("DEFAULT")
        .defaultModeSettings(getDefaultModeSettings())
        .tableModeSettings(getTableModeSettings())
        .build();

    HomeSettingsVO vo = HomeSettingsVO.builder()
        .waitingModeType("DEFAULT")
        .defaultModeSettings(getDefaultModeSettings())
        .tableModeSettings(getTableModeSettings())
        .build();

    HomeSettingsResponse response = HomeSettingsResponse.builder()
        .homeSettings(vo)
        .existsWaitingTeam(true)
        .isOpenedOperation(true)
        .build();

    // when
    when(homeSettingsApiService.saveHomeSettings(any(), any(), any(), any(LocalDate.class), any(ZonedDateTime.class))).thenReturn(
        response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/settings/waiting-home",
                    SHOP_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("home-settings-save",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("waitingModeType").type(JsonFieldType.STRING).description("웨이팅 타입"),
                fieldWithPath("defaultModeSettings").type(JsonFieldType.OBJECT)
                    .description("기본모드 설정"),
                fieldWithPath("defaultModeSettings.id").type(JsonFieldType.STRING)
                    .description("기본모드 UUID"),
                fieldWithPath("defaultModeSettings.name").type(JsonFieldType.STRING)
                    .description("기본모드 좌석옵션명"),
                fieldWithPath("defaultModeSettings.minSeatCount").type(JsonFieldType.NUMBER)
                    .description("기본모드 최소 착석인원"),
                fieldWithPath("defaultModeSettings.maxSeatCount").type(JsonFieldType.NUMBER)
                    .description("기본모드 최대 착석인원"),
                fieldWithPath("defaultModeSettings.expectedWaitingPeriod").type(
                    JsonFieldType.NUMBER).description("예상 웨이팅 시간"),
                fieldWithPath("defaultModeSettings.isUsedExpectedWaitingPeriod").type(
                    JsonFieldType.BOOLEAN).description("예상 웨이팅 시간 사용여부"),
                fieldWithPath("defaultModeSettings.isDefault").type(JsonFieldType.BOOLEAN)
                    .description("기본값 여부(기본모드는 항상 true)"),
                fieldWithPath("defaultModeSettings.isPickup").type(JsonFieldType.BOOLEAN)
                    .description("포장 여부(true-인원수 비활성화)"),
                fieldWithPath("tableModeSettings").type(JsonFieldType.ARRAY)
                    .description("테이블모드 설정"),
                fieldWithPath("tableModeSettings[].id").type(JsonFieldType.STRING)
                    .description("테이블 UUID"),
                fieldWithPath("tableModeSettings[].name").type(JsonFieldType.STRING)
                    .description("테이블별 좌석옵션명"),
                fieldWithPath("tableModeSettings[].minSeatCount").type(JsonFieldType.NUMBER)
                    .description("테이블별 최소 착석인원"),
                fieldWithPath("tableModeSettings[].maxSeatCount").type(JsonFieldType.NUMBER)
                    .description("테이블별 최대 착석인원"),
                fieldWithPath("tableModeSettings[].expectedWaitingPeriod").type(
                    JsonFieldType.NUMBER).description("테이블별 예상 웨이팅 시간"),
                fieldWithPath("tableModeSettings[].isUsedExpectedWaitingPeriod").type(
                    JsonFieldType.BOOLEAN).description("테이블별 예상 웨이팅 시간 사용여부"),
                fieldWithPath("tableModeSettings[].isDefault").type(JsonFieldType.BOOLEAN)
                    .description("기본값 여부(true-삭제버튼 비활성화)"),
                fieldWithPath("tableModeSettings[].isPickup").type(JsonFieldType.BOOLEAN)
                    .description("포장 여부(true-인원수 비활성화)")

            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("waitingModeType").type(JsonFieldType.STRING).description("웨이팅 타입"),
                fieldWithPath("defaultModeSettings").type(JsonFieldType.OBJECT)
                    .description("기본모드 설정"),
                fieldWithPath("defaultModeSettings.id").type(JsonFieldType.STRING)
                    .description("기본모드 UUID"),
                fieldWithPath("defaultModeSettings.name").type(JsonFieldType.STRING)
                    .description("기본모드 좌석옵션명"),
                fieldWithPath("defaultModeSettings.minSeatCount").type(JsonFieldType.NUMBER)
                    .description("기본모드 최소 착석인원"),
                fieldWithPath("defaultModeSettings.maxSeatCount").type(JsonFieldType.NUMBER)
                    .description("기본모드 최대 착석인원"),
                fieldWithPath("defaultModeSettings.expectedWaitingPeriod").type(
                    JsonFieldType.NUMBER).description("테이블별 예상 웨이팅 시간"),
                fieldWithPath("defaultModeSettings.isUsedExpectedWaitingPeriod").type(
                    JsonFieldType.BOOLEAN).description("예상 웨이팅 시간 사용여부"),
                fieldWithPath("defaultModeSettings.isDefault").type(JsonFieldType.BOOLEAN)
                    .description("기본값 여부(기본모드는 항상 true)"),
                fieldWithPath("defaultModeSettings.isPickup").type(JsonFieldType.BOOLEAN)
                    .description("포장 여부(true-인원수 비활성화)"),
                fieldWithPath("tableModeSettings").type(JsonFieldType.ARRAY)
                    .description("테이블모드 설정"),
                fieldWithPath("tableModeSettings[].id").type(JsonFieldType.STRING)
                    .description("테이블모드 UUID"),
                fieldWithPath("tableModeSettings[].name").type(JsonFieldType.STRING)
                    .description("테이블별 좌석옵션명"),
                fieldWithPath("tableModeSettings[].minSeatCount").type(JsonFieldType.NUMBER)
                    .description("테이블별 최소 착석인원"),
                fieldWithPath("tableModeSettings[].maxSeatCount").type(JsonFieldType.NUMBER)
                    .description("테이블별 최대 착석인원"),
                fieldWithPath("tableModeSettings[].expectedWaitingPeriod").type(
                    JsonFieldType.NUMBER).description("테이블별 예상 웨이팅 시간"),
                fieldWithPath("tableModeSettings[].isUsedExpectedWaitingPeriod").type(
                    JsonFieldType.BOOLEAN).description("테이블별 예상 웨이팅 시간 사용여부"),
                fieldWithPath("tableModeSettings[].isDefault").type(JsonFieldType.BOOLEAN)
                    .description("기본값 여부(true-삭제버튼 비활성화)"),
                fieldWithPath("tableModeSettings[].isPickup").type(JsonFieldType.BOOLEAN)
                    .description("포장 여부(true-인원수 비활성화)"),
                fieldWithPath("existsWaitingTeam").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅중인 팀 존재여부 (true-설정수정불가)"),
                fieldWithPath("isOpenedOperation").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅 접수 중 여부 (true-설정수정불가)")
            )
        ));
  }

  private static SeatOptionSettingVO getDefaultModeSettings() {
    return SeatOptionSettingVO.builder()
        .id(UUIDUtil.shortUUID())
        .name("착석")
        .minSeatCount(1)
        .maxSeatCount(4)
        .expectedWaitingPeriod(10)
        .isUsedExpectedWaitingPeriod(true)
        .isDefault(true)
        .isPickup(false)
        .build();
  }

  private static List<SeatOptionSettingVO> getTableModeSettings() {
    return List.of(
        SeatOptionSettingVO.builder()
            .id(UUIDUtil.shortUUID())
            .name("테이블1")
            .minSeatCount(1)
            .maxSeatCount(4)
            .expectedWaitingPeriod(10)
            .isUsedExpectedWaitingPeriod(true)
            .isDefault(true)
            .isPickup(false)
            .build(),
        SeatOptionSettingVO.builder()
            .id(UUIDUtil.shortUUID())
            .name("테이블2")
            .minSeatCount(1)
            .maxSeatCount(4)
            .expectedWaitingPeriod(10)
            .isUsedExpectedWaitingPeriod(true)
            .isDefault(true)
            .isPickup(false)
            .build());
  }
}
