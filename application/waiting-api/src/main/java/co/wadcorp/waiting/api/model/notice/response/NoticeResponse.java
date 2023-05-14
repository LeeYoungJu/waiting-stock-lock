package co.wadcorp.waiting.api.model.notice.response;

import co.wadcorp.waiting.data.domain.notice.NoticeVO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeResponse {

  private List<NoticeVO> noticeList;


  public static NoticeResponse toDto(List<NoticeVO> noticeVOList) {
    return new NoticeResponse(noticeVOList);
  }

  public static NoticeResponse toDto(NoticeVO noticeVO) {
    return toDto(List.of(noticeVO));
  }

}
