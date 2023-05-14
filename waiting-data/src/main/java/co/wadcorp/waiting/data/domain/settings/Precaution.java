package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.exception.AppException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Precaution {
  private String id;
  private String content;

  @Builder
  public Precaution(String id, String content) {
    if(StringUtils.hasText(content) && content.length() > 35) {
      throw new AppException(HttpStatus.BAD_REQUEST, "앱 내 유의사항은 최대 35자까지 입력할 수 있습니다.");
    }

    this.id = id;
    this.content = content;
  }
}
