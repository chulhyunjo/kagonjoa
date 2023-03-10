package com.ssafy.backend.common.exception.jwt;

import com.ssafy.backend.common.exception.BaseExceptionType;
import com.ssafy.backend.common.exception.enums.SignType;
import org.springframework.http.HttpStatus;

public enum JwtExceptionType implements BaseExceptionType {

    TOKEN_NULL(HttpStatus.BAD_REQUEST, "토큰 정보가 없습니다.", SignType.JWT),
    NOT_START_WITH_BEARER(HttpStatus.BAD_REQUEST, "토큰이 'Bearer '로 시작하지 않습니다.", SignType.JWT),
    TOKEN_TOO_SHORT(HttpStatus.BAD_REQUEST, "토큰의 길이가 너무 짧습니다.", SignType.JWT),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.", SignType.JWT),
    JWT_VERIFICATION_EXCEPTION(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다.", SignType.JWT),
    DECODE_FAIL(HttpStatus.BAD_REQUEST, "토큰 해독 실패, 비정상적인 토큰입니다.", SignType.JWT),
    TOKEN_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 저장 실패", SignType.JWT);

    private final HttpStatus httpStatus;
    private final String errorMessage;
    private final SignType sign;

    JwtExceptionType(HttpStatus httpStatus, String errorMessage, SignType sign) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
        this.sign = sign;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public SignType getDataSign() {
        return this.sign;
    }
}
