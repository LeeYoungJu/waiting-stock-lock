package co.wadcorp.waiting.gateway.auth;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

/**
 * CIDR 주소를 받아서 해당 CIDR 범위 내에 특정 IP가 있는지를 검사하는 기능을 제공한다.
 */
@Slf4j
public class CidrRangeChecker {

  private final List<SubnetInfo> subnetInfos;

  /**
   * CIDR 문자열들을 받아서 CidrCheck 인스턴스를 생성한다.
   *
   * @param cidrs CIDR 형식의 문자열 컬렉션.
   */
  public CidrRangeChecker(Collection<String> cidrs) {
    cidrs = Objects.requireNonNullElse(cidrs, Collections.emptySet());

    this.subnetInfos = cidrs.stream().map(cidr -> {
      SubnetUtils subnetUtils = new SubnetUtils(cidr);
      return subnetUtils.getInfo();
    }).toList();
    log.info("cidrs:{}", cidrs);
  }

  /**
   * 주어진 IP가 cidr 범위 내에 있는지 여부를 반환한다.
   */
  public boolean isInRange(final String ip) {
    if (StringUtils.isBlank(ip)) {
      return false;
    }

    for (SubnetUtils.SubnetInfo subnetInfo : subnetInfos) {
      if (StringUtils.equals(subnetInfo.getAddress(), ip)) {
        return true;
      }
      boolean isInRange = subnetInfo.isInRange(ip);
      if (isInRange) {
        return true;
      }
    }
    return false;
  }

  public boolean isInRange(final InetSocketAddress inetSocketAddress) {
    if (inetSocketAddress == null) {
      throw new IllegalArgumentException("inetSocketAddress");
    }

    String ip = inetSocketAddress.getAddress().getHostAddress();
    return isInRange(ip);
  }

}
