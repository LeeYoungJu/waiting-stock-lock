package co.wadcorp.waiting.api.model.notice.request;

import java.util.List;
import lombok.Getter;

@Getter
public class ReadNoticeRequest {

  private List<Long> noticeSeqList;

}
