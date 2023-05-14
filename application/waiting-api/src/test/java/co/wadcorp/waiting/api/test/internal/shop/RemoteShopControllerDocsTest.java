package co.wadcorp.waiting.api.test.internal.shop;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.internal.controller.shop.RemoteShopController;
import co.wadcorp.waiting.api.internal.service.shop.RemoteShopApiService;
import co.wadcorp.waiting.api.internal.service.shop.RemoteShopBulkApiService;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopBulkResponse;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopBulkResponse.ShopSeqPair;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopOperationResponse;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.settings.Precaution;
import co.wadcorp.waiting.data.domain.shop.operation.ClosedReason;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

class RemoteShopControllerDocsTest extends RestDocsSupport {

  private final RemoteShopApiService remoteShopApiService = mock(RemoteShopApiService.class);
  private final RemoteShopBulkApiService remoteShopBulkApiService = mock(
      RemoteShopBulkApiService.class);

  @Override
  public Object init() {
    return new RemoteShopController(remoteShopApiService, remoteShopBulkApiService);
  }

  @DisplayName("원격 웨이팅 매장 정보 목록")
  @Test
  void getShopOperationsTest() throws Exception {
    // given
    List<RemoteShopOperationResponse> response = List.of(RemoteShopOperationResponse.builder()
        .shopId(1L)
        .operationDate(LocalDate.of(2023, 2, 28))
        .isUsedRemoteWaiting(true)
        .operationStatus(OperationStatus.OPEN)
        .operationStartDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 10, 0)))
        .operationEndDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 22, 0)))
        .pauseStartDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 14, 0)))
        .pauseEndDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 15, 0)))
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
        .build()
    );

    // when
    when(remoteShopApiService.findShopOperations(any(), any(), any()))
        .thenReturn(response);

    // then
    mockMvc.perform(get("/internal/api/v1/shops/{shopIds}", "CSV_SHOP_IDS")
            .queryParam("operationDate", "2023-02-28")
        )
        .andExpect(status().isOk())
        .andDo(document("remote-shop-operations",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq CSV (1,2,3)")
            ),
            queryParameters(
                parameterWithName("operationDate")
                    .description("운영일 (yyyy-MM-dd)")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopId").type(JsonFieldType.NUMBER)
                    .description("B2C의 shopSeq"),
                fieldWithPath("operationDate").type(JsonFieldType.STRING)
                    .description("영업일 날짜 (yyyy-MM-dd)"),
                fieldWithPath("isUsedWaiting").type(JsonFieldType.BOOLEAN)
                    .description("현장 웨이팅 사용 여부 (가맹 여부)"),
                fieldWithPath("isUsedRemoteWaiting").type(JsonFieldType.BOOLEAN)
                    .description("원격 웨이팅 사용 여부"),
                fieldWithPath("operationStatus").type(JsonFieldType.STRING)
                    .description("영업 상태"),
                fieldWithPath("operationStartDateTime").type(JsonFieldType.STRING)
                    .description("영업 시작 시간"),
                fieldWithPath("operationEndDateTime").type(JsonFieldType.STRING)
                    .description("영업 종료 시간"),
                fieldWithPath("pauseStartDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("일시중지 시작 시간"),
                fieldWithPath("pauseEndDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("일시중지 종료 시간"),
                fieldWithPath("pauseReasonId").type(JsonFieldType.STRING)
                    .optional()
                    .description("일시중지 사유 ID"),
                fieldWithPath("pauseReason").type(JsonFieldType.STRING)
                    .optional()
                    .description("일시중지 사유"),
                fieldWithPath("autoPauseStartDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("자동 일시중지 시작 시간"),
                fieldWithPath("autoPauseEndDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("자동 일시중지 종료 시간"),
                fieldWithPath("autoPauseReasonId").type(JsonFieldType.STRING)
                    .optional()
                    .description("자동 일시중지 사유 ID"),
                fieldWithPath("autoPauseReason").type(JsonFieldType.STRING)
                    .optional()
                    .description("자동 일시중지 사유"),
                fieldWithPath("manualPauseStartDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("수동 일시중지 시작 시간"),
                fieldWithPath("manualPauseEndDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("수동 일시중지 종료 시간"),
                fieldWithPath("manualPauseReasonId").type(JsonFieldType.STRING)
                    .optional()
                    .description("수동 일시중지 사유 ID"),
                fieldWithPath("manualPauseReason").type(JsonFieldType.STRING)
                    .optional()
                    .description("수동 일시중지 사유"),
                fieldWithPath("closedReason").type(JsonFieldType.STRING)
                    .optional()
                    .description("영업 종료 사유"),
                fieldWithPath("autoAlarmOrdering").type(JsonFieldType.NUMBER)
                    .description("입장준비 알림톡 N번째 발송 설정값"),
                fieldWithPath("isUsedPrecautions").type(JsonFieldType.BOOLEAN)
                    .description("앱 내 유의사항 사용 여부"),
                fieldWithPath("precautions").type(JsonFieldType.ARRAY)
                    .description("앱 내 유의사항"),
                fieldWithPath("precautions[].id").type(JsonFieldType.STRING)
                    .description("앱 내 유의사항 ID"),
                fieldWithPath("precautions[].content").type(JsonFieldType.STRING)
                    .description("앱 내 유의사항 내용"),
                fieldWithPath("messagePrecaution").type(JsonFieldType.STRING)
                    .description("메시지 유의사항"),
                fieldWithPath("remoteOperationStartDateTime").type(JsonFieldType.STRING)
                    .description("원격 영업 시작 시간"),
                fieldWithPath("remoteOperationEndDateTime").type(JsonFieldType.STRING)
                    .description("원격 영업 종료 시간"),
                fieldWithPath("remoteAutoPauseStartDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("원격 자동 일시중지 시작 시간"),
                fieldWithPath("remoteAutoPauseEndDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("원격 자동 일시중지 종료 시간"),
                fieldWithPath("disablePutOff").type(JsonFieldType.BOOLEAN)
                    .description("미루기 off 매장 여부"),
                fieldWithPath("isPossibleOrder").type(JsonFieldType.BOOLEAN)
                    .description("선주문 사용 여부")
            )
        ));
  }

  @DisplayName("원격 웨이팅 매장 벌크")
  @Test
  void getShopOperationsByBulk() throws Exception {
    // given
    RemoteShopBulkResponse response = RemoteShopBulkResponse.builder()
        .shopIdPairs(List.of(
            ShopSeqPair.builder()
                .seq(1L)
                .shopId(11L)
                .waitingShopId("shopId-1-UUID")
                .build(),
            ShopSeqPair.builder()
                .seq(2L)
                .shopId(null)
                .waitingShopId("shopId-2-UUID")
                .build(),
            ShopSeqPair.builder()
                .seq(3L)
                .shopId(13L)
                .waitingShopId("shopId-3-UUID")
                .build()
        ))
        .build();

    // when
    when(remoteShopBulkApiService.findShopsByBulk(anyString(), any()))
        .thenReturn(response);

    // then
    mockMvc.perform(get("/internal/api/v1/shops/bulk")
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
            .queryParam("minSeq", "1")
            .queryParam("size", "100")
        )
        .andExpect(status().isOk())
        .andDo(document("remote-shop-bulk",
            getDocumentRequest(),
            getDocumentResponse(),
            queryParameters(
                parameterWithName("minSeq")
                    .description("최소 Seq (이전 페이지 마지막 아이템의 seq + 1. 단, 첫 페이지는 0)"),
                parameterWithName("size")
                    .description("페이징 개수")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopIdPairs").type(JsonFieldType.ARRAY)
                    .description("매장 seq, B2C의 shopSeq 묶음"),
                fieldWithPath("shopIdPairs[].seq").type(JsonFieldType.NUMBER)
                    .description("매장 Seq (마지막 아이템의 (seq + 1) 값을 다음 minSeq에 요청. 단, 첫 페이지는 0)"),
                fieldWithPath("shopIdPairs[].shopId").type(JsonFieldType.NUMBER)
                    .optional()
                    .description("B2C의 shopSeq"),
                fieldWithPath("shopIdPairs[].waitingShopId").type(JsonFieldType.STRING)
                    .description("매장 ID (UUID)")
            )
        ));
  }

}
