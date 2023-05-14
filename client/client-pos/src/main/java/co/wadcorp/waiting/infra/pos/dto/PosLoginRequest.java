package co.wadcorp.waiting.infra.pos.dto;

public record PosLoginRequest(String serviceType, String userId, String userPw) {

}
