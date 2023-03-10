package com.ssafy.backend.member.service;

import com.ssafy.backend.jwt.dto.TokenRespDto;
import com.ssafy.backend.member.domain.dto.MemberCoinRespDto;
import com.ssafy.backend.member.domain.dto.MemberIdAndNicknameDto;
import com.ssafy.backend.member.domain.dto.SuperMemberCafeAuthReqDto;
import com.ssafy.backend.member.domain.entity.Member;
import com.ssafy.backend.member.domain.enums.OauthType;

import java.util.Optional;

public interface MemberService {
    void checkDuplicatedNickname(String nickName);

    String changeNickname(Member member, String newNickname);

    // 메소드 오버로딩으로 구현할것
    Optional<Member> getMember(long kakaoMemberId, OauthType kakao);

    void saveMember(long oAuthId, String nickname, OauthType oauthType);

    TokenRespDto tokenRefresh();

    void logout();

    MemberIdAndNicknameDto getMemberIdAndNicknameByJwtToken();

    void deleteMember();

    void addMemberCoin(int addCoinVal);

    MemberCoinRespDto getMemberCoin();

    void setHyncholAuth(SuperMemberCafeAuthReqDto locationDto);
}
