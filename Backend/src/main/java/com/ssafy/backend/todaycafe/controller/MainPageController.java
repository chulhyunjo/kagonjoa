package com.ssafy.backend.todaycafe.controller;

import com.ssafy.backend.common.annotation.Auth;
import com.ssafy.backend.common.annotation.CafeAuth;
import com.ssafy.backend.common.dto.ResponseDTO;
import com.ssafy.backend.common.exception.todaycafe.TodayCafeException;
import com.ssafy.backend.common.exception.todaycafe.TodayCafeExceptionType;
import com.ssafy.backend.todaycafe.domain.dto.*;
import com.ssafy.backend.todaycafe.service.TodayCafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("todaycafe/main")
@RequiredArgsConstructor
public class MainPageController {

    private final TodayCafeService todayCafeService;


    @Auth
    @CafeAuth
    @PostMapping("/todo")
    public ResponseEntity<ResponseDTO> writeTodo(@RequestBody TodoReqeustDto todoReqeustDto) {
        TodoResponseDto todoResponseDto = todayCafeService.todoEvent(todoReqeustDto);
        ResponseDTO responseDTO = new ResponseDTO("", "", HttpStatus.OK, todoResponseDto);
        switch (todoResponseDto.getResponseType()) {
            case 1:
                responseDTO.setMsg("[responseType 1] 글생성 완료!"); // todo 1객체
                break;
            case 2:
                responseDTO.setMsg("[responseType 2] 글 업데이트 완료"); // todo 1객체
                break;
            case 3:
                responseDTO.setMsg("[responseType 3] 체크박스 토글 완료"); // todo 1객체
                break;
            case 4:
                responseDTO.setMsg("[responseType 4] 글 삭제 완료");
                break;
        }
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @Auth
    @GetMapping("/todo/feed")
    public ResponseEntity<ResponseDTO> findTodo(@RequestParam int visitedAt) {


        List<TodoResponseDto> todoList = todayCafeService.findTodo(visitedAt);

        ResponseDTO responseDTO = new ResponseDTO("Todo List 불러오기 완료!", "", HttpStatus.OK, todoList);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @Auth
    @CafeAuth
    @PutMapping("/addtime")
    public ResponseEntity<ResponseDTO> addTimeBar(@RequestParam int type) {
        if(type != 1762320904) throw new TodayCafeException(TodayCafeExceptionType.BAD_TYPE_REQUEST);
        int currentTimeBar = todayCafeService.addTimeBar();
        ResponseDTO responseDTO = new ResponseDTO("시간바 추가 완료!", "", HttpStatus.OK, currentTimeBar);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @Auth
    @CafeAuth
    @PostMapping("/survey")
    public ResponseEntity<ResponseDTO> submitSurvey(@RequestBody SurveyRequestDto surveyRequestDto) {
        todayCafeService.saveSurvey(surveyRequestDto);

        ResponseDTO responseDTO = new ResponseDTO("조사결과 저장완료", "", HttpStatus.OK, null);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    /**
     * 입실 일자를 받아서 만족도 설문 여부 제공 -> req: ?todayTime: 20231110 / (data, 200 ok)isVisited: true false
     * @param todayDate
     * @return
     */
    @Auth
    @CafeAuth
    @GetMapping("/survey/check")
    public ResponseEntity<ResponseDTO> checkSubmitSurvey(@RequestParam int todayDate) {
        Boolean isSubmit = todayCafeService.checkSubmitSurvey(todayDate);
        CheckSurveyRespDto checkSurveyRespDto = new CheckSurveyRespDto(isSubmit);
        ResponseDTO responseDTO
                = new ResponseDTO("만족도 설문 조사 제출 여부 확인!", "", HttpStatus.OK, checkSurveyRespDto);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    @Auth
    @CafeAuth
    @GetMapping("/acctime")
    public ResponseEntity<ResponseDTO> getAccTime(@RequestParam int todayDate) {
        int accTime = todayCafeService.getAccTime(todayDate);
        ResponseDTO responseDTO = new ResponseDTO("현재 카페 누적시간 제공 완료", "", HttpStatus.OK, accTime);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
