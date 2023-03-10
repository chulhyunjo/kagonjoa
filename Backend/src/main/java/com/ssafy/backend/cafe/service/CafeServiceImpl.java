package com.ssafy.backend.cafe.service;

import com.ssafy.backend.cafe.domain.dto.*;
import com.ssafy.backend.cafe.domain.entity.Cafe;
import com.ssafy.backend.cafe.domain.entity.CafeCrowd;
import com.ssafy.backend.cafe.domain.entity.CafeLocation;
import com.ssafy.backend.cafe.domain.enums.CrowdLevel;
import com.ssafy.backend.cafe.domain.enums.Direction;
import com.ssafy.backend.cafe.repository.CafeCrowdRepository;
import com.ssafy.backend.cafe.repository.CafeLocationRepository;
import com.ssafy.backend.cafe.repository.CafeRepository;
import com.ssafy.backend.cafe.util.GeometryUtil;
import com.ssafy.backend.common.enums.ExeType;
import com.ssafy.backend.common.exception.cafe.CafeException;
import com.ssafy.backend.common.exception.cafe.CafeExceptionType;
import com.ssafy.backend.member.domain.dto.MemberIdAndNicknameDto;
import com.ssafy.backend.member.domain.entity.MemberCafeTier;
import com.ssafy.backend.member.repository.MemberCafeTierRepository;
import com.ssafy.backend.member.repository.MemberRepository;
import com.ssafy.backend.member.service.MemberService;
import com.ssafy.backend.member.util.MemberUtil;
import com.ssafy.backend.redis.CafeAuth;
import com.ssafy.backend.redis.CafeAuthRepository;
import com.ssafy.backend.todaycafe.domain.entity.CafeVisitLog;
import com.ssafy.backend.todaycafe.domain.entity.Survey;
import com.ssafy.backend.todaycafe.repository.CafeVisitLogRepository;
import com.ssafy.backend.todaycafe.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Service
@RequiredArgsConstructor
@Transactional
public class CafeServiceImpl implements CafeService {

    private final int THREE_HOURS_AGO = 3;
    private final int TWO_MONTH_AGO = 2;
    private final EntityManager em;
    private final MemberService memberService;
    private final CafeAuthRepository cafeAuthRepository;
    private final CafeRepository cafeRepository;
    private final MemberRepository memberRepository;
    private final MemberCafeTierRepository memberCafeTierRepository;
    private final CafeCrowdRepository cafeCrowdRepository;
    private final CafeVisitLogRepository cafeVisitLogRepository;
    private final CafeLocationRepository cafeLocationRepository;
    private final SurveyRepository surveyRepository;
    private final MemberUtil memberUtil;


    @Override
    public CafeSurveyRespDto getCafeSurvey(LocationAndDateDto locationAndDateDto) {

        // location?????? cafe id ????????????
        Optional<CafeLocation> optionalCafeLocation = cafeLocationRepository
                                                                .findByLatAndLng(locationAndDateDto.getLatitude(),
                                                                                locationAndDateDto.getLongitude());
        if (optionalCafeLocation.isEmpty()) {
            throw new CafeException(CafeExceptionType.CAFE_NOT_EXIST);
        }

        long cafeId = optionalCafeLocation.get().getCafe().getId();

        // ?????? ????????? ????????? ?????? ????????? ?????? 2??? ??? ????????? ????????????
        LocalDateTime todayTime = locationAndDateDto.getTodayTime();
        LocalDateTime twoMonthAgo = todayTime.minusMonths(TWO_MONTH_AGO);
        List<Survey> surveys = surveyRepository.findByCafeIdsAndTimeRange(cafeId, twoMonthAgo, todayTime);

        /**
         * ???????????? ?????? ???????????? map?????? ??????
         * "power": [3, 4, 1] <- G,N,B ???
         */
        Map<String, ArrayList<Integer>> gnbCntMap = new HashMap<>();
        gnbCntMap.put("power", new ArrayList<>(Collections.nCopies(3, 0)));
        gnbCntMap.put("wifi", new ArrayList<>(Collections.nCopies(3, 0)));
        gnbCntMap.put("toilet", new ArrayList<>(Collections.nCopies(3, 0)));
        gnbCntMap.put("time", new ArrayList<>(Collections.nCopies(2, 0)));

        // surveys ?????? ????????? ???????????? resp dto ?????? ?????????
        for (Survey survey : surveys) {
            if (survey.getReplyPower().equals("G")) {
                int curVal = gnbCntMap.get("power").get(0);
                gnbCntMap.get("power").set(0, curVal + 1);

            } else if (survey.getReplyPower().equals("N")) {
                int curVal = gnbCntMap.get("power").get(1);
                gnbCntMap.get("power").set(1, curVal + 1);

            } else if (survey.getReplyPower().equals("B")) {
                int curVal = gnbCntMap.get("power").get(2);
                gnbCntMap.get("power").set(2, curVal + 1);
            }

            if (survey.getReplyWifi().equals("G")) {
                int curVal = gnbCntMap.get("wifi").get(0);
                gnbCntMap.get("wifi").set(0, curVal + 1);

            } else if (survey.getReplyWifi().equals("N")) {
                int curVal = gnbCntMap.get("wifi").get(1);
                gnbCntMap.get("wifi").set(1, curVal + 1);

            } else if (survey.getReplyWifi().equals("B")) {
                int curVal = gnbCntMap.get("wifi").get(2);
                gnbCntMap.get("wifi").set(2, curVal + 1);
            }

            if (survey.getReplyToilet().equals("G")) {
                int curVal = gnbCntMap.get("toilet").get(0);
                gnbCntMap.get("toilet").set(0, curVal + 1);

            } else if (survey.getReplyToilet().equals("N")) {
                int curVal = gnbCntMap.get("toilet").get(1);
                gnbCntMap.get("toilet").set(1, curVal + 1);

            } else if (survey.getReplyToilet().equals("B")) {
                int curVal = gnbCntMap.get("toilet").get(2);
                gnbCntMap.get("toilet").set(2, curVal + 1);
            }

            if (survey.isReplyTime()) {
                int curVal = gnbCntMap.get("time").get(0);
                gnbCntMap.get("time").set(0, curVal + 1); // 0??? = ???????????? ?????? ??????
            } else {
                int curVal = gnbCntMap.get("time").get(1);
                gnbCntMap.get("time").set(1, curVal + 1); // 1??? = ???????????? ?????? ??????
            }
        }

        CafeSurveyRespDto cafeSurveyRespDto = new CafeSurveyRespDto();

        cafeSurveyRespDto.setReplyPower_high(gnbCntMap.get("power").get(0));
        cafeSurveyRespDto.setReplyPower_mid(gnbCntMap.get("power").get(1));
        cafeSurveyRespDto.setReplyPower_low(gnbCntMap.get("power").get(2));

        cafeSurveyRespDto.setReplyWifi_high(gnbCntMap.get("wifi").get(0));
        cafeSurveyRespDto.setReplyWifi_mid(gnbCntMap.get("wifi").get(1));
        cafeSurveyRespDto.setReplyWifi_low(gnbCntMap.get("wifi").get(2));

        cafeSurveyRespDto.setReplyToilet_high(gnbCntMap.get("toilet").get(0));
        cafeSurveyRespDto.setReplyToilet_mid(gnbCntMap.get("toilet").get(1));
        cafeSurveyRespDto.setReplyToilet_low(gnbCntMap.get("toilet").get(2));

        if (gnbCntMap.get("time").get(0) >= gnbCntMap.get("time").get(1)) {
            // ???????????? ?????? ??????
            cafeSurveyRespDto.setReplyTime(true);
        } else {
            // ???????????? ?????? ??????
            cafeSurveyRespDto.setReplyTime(false);
        }

        return cafeSurveyRespDto;
    }

    @Override
    public int attendanceReward(int todayDate) {

        // ?????? ?????? 20230213??? ???????????? todayCafeLog??? ??????????
        List<CafeVisitLog> todayVisLog = cafeVisitLogRepository.findByVisitedAt(todayDate);

        if (todayVisLog.isEmpty()) {
            // ?????? ?????? ??????????????? ????????? ??????
            return 10;
        }

        return 0;
    }

    @Override
    public InitCafeAuthRespDto initCheckCafeAuth() {
        // @CafeAuth??? ??????????????? ??? ??????
        String nickname = memberService.getMemberIdAndNicknameByJwtToken().getNickname();
        Optional<CafeAuth> cafeAuthOptional = cafeAuthRepository.findById(nickname); // key = nickname
        CafeAuth cafeAuth = cafeAuthOptional.orElseThrow(() -> new CafeException(CafeExceptionType.CAFE_AUTH_EXPIRED));

        long cafeId = cafeAuth.getCafeId();

        // cafeId??? ????????? ????????????
        Optional<CafeLocation> optionalCafeLocation = cafeLocationRepository.findByCafeId(cafeId);
        CafeLocation cafeLocation
                = optionalCafeLocation.orElseThrow(() -> new CafeException(CafeExceptionType.CAFE_NOT_EXIST));

        String cafeName = cafeLocation.getCafe().getName();


        InitCafeAuthRespDto respDto = InitCafeAuthRespDto.builder()
                .cafeName(cafeName)
                .latitude(cafeLocation.getLat())
                .longitude(cafeLocation.getLng())
                .build();

        return respDto;
    }


    @Override
    public void saveCrowdLevel(CrowdCheckReqDto crowdCheckReqDto) {
        // ????????? ????????????
        MemberIdAndNicknameDto memberIdAndNick = memberService.getMemberIdAndNicknameByJwtToken();
        long memberId = memberIdAndNick.getId();
        String nickname = memberIdAndNick.getNickname();

        // ????????? ?????? - ???????????? ?????? ??? ?????? id ????????????
        Optional<CafeAuth> cafeAuthOptional = cafeAuthRepository.findById(nickname); // key = nickname
        if (cafeAuthOptional.isEmpty()) {
            throw new CafeException(CafeExceptionType.CAFE_AUTH_EXPIRED);
        }

        long cafeId = cafeAuthOptional.get().getCafeId();

        // ????????? ?????? ?????? ????????????
        Optional<CafeVisitLog> optionalCafeVisitLog = cafeVisitLogRepository
                .findByVisitedAtAndMemberIdAndCafeId(crowdCheckReqDto.getTodayDate(), memberId, cafeId);

        if (optionalCafeVisitLog.isEmpty()) {
            throw new CafeException(CafeExceptionType.CAFE_AUTH_MISMATCH);
        }

        if (optionalCafeVisitLog.get().isCrowdSurvey()) {
            throw new CafeException(CafeExceptionType.ALREADY_SUBMIT_CROWD_SURVEY);
        }

        optionalCafeVisitLog.get().setCrowdSurvey(true);

        // ????????? ?????? ??????
        Optional<Cafe> optionalCafe = cafeRepository.findById(cafeId);
        if (optionalCafe.isEmpty()) {
            throw new CafeException(CafeExceptionType.CAFE_NOT_EXIST);
        }

        CafeCrowd cafeCrowd = CafeCrowd.cafeCrowdSaveBuilder()
                .cafe(optionalCafe.get())
                .crowdValue(crowdCheckReqDto.getCrowdLevel())
                .build();
        cafeCrowdRepository.save(cafeCrowd);
    }

    @Override
    public boolean checkCrowdSurvey(int todayDate) {
        // ????????? ????????????
        MemberIdAndNicknameDto memberIdAndNick = memberService.getMemberIdAndNicknameByJwtToken();
        long memberId = memberIdAndNick.getId();
        String nickname = memberIdAndNick.getNickname();

        // ????????? ?????? - ???????????? ?????? ??? ?????? id ????????????
        Optional<CafeAuth> cafeAuthOptional = cafeAuthRepository.findById(nickname); // key = nickname
        if (cafeAuthOptional.isEmpty()) {
            throw new CafeException(CafeExceptionType.CAFE_AUTH_EXPIRED);
        }

        long cafeId = cafeAuthOptional.get().getCafeId();

        // ????????? ?????? ?????? ????????????
        Optional<CafeVisitLog> optionalCafeVisitLog = cafeVisitLogRepository
                .findByVisitedAtAndMemberIdAndCafeId(todayDate, memberId, cafeId);

        if (optionalCafeVisitLog.isEmpty()) {
            throw new CafeException(CafeExceptionType.CAFE_AUTH_MISMATCH);
        }

        return optionalCafeVisitLog.get().isCrowdSurvey();
    }


    @Override
    public List<NearByCafeWithCrowdResultDto> addCrowdInfoToNearByCafes(List<NearByCafeResultDto> nearByCafeLocations,
                                                                        CurTimeReqDto curTimeReqDto) {

        // ????????? ????????? ??????
        List<NearByCafeWithCrowdResultDto> nearByCafeWithCrowdResultDtos = new ArrayList<>();

        // ?????? ?????? ???????????? ?????????
        List<Long> cafeIdList = new ArrayList<>();

        for (NearByCafeResultDto nearByCafeLocation : nearByCafeLocations) {
            cafeIdList.add(nearByCafeLocation.getId().longValue());
        }

        LocalDateTime todayTime = curTimeReqDto.getTodayTime();
        LocalDateTime threeHoursAgo = todayTime.minusHours(THREE_HOURS_AGO);

        List<CafeCrowd> cafeCrowds = cafeCrowdRepository.fineByCafeIds(cafeIdList, threeHoursAgo, todayTime);

        ArrayList<Long> curCafeIds = new ArrayList<>();

        for (CafeCrowd cafeCrowd : cafeCrowds) {
            curCafeIds.add(cafeCrowd.getCafe().getId());
        }

        List<Long> distinctCurCafeIds = curCafeIds.stream().distinct().collect(Collectors.toList());

        // [ {cafeID: [(time, value), (time, value), ...]}, {}, ... {}]
        Map<Long, ArrayList<TimeCrowdDto>> cafeCrowdMap = new HashMap();

        // map ?????????
        for (Long distinctCurCafeId : distinctCurCafeIds) {
            cafeCrowdMap.put(distinctCurCafeId, new ArrayList<TimeCrowdDto>());
        }

        // map??? ??? ?????????
        for (CafeCrowd cafeCrowd : cafeCrowds) {
            long curCafeId = cafeCrowd.getCafe().getId();
            cafeCrowdMap.get(curCafeId).add(new TimeCrowdDto(cafeCrowd.getCreatedAt(), cafeCrowd.getCrowdValue()));
        }

        // {cafeId : "L"}, {cafeId2 : "M"}, {cafeId3 : "H"} ...
        Map<Long, CrowdLevel> cafeIdsWithCrowdMap = calcCrowdLevel(cafeCrowdMap, todayTime);

        for (NearByCafeResultDto nearByCafeLocation : nearByCafeLocations) {

            NearByCafeWithCrowdResultDto nearByCafeWithCrowdResultDto = new NearByCafeWithCrowdResultDto();
            nearByCafeWithCrowdResultDto.setCrowdLevel(cafeIdsWithCrowdMap.get(nearByCafeLocation.getId().longValue()));

            // ????????? ?????? dto??? ?????? ?????? dto ??????
            BeanUtils.copyProperties(nearByCafeLocation, nearByCafeWithCrowdResultDto);
            nearByCafeWithCrowdResultDtos.add(nearByCafeWithCrowdResultDto);
        }
        return nearByCafeWithCrowdResultDtos;
    }

    /**
     *  ???????????? ????????? ????????? -> ??????????????? ?????? ?????????????????? ????????????
     *  ~ 10??? - * 2
     *  ~ 30??? - * 1
     *  ~ 2?????? - * 0.7
     *  ~ 3?????? - * 0.5
     */
    private Map<Long, CrowdLevel> calcCrowdLevel(Map<Long, ArrayList<TimeCrowdDto>> cafeCrowdMap,
                                                 LocalDateTime todayTime) {

        // ????????? ???????????? ?????? ??????????????? ??????
        // ???????????? ??????????????? ?????? ????????? ??????????????? ????????? ?????????. ?????? ??????????????? ??? ?????? ??????????????? yml??? ??????
        final ExeType EXE_TYPE = ExeType.DEMO;

        int MINUTE_LEVEL1;
        int MINUTE_LEVEL2;
        int MINUTE_LEVEL3;
        int MINUTE_LEVEL4;

        if (EXE_TYPE == ExeType.DEMO) {
            MINUTE_LEVEL1 = 1440;
            MINUTE_LEVEL2 = 2880;
            MINUTE_LEVEL3 = 10080;
            MINUTE_LEVEL4 = 14400;
        } else {
            MINUTE_LEVEL1 = 10;
            MINUTE_LEVEL2 = 30;
            MINUTE_LEVEL3 = 120;
            MINUTE_LEVEL4 = 180;
        }

        // {cafeId : "L"}, {cafeId2 : "M"}, {cafeId3 : "H"} ...
        Map<Long, CrowdLevel> results = new HashMap<>();

        for (Long cafeId : cafeCrowdMap.keySet()) {
            ArrayList<TimeCrowdDto> timeCrowdDtos = cafeCrowdMap.get(cafeId);
            int crowdListSize = timeCrowdDtos.size();
            double sumCrowdVal = 0.0;


            for (TimeCrowdDto timeCrowdDto : timeCrowdDtos) {
                int crowdValue = timeCrowdDto.getVal();

                Duration duration = Duration.between(timeCrowdDto.getTime(), todayTime);
                int minutes = (int) duration.toMinutes();

                if (minutes <= MINUTE_LEVEL1) {
                    sumCrowdVal += crowdValue * 2; // ~ 10??? 2???
                } else if (minutes <= MINUTE_LEVEL2) {
                    sumCrowdVal += crowdValue; // ~ 30??? 1???
                } else if (minutes <= MINUTE_LEVEL3) {
                    sumCrowdVal += crowdValue * 0.6; // ~ 120??? 0.7???
                } else {
                    sumCrowdVal += crowdValue * 0.3; // ~ 180??? 0.5???
                }
            }

            double meanVal = sumCrowdVal / crowdListSize;

            // ?????????????????? ????????? ??????
            double roundedMeanVal = (double) Math.round(meanVal * 10) / 10;

            if (roundedMeanVal <= 2) {
                results.put(cafeId, CrowdLevel.L);
            } else if (roundedMeanVal <= 4) {
                results.put(cafeId, CrowdLevel.M);
            } else {
                results.put(cafeId, CrowdLevel.H);
            }
        }
        return results;
    }

    @Override
    public void checkCafeAuth() {
        // ????????? ???????????? ????????? ????????? ?????? ??? ????????? (time out ?????? ?????????)
        String nickname = memberService.getMemberIdAndNicknameByJwtToken().getNickname();
        Optional<CafeAuth> cafeAuthOptional = cafeAuthRepository.findById(nickname); // key = nickname

        // ???????????? ??????????????? ????????? ??????
        CafeAuth cafeAuth = cafeAuthOptional.orElseThrow(() -> new CafeException(CafeExceptionType.CAFE_AUTH_EXPIRED));

        // ??????????????? ????????? ??????
        long cafeId = cafeAuth.getCafeId();
        cafeAuthRepository.deleteById(nickname);

        CafeAuth newCafeAuth = CafeAuth.builder()
                .cafeId(cafeId)
                .nickname(nickname)
                .expiration(600) // 600???
                .build();

        cafeAuthRepository.save(newCafeAuth);
    }


    @Override
    public void saveCafeAuth(SelectCafeRequestDto selectCafeRequestDto) {
        // ??? ????????? cafeId??? ????????? ??????????????? ?????? ??????
        List<NearByCafeResultDto> nearByCafeLocations
                = this.getNearByCafeLocations(new ClientPosInfoDto(selectCafeRequestDto.getLatitude(),
                selectCafeRequestDto.getLongitude(), 1.0)); // ????????????

        ArrayList<Long> cafeIdLstForValid = new ArrayList<>();
        for (NearByCafeResultDto nearByCafeLocation : nearByCafeLocations) {
            cafeIdLstForValid.add(nearByCafeLocation.getId().longValue()); // ????????? cafe id ??????
        }

        if (!cafeIdLstForValid.contains(selectCafeRequestDto.getCafeId())) {
            throw new CafeException(CafeExceptionType.CAFE_AUTH_MISMATCH);
        }

        // ??? ????????? cafe ID??? ????????? ???????????????
        String nickname = memberService.getMemberIdAndNicknameByJwtToken().getNickname();
        CafeAuth cafeAuth = CafeAuth.builder()
                                .cafeId(selectCafeRequestDto.getCafeId())
                                .nickname(nickname)
                                .expiration(600) // ???
                                .build();

        cafeAuthRepository.save(cafeAuth);

        Optional<CafeAuth> cafeAuthOptional = cafeAuthRepository.findById(nickname); // key = nickname

        if (cafeAuthOptional.isEmpty()) {
            throw new CafeException(CafeExceptionType.CAFE_AUTH_SAVE_FAIL);
        }
    }


    @Transactional(readOnly = true)
    public List<NearByCafeResultDto> getNearByCafeLocations(ClientPosInfoDto clientPosInfoDto) {
        Double latitude = clientPosInfoDto.getLatitude();
        Double longitude = clientPosInfoDto.getLongitude();
        Double distance = clientPosInfoDto.getDist();

        LocationDto northEast = GeometryUtil
                .calculate(latitude, longitude, distance, Direction.NORTHEAST.getBearing());
        LocationDto southWest = GeometryUtil
                .calculate(latitude, longitude, distance, Direction.SOUTHWEST.getBearing());

        double x1 = northEast.getLatitude();
        double y1 = northEast.getLongitude();
        double x2 = southWest.getLatitude();
        double y2 = southWest.getLongitude();

        String pointFormat = String.format("'LINESTRING(%f %f, %f %f)')", x1, y1, x2, y2);

        Query query
                = em.createNativeQuery(
            "SELECT cf.id, cf.name, cl.address, cl.lat, cl.lng, cf.brand_type "
                    + "FROM (SELECT * "
                            + "FROM cafe_location AS c "
                            + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", c.point) = 1) AS cl "
                    + "INNER JOIN cafe cf ON cf.id = cl.cafe_id");

        List<Object[]> results = query.getResultList();

        List<NearByCafeResultDto> nearByCafeResultDtos = new ArrayList<>();

        for (Object[] result : results) {
            NearByCafeResultDto dto = NearByCafeResultDto.builder()
                    .id((BigInteger) result[0])
                    .name((String) result[1])
                    .address((String) result[2])
                    .latitude((BigDecimal)result[3])
                    .longitude((BigDecimal)result[4])
                    .brand_type((String) result[5]).build();
            nearByCafeResultDtos.add(dto);
        }

        return nearByCafeResultDtos;
    }

    @Override
    public void saveTier() {
        String nickname = memberUtil.checkMember().getNickname();
        Long memberId = memberUtil.checkMember().getMemberId();
        CafeAuth cafeAuth = cafeAuthRepository.findById(nickname).get();
        Long cafeId = cafeAuth.getCafeId();
        Optional<MemberCafeTier> optionalMemberCafeTier = memberCafeTierRepository.findByMemberIdAndCafeId(memberId, cafeId);
        
        // ????????? - ?????????
        MemberCafeTier memberCafeTier;
        if(optionalMemberCafeTier.isEmpty() || optionalMemberCafeTier == null) {
            memberCafeTier  = MemberCafeTier.TierBuilder()
                    .cafe(cafeRepository.findById(cafeId).get())
                    .member(memberRepository.findById(memberId).get())
                    .exp(100L)
                    .build();
            memberCafeTierRepository.saveAndFlush(memberCafeTier);
        }
    }
}
