package co.wadcorp.waiting.api.service.user;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.api.controller.user.dto.UserResponse;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.infra.pos.CatchtablePosShopClient;
import co.wadcorp.waiting.infra.pos.CatchtablePosUserClient;
import co.wadcorp.waiting.infra.pos.dto.BusinessInfo;
import co.wadcorp.waiting.infra.pos.dto.PosApiResponse;
import co.wadcorp.waiting.infra.pos.dto.PosShopResponse;
import co.wadcorp.waiting.infra.pos.dto.PosUserResponse;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserApiService {

  private final CatchtablePosUserClient catchtablePosUserClient;
  private final CatchtablePosShopClient catchtablePosShopClient;


  public UserResponse getUserInfo(String shopId, String ctmAuth) {

    PosApiResponse<PosUserResponse> posUserResponse = catchtablePosUserClient.getUserInfo(shopId,
        ctmAuth);
    PosApiResponse<PosShopResponse> posShopResponse = catchtablePosShopClient.getShop(shopId,
        ctmAuth);


    if (posUserResponse.isUnauthorized() || posShopResponse.isUnauthorized()) {
      throw new AppException(HttpStatus.UNAUTHORIZED);
    }

    if (posUserResponse.isError() || posShopResponse.isError()) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.POS_API_ERROR_USER_INFO);
    }

    PosUserResponse posUser = posUserResponse.getData();
    PosShopResponse posShop = posShopResponse.getData();

    PhoneNumber phoneNumber = PhoneNumberUtils.ofKr(posUser.getPhone());

    UserResponse.User user = UserResponse.User.builder()
        .phone(phoneNumber.getLocal())
        .email(posUser.getEmail())
        .updateEmail(posUser.getUpdateEmail())
        .build();

    BusinessInfo businessInfo = posShop.getBusinessInfo();
    UserResponse.Business business = UserResponse.Business.builder()
        .bizNum(businessInfo.getBizNum())
        .bizAddress(businessInfo.getBizAddress())
        .build();

    return new UserResponse(user, business);
  }

  public void updateEmail(String newEmail, String ctmAuth) {

    PosApiResponse<?> posApiResponse = catchtablePosUserClient.updateEmailRequest(newEmail,
        ctmAuth);

    if (posApiResponse.isUnauthorized()) {
      throw new AppException(HttpStatus.UNAUTHORIZED);
    }

    if (posApiResponse.isError()) {
      throw new AppException(posApiResponse.httpStatus(), posApiResponse.getMessage(),
          posApiResponse.getDisplayMessage(), posApiResponse.getReason());
    }
  }

  public void updatePhoneRequest(String phoneNumber, String ctmAuth) {
    PhoneNumber newPhoneNumber = PhoneNumberUtils.ofKr(phoneNumber);

    PosApiResponse<?> posApiResponse = catchtablePosUserClient.updatePhoneNumberRequest(
        newPhoneNumber.getE164(),
        ctmAuth);

    if (posApiResponse.isUnauthorized()) {
      throw new AppException(HttpStatus.UNAUTHORIZED);
    }

    if (posApiResponse.isError()) {
      throw new AppException(posApiResponse.httpStatus(), posApiResponse.getMessage(),
          posApiResponse.getDisplayMessage(), posApiResponse.getReason());
    }
  }

  public void updatePhoneConfirm(String phoneNumber, String certNo, String ctmAuth) {
    PhoneNumber newPhoneNumber = PhoneNumberUtils.ofKr(phoneNumber);

    PosApiResponse<?> posApiResponse = catchtablePosUserClient.updatePhoneNumberConfirm(
        newPhoneNumber.getE164(),
        certNo, ctmAuth);

    if (posApiResponse.isUnauthorized()) {
      throw new AppException(HttpStatus.UNAUTHORIZED);
    }

    if (posApiResponse.isError()) {
      throw new AppException(posApiResponse.httpStatus(), posApiResponse.getMessage(),
          posApiResponse.getDisplayMessage(), posApiResponse.getReason());
    }
  }

  public void updatePassword(String oldPassword, String newPassword, String ctmAuth) {
    PosApiResponse<?> posApiResponse = catchtablePosUserClient.updatePassword(oldPassword,
        newPassword, ctmAuth);

    if (posApiResponse.isUnauthorized()) {
      throw new AppException(HttpStatus.UNAUTHORIZED);
    }

    if (posApiResponse.isError()) {
      throw new AppException(posApiResponse.httpStatus(), posApiResponse.getMessage(),
          posApiResponse.getDisplayMessage(), posApiResponse.getReason());
    }
  }

}
