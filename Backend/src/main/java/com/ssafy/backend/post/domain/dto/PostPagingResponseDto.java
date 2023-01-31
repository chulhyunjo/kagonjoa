package com.ssafy.backend.post.domain.dto;

import com.ssafy.backend.member.domain.entity.Member;
import com.ssafy.backend.post.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostPagingResponseDto {

    // 리턴객체 : 게시물 PK, 유저정보, 만든시간, 이미지, 글내용 전체, 좋아요 개수, 댓글 개수
    private Long id;
    private Member member;
    private LocalDateTime createdAt;

    // private Image image;
    private String Content;
    private int likeCounts;
    private int commentCounts;


    // 댓글리스트






}
