package com.ssafy.backend.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.annotation.PostConstruct;

import com.ssafy.backend.common.exception.jwt.JwtException;
import com.ssafy.backend.common.exception.jwt.JwtExceptionType;
import com.ssafy.backend.member.domain.entity.Member;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secret;
    private Algorithm key;
    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInMinutes;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInMinutes;

    static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    static final String NICKNAME_CLAIM = "nickname";
    static final String BEARER = "Bearer ";

    @PostConstruct
    public void setKey() {
        key = Algorithm.HMAC256(secret);
    }

    // 현재 accessToken : 10분/ refreshToken : 1시간 => 배포시 늘려야됨
    public String getAccessToken(Member member) {
        return JWT.create()
                .withSubject("AccessToken")
                .withAudience(member.getId().toString())
                .withClaim(NICKNAME_CLAIM, member.getNickname())
                .withExpiresAt(Date.from(LocalDateTime.now()
                        .plusMinutes(accessTokenValidityInMinutes)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .sign(key);
    }

    public String getRefreshToken(Member member) {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withAudience(member.getId().toString())
                .withExpiresAt(Date.from(LocalDateTime.now()
                        .plusMinutes(refreshTokenValidityInMinutes)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .sign(key);
    }

    public void isValidForm(String token){
        // 토큰이 들어왔는가?
        if (token == null) {
            throw new JwtException(JwtExceptionType.TOKEN_NULL);
        }

        // 토큰이 "Bearer "로 시작하는가?
        if (!token.startsWith(BEARER)) {
            throw new JwtException(JwtExceptionType.NOT_START_WITH_BEARER);
        }

        // 토큰이 "Bearer " 이후로 존재하는가?
        if (token.length() < 8) {
            throw new JwtException(JwtExceptionType.TOKEN_TOO_SHORT);
        }
    }

    // 1. 토큰 타입이 올바른가?
    // 2. 토큰 서명이 일치하는가?
    // 3. 토큰 발행 대상자가 존재하는가?
    public void isValidToken(String token, String tokenType) {
        try {
            DecodedJWT decodedJWT = JWT.require(key)
                    .withSubject(tokenType)
                    .build()
                    .verify(token);

            if (decodedJWT.getAudience().isEmpty()) {
                // 여기서 터지면 밑의 catch 블록에서 받는 구조
                throw new JWTVerificationException("NotValidToken");
            }
        } catch (TokenExpiredException e) {
            // 토큰 만료시 -> 401 + 클라이언트에서 억세스 토큰 재발급
            throw new JwtException(JwtExceptionType.TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            // 다른 경우는 모두 인증 실패
            throw new JwtException(JwtExceptionType.JWT_VERIFICATION_EXCEPTION);
        }
    }


    // 검증 없이 토큰 디코딩
    public DecodedJWT getDecodedJWT(String token) {
        try {
            return JWT.decode(token);
        } catch (JWTDecodeException e) {
            throw new JwtException(JwtExceptionType.DECODE_FAIL);
        }
    }
}

