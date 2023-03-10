package com.ssafy.backend.mypage.domain.dto;

import com.ssafy.backend.cafe.domain.dto.CafeNameAndBrandDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitCafeListResponseDto {

    private Long cafeId; // 카페 PK
    private String cafeName; // 카페명
    private String brandType; // 카페 브랜드 타입
    private Long exp; // 경험치
    private Double latitude; // 위도
    private Double longitude; // 경도
    private String address;

    public void addLocation (Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
