package co.wadcorp.waiting.api.controller.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse {

  private final User user;
  private final Business business;

  public UserResponse(User user, Business business) {
    this.user = user;
    this.business = business;
  }

  @Getter
  public static class User {
    private String phone;
    private String email;
    private String updateEmail;

    @Builder
    public User(String phone, String email, String updateEmail) {
      this.phone = phone;
      this.email = email;
      this.updateEmail = updateEmail;
    }
  }

  @Getter
  public static class Business {
    private String bizNum;
    private String bizAddress;

    @Builder
    public Business(String bizNum, String bizAddress) {
      this.bizNum = bizNum;
      this.bizAddress = bizAddress;
    }
  }
}
