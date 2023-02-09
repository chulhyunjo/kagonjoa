package com.ssafy.backend.member.service;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssafy.backend.common.exception.jwt.JwtException;
import com.ssafy.backend.common.exception.jwt.JwtExceptionType;
import com.ssafy.backend.common.exception.member.MemberException;
import com.ssafy.backend.common.exception.member.MemberExceptionType;
import com.ssafy.backend.jwt.JwtUtil;
import com.ssafy.backend.jwt.dto.TokenRespDto;
import com.ssafy.backend.member.domain.dto.MemberIdAndNicknameDto;
import com.ssafy.backend.member.domain.entity.MemberCoin;
import com.ssafy.backend.member.repository.MemberCoinRepository;
import com.ssafy.backend.redis.CafeAuthRepository;
import com.ssafy.backend.redis.RefreshTokenRepository;
import com.ssafy.backend.member.domain.entity.Member;
import com.ssafy.backend.member.domain.enums.OauthType;
import com.ssafy.backend.member.repository.MemberRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final MemberCoinRepository memberCoinRepository;
    private final CafeAuthRepository cafeAuthRepository;

    @Override
    public void checkDuplicatedNickname(String nickName) {
        // 닉네임 조회 후 존재하면 에러 발생 시킴
        memberRepository.findByNickname(nickName).ifPresent(x -> {
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_NICKNAME);
        });
    }

    @Override
    public String changeNickname(Member member, String newNickname) {

        // 닉네임 유효성 체크 = 받는 dto에서!

        if (member.getNickname().equals(newNickname)) {
            throw new MemberException(MemberExceptionType.SAME_NICKNAME);
        }

        member.setNickname(newNickname);

        return newNickname;
    }

    @Override
    public Optional<Member> getMember(long kakaoMemberId, OauthType kakao) {
        return memberRepository.findByOauthIdAndOauthType(kakaoMemberId, OauthType.KAKAO);
    }

    @Override
    public void saveMember(long oAuthId, String nickname, OauthType oauthType) {
        Member member = Member.oAuthBuilder()
                .nickname(nickname)
                .oAuthId(oAuthId)
                .oAuthType(oauthType)
                .build();
        Member savedMember = memberRepository.save(member);
        System.out.println("MemberServiceImpl : 멤버 저장 완료");

        MemberCoin memberCoin = MemberCoin.coinBuilder()
                .member(savedMember)
                .coffeeBeanCount(0)
                .coffeeCount(0)
                .build();
        memberCoinRepository.save(memberCoin);
        System.out.println("MemberServiceImpl : MemberCoin 저장완료");
        member.updateMemberCoin(memberCoin);
    }

    @Override
    public TokenRespDto tokenRefresh() {
        // refresh token 받아오기
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String refreshToken = request.getHeader("Authorization");

        // refresh token 인증
        jwtUtil.isValidForm(refreshToken);
        refreshToken = refreshToken.substring(7);
        jwtUtil.isValidToken(refreshToken, "RefreshToken");

        // refresh token 에서 유저 aud값 가져오기
        DecodedJWT payload = jwtUtil.getDecodedJWT(refreshToken);
        long memberId = Long.parseLong(payload.getAudience().get(0));

        // redis에 refresh 토큰이 없다면(리프레쉬 토큰 만료시)
        if (refreshTokenRepository.findById(refreshToken).isEmpty()) {
            throw new JwtException(JwtExceptionType.TOKEN_EXPIRED);
        }

        TokenRespDto tokenRespDto = new TokenRespDto();
        // 리프레쉬 토큰이 redis에 존재 하는 상황
        refreshTokenRepository.findById(refreshToken).ifPresent(a -> {
            System.out.println("억세스 발급");
            Optional<Member> dbMemberOpt = memberRepository.findById(memberId);
            if (dbMemberOpt.isEmpty()) {
                throw new MemberException(MemberExceptionType.NOT_FOUND_MEMBER);
            }
            Member dbMember = dbMemberOpt.get();
            String accessToken = jwtUtil.getAccessToken(dbMember);

            tokenRespDto.setAccessToken(accessToken);
        });
        return tokenRespDto;
    }

    @Override
    public void logout(String nickname) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String refreshToken = request.getHeader("Authorization");

//        // refresh token 인증
//        jwtUtil.isValidForm(refreshToken);
//        refreshToken = refreshToken.substring(7);
//        jwtUtil.isValidToken(refreshToken, "RefreshToken");

        // redis에 저장된 refresh토큰 삭제하기
        refreshTokenRepository.deleteById(refreshToken);

        refreshTokenRepository.findById(refreshToken).ifPresent(a -> {
            throw new MemberException(MemberExceptionType.NOT_DELETE_REFRESH_TOKEN);
        });

        // 사용자 위치 인증 정보 삭제하기
        cafeAuthRepository.deleteById(nickname);
    }

    @Override
    public MemberIdAndNicknameDto getMemberIdAndNicknameByJwtToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String accessToken = request.getHeader("Authorization");
        accessToken = accessToken.substring(7);
        DecodedJWT payload = jwtUtil.getDecodedJWT(accessToken);
        long memberId = Long.parseLong(payload.getAudience().get(0));
        String nickname = String.valueOf(payload.getClaim("nickname"));
        return new MemberIdAndNicknameDto(memberId, nickname);
    }

    @Override
    public void deleteMember() {
        long memberId = getMemberIdAndNicknameByJwtToken().getId();
        memberRepository.deleteById(memberId);
        memberRepository.findById(memberId).ifPresent(a -> {
            throw new MemberException(MemberExceptionType.NOT_DELETE_MEMBER);
        });
    }

}
