package co.wadcorp.waiting.api.test.internal.meta;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.internal.controller.meta.RemoteMetaController;
import co.wadcorp.waiting.api.internal.service.meta.RemoteMetaApiService;
import co.wadcorp.waiting.api.internal.service.meta.dto.RemoteMetaResponse;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse.AdditionalOptionVO;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse.PersonOptionVO;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopOperationResponse;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse.ModeSettingsVO;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.settings.Precaution;
import co.wadcorp.waiting.data.domain.shop.operation.ClosedReason;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class RemoteMetaControllerDocsTest extends RestDocsSupport {

  private final RemoteMetaApiService remoteMetaApiService = mock(RemoteMetaApiService.class);

  @Override
  public Object init() {
    return new RemoteMetaController(remoteMetaApiService);
  }

  @DisplayName("원격 웨이팅 인원 옵션 목록")
  @Test
  void getMeta() throws Exception {
    // given
    List<RemoteMetaResponse> response = List.of(createMetaResponse());

    // when
    when(remoteMetaApiService.getMeta(
        any(ChannelShopIdMapping.class),
        any(LocalDate.class),
        any(ZonedDateTime.class)
    )).thenReturn(response);

    // then
    mockMvc.perform(get("/internal/api/v1/shops/{shopIds}/meta", "CSV_SHOP_IDS")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
        )
        .andExpect(status().isOk())
        .andDo(document("remote-meta",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq CSV (1,2,3)")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopId").type(JsonFieldType.NUMBER)
                    .description("B2C의 shopSeq"),
                fieldWithPath("shopOperation.shopId").type(JsonFieldType.NUMBER)
                    .description("B2C의 shopSeq"),
                fieldWithPath("shopOperation.operationDate").type(JsonFieldType.STRING)
                    .description("영업일 날짜 (yyyy-MM-dd)"),
                fieldWithPath("shopOperation.isUsedWaiting").type(JsonFieldType.BOOLEAN)
                    .description("현장 웨이팅 사용 여부 (가맹 여부)"),
                fieldWithPath("shopOperation.isUsedRemoteWaiting").type(JsonFieldType.BOOLEAN)
                    .description("원격 웨이팅 사용 여부"),
                fieldWithPath("shopOperation.operationStatus").type(JsonFieldType.STRING)
                    .description("영업 상태"),
                fieldWithPath("shopOperation.operationStartDateTime").type(JsonFieldType.STRING)
                    .description("영업 시작 시간"),
                fieldWithPath("shopOperation.operationEndDateTime").type(JsonFieldType.STRING)
                    .description("영업 종료 시간"),
                fieldWithPath("shopOperation.pauseStartDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("일시중지 시작 시간"),
                fieldWithPath("shopOperation.pauseEndDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("일시중지 종료 시간"),
                fieldWithPath("shopOperation.pauseReasonId").type(JsonFieldType.STRING)
                    .optional()
                    .description("일시중지 사유 ID"),
                fieldWithPath("shopOperation.pauseReason").type(JsonFieldType.STRING)
                    .optional()
                    .description("일시중지 사유"),
                fieldWithPath("shopOperation.autoPauseStartDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("자동 일시중지 시작 시간"),
                fieldWithPath("shopOperation.autoPauseEndDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("자동 일시중지 종료 시간"),
                fieldWithPath("shopOperation.autoPauseReasonId").type(JsonFieldType.STRING)
                    .optional()
                    .description("자동 일시중지 사유 ID"),
                fieldWithPath("shopOperation.autoPauseReason").type(JsonFieldType.STRING)
                    .optional()
                    .description("자동 일시중지 사유"),
                fieldWithPath("shopOperation.manualPauseStartDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("수동 일시중지 시작 시간"),
                fieldWithPath("shopOperation.manualPauseEndDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("수동 일시중지 종료 시간"),
                fieldWithPath("shopOperation.manualPauseReasonId").type(JsonFieldType.STRING)
                    .optional()
                    .description("수동 일시중지 사유 ID"),
                fieldWithPath("shopOperation.manualPauseReason").type(JsonFieldType.STRING)
                    .optional()
                    .description("수동 일시중지 사유"),
                fieldWithPath("shopOperation.closedReason").type(JsonFieldType.STRING)
                    .optional()
                    .description("영업 종료 사유"),
                fieldWithPath("shopOperation.autoAlarmOrdering").type(JsonFieldType.NUMBER)
                    .description("입장준비 알림톡 N번째 발송 설정값"),
                fieldWithPath("shopOperation.isUsedPrecautions").type(JsonFieldType.BOOLEAN)
                    .description("앱 내 유의사항 사용 여부"),
                fieldWithPath("shopOperation.precautions").type(JsonFieldType.ARRAY)
                    .description("앱 내 유의사항"),
                fieldWithPath("shopOperation.precautions[].id").type(JsonFieldType.STRING)
                    .description("앱 내 유의사항 ID"),
                fieldWithPath("shopOperation.precautions[].content").type(JsonFieldType.STRING)
                    .description("앱 내 유의사항 내용"),
                fieldWithPath("shopOperation.messagePrecaution").type(JsonFieldType.STRING)
                    .description("메시지 유의사항"),
                fieldWithPath("shopOperation.remoteOperationStartDateTime")
                    .type(JsonFieldType.STRING)
                    .description("원격 영업 시작 시간"),
                fieldWithPath("shopOperation.remoteOperationEndDateTime").type(JsonFieldType.STRING)
                    .description("원격 영업 종료 시간"),
                fieldWithPath("shopOperation.remoteAutoPauseStartDateTime")
                    .type(JsonFieldType.STRING)
                    .optional()
                    .description("원격 자동 일시중지 시작 시간"),
                fieldWithPath("shopOperation.remoteAutoPauseEndDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("원격 자동 일시중지 종료 시간"),
                fieldWithPath("shopOperation.disablePutOff").type(JsonFieldType.BOOLEAN)
                    .description("미루기 off 매장 여부"),
                fieldWithPath("shopOperation.isPossibleOrder").type(JsonFieldType.BOOLEAN)
                    .description("선주문 사용 여부"),

                fieldWithPath("tableSetting.shopId").type(JsonFieldType.NUMBER)
                    .description("B2C의 shopSeq"),
                fieldWithPath("tableSetting.waitingModeType").type(JsonFieldType.STRING)
                    .description("테이블 모드 정보 (DEFAULT, TABLE)"),
                fieldWithPath("tableSetting.defaultModeSettings").type(JsonFieldType.OBJECT)
                    .description("기본모드(DEFAULT)에서 사용하는 테이블 정보"),
                fieldWithPath("tableSetting.defaultModeSettings.id").type(JsonFieldType.STRING)
                    .description("테이블 ID"),
                fieldWithPath("tableSetting.defaultModeSettings.name").type(JsonFieldType.STRING)
                    .description("테이블 이름"),
                fieldWithPath("tableSetting.defaultModeSettings.minSeatCount")
                    .type(JsonFieldType.NUMBER)
                    .description("테이블 최소 착석 수"),
                fieldWithPath("tableSetting.defaultModeSettings.maxSeatCount")
                    .type(JsonFieldType.NUMBER)
                    .description("테이블 최대 착석 수"),
                fieldWithPath("tableSetting.defaultModeSettings.expectedWaitingPeriod")
                    .type(JsonFieldType.NUMBER)
                    .description("예상 대기 시간 (팀당 시간, 분 단위)"),
                fieldWithPath("tableSetting.defaultModeSettings.isUsedExpectedWaitingPeriod")
                    .type(JsonFieldType.BOOLEAN)
                    .description("예상 대기 시간 사용 여부"),
                fieldWithPath("tableSetting.defaultModeSettings.isTakeOut")
                    .type(JsonFieldType.BOOLEAN)
                    .description("포장 여부"),
                fieldWithPath("tableSetting.tableModeSettings").type(JsonFieldType.ARRAY)
                    .description("테이블 모드(TABLE)에서 사용하는 테이블 정보"),
                fieldWithPath("tableSetting.tableModeSettings[].id").type(JsonFieldType.STRING)
                    .description("테이블 ID"),
                fieldWithPath("tableSetting.tableModeSettings[].name").type(JsonFieldType.STRING)
                    .description("테이블 이름"),
                fieldWithPath("tableSetting.tableModeSettings[].minSeatCount")
                    .type(JsonFieldType.NUMBER)
                    .description("테이블 최소 착석 수"),
                fieldWithPath("tableSetting.tableModeSettings[].maxSeatCount")
                    .type(JsonFieldType.NUMBER)
                    .description("테이블 최대 착석 수"),
                fieldWithPath("tableSetting.tableModeSettings[].expectedWaitingPeriod")
                    .type(JsonFieldType.NUMBER)
                    .description("예상 대기 시간 (팀당 시간, 분 단위)"),
                fieldWithPath("tableSetting.tableModeSettings[].isUsedExpectedWaitingPeriod")
                    .type(JsonFieldType.BOOLEAN)
                    .description("예상 대기 시간 사용 여부"),
                fieldWithPath("tableSetting.tableModeSettings[].isTakeOut")
                    .type(JsonFieldType.BOOLEAN)
                    .description("포장 여부"),

                fieldWithPath("personOption.shopId").type(JsonFieldType.NUMBER)
                    .description("B2C의 shopSeq"),
                fieldWithPath("personOption.isUsedPersonOptionSetting").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 사용 여부"),
                fieldWithPath("personOption.personOptions").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 정보"),
                fieldWithPath("personOption.personOptions[].id").type(JsonFieldType.STRING)
                    .description("인원 옵션 ID"),
                fieldWithPath("personOption.personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 이름"),
                fieldWithPath("personOption.personOptions[].isDisplayed")
                    .type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 노출 여부"),
                fieldWithPath("personOption.personOptions[].isSeat").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 착석 여부 (테이블 착석자 수에 포함 여부)"),
                fieldWithPath("personOption.personOptions[].additionalOptions")
                    .type(JsonFieldType.ARRAY)
                    .description("추가 인원 옵션"),
                fieldWithPath("personOption.personOptions[].additionalOptions[].id")
                    .type(JsonFieldType.STRING)
                    .description("추가 인원 옵션 ID"),
                fieldWithPath("personOption.personOptions[].additionalOptions[].name")
                    .type(JsonFieldType.STRING)
                    .description("추가 인원 옵션 이름"),
                fieldWithPath("personOption.personOptions[].additionalOptions[].isDisplayed")
                    .type(JsonFieldType.BOOLEAN)
                    .description("추가 인원 옵션 노출 여부")
            )
        ));
  }

  private static RemoteMetaResponse createMetaResponse() {
    RemoteShopOperationResponse remoteShopOperationResponse = RemoteShopOperationResponse.builder()
        .shopId(1L)
        .operationDate(LocalDate.of(2023, 2, 28))
        .isUsedRemoteWaiting(true)
        .operationStatus(OperationStatus.OPEN)
        .operationStartDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 10, 0)))
        .operationEndDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 22, 0)))
        .pauseStartDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 14, 0)))
        .pauseEndDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 15, 0)))
        .pauseReasonId(UUIDUtil.shortUUID())
        .pauseReason("잠시만 기다려주세요.")
        .autoPauseStartDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 14, 0)))
        .autoPauseEndDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 15, 0)))
        .autoPauseReasonId(UUIDUtil.shortUUID())
        .autoPauseReason("잠시만 기다려주세요.")
        .manualPauseStartDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 14, 0)))
        .manualPauseEndDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 15, 0)))
        .manualPauseReasonId(UUIDUtil.shortUUID())
        .manualPauseReason("잠시만 기다려주세요.")
        .closedReason(ClosedReason.MANUAL)
        .autoAlarmOrdering(5)
        .isUsedPrecautions(true)
        .isPossibleOrder(true)
        .precautions(List.of(Precaution.builder()
            .id(UUIDUtil.shortUUID())
            .content("호출 알림톡을 받고 5분 동안 미입장 시, 자동취소됩니다.")
            .build())
        )
        .messagePrecaution("알림톡이나 문자에 들어가는 유의사항")
        .remoteOperationStartDateTime(
            ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 10, 30))
        )
        .remoteOperationEndDateTime(
            ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 21, 30))
        )
        .remoteAutoPauseStartDateTime(
            ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 14, 0))
        )
        .remoteAutoPauseEndDateTime(
            ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 15, 0))
        )
        .build();

    RemotePersonOptionResponse remotePersonOptionResponse = RemotePersonOptionResponse.builder()
        .shopId(1L)
        .isUsedPersonOptionSetting(true)
        .personOptions(List.of(PersonOptionVO.builder()
            .id(UUIDUtil.shortUUID())
            .name("유아")
            .isDisplayed(true)
            .isSeat(true)
            .additionalOptions(List.of(AdditionalOptionVO.builder()
                .id(UUIDUtil.shortUUID())
                .name("유아용 의자")
                .isDisplayed(true)
                .build()
            ))
            .build()
        ))
        .build();

    RemoteTableSettingResponse remoteTableSettingResponse = RemoteTableSettingResponse.builder()
        .shopId(1L)
        .waitingModeType(WaitingModeType.DEFAULT)
        .defaultModeSettings(ModeSettingsVO.builder()
            .id(UUIDUtil.shortUUID())
            .name("착석")
            .minSeatCount(1)
            .maxSeatCount(4)
            .expectedWaitingPeriod(10)
            .isUsedExpectedWaitingPeriod(true)
            .isTakeOut(false)
            .build()
        )
        .tableModeSettings(List.of(
            ModeSettingsVO.builder()
                .id(UUIDUtil.shortUUID())
                .name("테이블1")
                .minSeatCount(1)
                .maxSeatCount(4)
                .expectedWaitingPeriod(10)
                .isUsedExpectedWaitingPeriod(true)
                .isTakeOut(false)
                .build(),
            ModeSettingsVO.builder()
                .id(UUIDUtil.shortUUID())
                .name("테이블2")
                .minSeatCount(1)
                .maxSeatCount(4)
                .expectedWaitingPeriod(10)
                .isUsedExpectedWaitingPeriod(true)
                .isTakeOut(false)
                .build()
        ))
        .build();

    return RemoteMetaResponse
        .builder()
        .shopId(1L)
        .shopOperation(remoteShopOperationResponse)
        .tableSetting(remoteTableSettingResponse)
        .personOption(remotePersonOptionResponse)
        .build();
  }
}
