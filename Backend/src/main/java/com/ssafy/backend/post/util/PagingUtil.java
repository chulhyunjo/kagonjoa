package com.ssafy.backend.post.util;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.backend.cafe.domain.dto.ClientPosInfoDto;
import com.ssafy.backend.cafe.domain.dto.NearByCafeResultDto;
import com.ssafy.backend.cafe.domain.entity.Cafe;
import com.ssafy.backend.cafe.repository.CafeRepository;
import com.ssafy.backend.cafe.service.CafeServiceImpl;
import com.ssafy.backend.common.exception.jwt.JwtException;
import com.ssafy.backend.common.exception.post.PostException;
import com.ssafy.backend.common.exception.post.PostExceptionType;
import com.ssafy.backend.member.domain.dto.MemberIdAndNicknameDto;
import com.ssafy.backend.member.domain.entity.MemberCafeTier;
import com.ssafy.backend.member.repository.MemberCafeTierRepository;
import com.ssafy.backend.member.service.MemberServiceImpl;
import com.ssafy.backend.post.domain.dto.CheckedResponseDto;
import com.ssafy.backend.post.domain.dto.CommentPagingResponseDto;
import com.ssafy.backend.post.domain.dto.RepliesPagingResponseDto;
import com.ssafy.backend.post.domain.entity.Comment;
import com.ssafy.backend.post.domain.entity.Post;
import com.ssafy.backend.post.domain.entity.PostImage;
import com.ssafy.backend.post.domain.enums.PostType;
import com.ssafy.backend.post.repository.CommentRepository;
import com.ssafy.backend.post.repository.PostImageRepository;
import com.ssafy.backend.post.repository.PostRepository;
import com.ssafy.backend.redis.CafeAuth;
import com.ssafy.backend.redis.CafeAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.ssafy.backend.common.exception.jwt.JwtExceptionType.JWT_VERIFICATION_EXCEPTION;

@RequiredArgsConstructor // 얘도 커스텀?
@Transactional(readOnly = true)
@Service
public class PagingUtil {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CafeAuthRepository cafeAuthRepository;
    private final CafeRepository cafeRepository;
    private final MemberCafeTierRepository memberCafeTierRepository;


    /**
     * 0. 유저 확인
     **/
    public Slice<Post> getPostFeeds(List<Long> cafeIdList, Long postId, List<PostType> types, Pageable pageable) {
        Slice<Post> postSlice;
        if (types.contains(PostType.hot)) { // 핫 게시물을 포함하고 있을 때

            if (postId == -1L) {
                // 처음 요청할때 (refresh)
                System.out.println("hot 첫번째요청");
                postSlice = postRepository.findHotPostNext(cafeIdList, Long.MAX_VALUE, pageable);
            } else {
                // 두번째 이상으로 요청할 때 (마지막 글의 pk 를 기준으로 함)
                System.out.println("hot 두번째이상 요청");
                postSlice = postRepository.findHotPostNext(cafeIdList, postId, pageable);
                // 갖고올 게시물이 없으면
            }

        } else { //

            if (postId == -1L) {
                // 처음 요청할때 (refresh)
                System.out.println("피드 첫번째 요청");
                postSlice = postRepository.findNextFeed(Long.MAX_VALUE, types, cafeIdList, pageable);
            } else {
                // 두번째 이상으로 요청할 때 (마지막 글의 pk 를 기준으로 함)
                System.out.println("두번째이상 요청");
                postSlice = postRepository.findNextFeed(postId, types, cafeIdList, pageable);
                // 갖고올 게시물이 없으면
            }
        }
        return postSlice;
    }


    public Slice<Post> getSearchedFeeds(List<Long> cafeIdList, Long postId, String searchKeyword, int searchType, Pageable pageable) {
        Slice<Post> postSlice;
        if (searchType == 1) { // 글 내용으로 찾기

            if (postId == -1L) {
                // 처음 요청할때 (refresh)
                System.out.println("글내용으로 찾기 첫번째 요청");
                postSlice = postRepository.findBySearchContentFirst(cafeIdList, searchKeyword, pageable);

            } else {
                // 두번째 이상으로 요청할 때 (마지막 글의 pk 를 기준으로 함)
                System.out.println("글내용으로 찾기 다음 요청");
                postSlice = postRepository.findBySearchContentNext(cafeIdList, searchKeyword, postId, pageable);
                // 갖고올 게시물이 없으면
            }

        } else if (searchType == 2) { // 유저 이름으로 찾기

            if (postId == -1L) {
                // 처음 요청할때 (refresh)
                System.out.println("유저 이름으로 찾기 첫번째 요청");
                postSlice = postRepository.findBySearchNicknameFirst(cafeIdList, searchKeyword, pageable);
            } else {
                // 두번째 이상으로 요청할 때 (마지막 글의 pk 를 기준으로 함)
                System.out.println("유저 이름으로 찾기 다음 요청");
                postSlice = postRepository.findBySearchNicknameNext(cafeIdList, searchKeyword, postId, pageable);
                // 갖고올 게시물이 없으면
            }

        } else {
            throw new PostException(PostExceptionType.NOT_ALLOWED_TYPE);
        }

        return postSlice;
    }

    public Slice<Post> findMyFeeds(Long postId, Long memberId, Pageable pageable) {
        Slice<Post> postSlice;
        if (postId == -1L) {
            // 처음 요청할때 (refresh)
            System.out.println("글내용으로 찾기 첫번째 요청");
            postSlice = postRepository.findAllMyFeed(Long.MAX_VALUE,memberId, pageable);

        } else {
            // 두번째 이상으로 요청할 때 (마지막 글의 pk 를 기준으로 함)
            System.out.println("글내용으로 찾기 다음 요청");
            postSlice = postRepository.findAllMyFeed(postId,memberId, pageable);
            // 갖고올 게시물이 없으면
        }
//         post를 slice 형태로 갖고오기

        if (postSlice.isEmpty() || postSlice == null) { // 불러올 게시물이 없을 때
            throw new PostException(PostExceptionType.NO_POST_FEED);
        }
        return postSlice;

    }

    public Slice<Comment> findMyComments(Long commentId, Long memberId, Pageable pageable) {
        Slice<Comment> commentSlice;
        if (commentId == -1L) {
            // 처음 요청할때 (refresh)
            System.out.println("내글 불러오기 첫번째 요청");
            commentSlice = commentRepository.findAllByIdLessThanAndMemberId(Long.MAX_VALUE,memberId, pageable);

        } else {
            // 두번째 이상으로 요청할 때 (마지막 글의 pk 를 기준으로 함)
            System.out.println("내글 불러오기 다음 요청");
            commentSlice = commentRepository.findAllByIdLessThanAndMemberId(commentId,memberId, pageable);
            // 갖고올 게시물이 없으면
        }
//         post를 slice 형태로 갖고오기

        if (commentSlice.isEmpty() || commentSlice == null) { // 불러올 게시물이 없을 때
            throw new PostException(PostExceptionType.NO_POST_FEED);
        }
        return commentSlice;

    }

    public Map.Entry<Long,Long> findGroupNo(Long postId, Long commentId) {
        Long groupNo;
        Long stepNo;
        Optional<Comment> commentOptional = commentRepository.findTopByPostIdOrderByIdDesc(postId);
        if (commentId == -1L) { // 댓글
            if (commentOptional.isEmpty() || commentOptional == null) {
                groupNo = 1L;
                stepNo = 0L;
            } else {
                groupNo = commentOptional.get().getGroupNo() + 1L;
                stepNo = 0L;
            }
        } else {
            groupNo = commentRepository.findById(commentId).get().getGroupNo();
            stepNo = commentRepository.findTopByPostIdAndGroupNoOrderByIdDesc(postId, groupNo).get().getStepNo() + 1;
        }
        return new AbstractMap.SimpleEntry<>(groupNo,stepNo);
    }

    public CommentPagingResponseDto getCommentList(List<Comment> commentGroupList) {
        CommentPagingResponseDto commentPagingResponseDto = CommentPagingResponseDto.CommentResponseBuilder()
                .commentId(commentGroupList.get(0).getId())
                .writerId(commentGroupList.get(0).getMember().getId())
                .writerNickname(commentGroupList.get(0).getMember().getNickname())
                .content(commentGroupList.get(0).getContent())
                .createdAt(commentGroupList.get(0).getCreatedAt())
                .commentLikeCnt(commentGroupList.get(0).getCommentLikeList().size())
                .groupNo(commentGroupList.get(0).getGroupNo())
                .writerType(false)
                .build();
        System.out.println(commentPagingResponseDto.toString());
        Optional<CafeAuth> cafeAuthOptional = cafeAuthRepository.findById(commentGroupList.get(0).getMember().getNickname());
        if (cafeAuthOptional.isPresent()) {
            System.out.println("1차점검");
            Long cafeId = cafeAuthOptional.get().getCafeId();
            Cafe cafe = cafeRepository.findById(cafeId).get();
            MemberCafeTier memberCafeTier = memberCafeTierRepository.findByMemberIdAndCafeId(commentGroupList.get(0).getMember().getId(), cafeId).get();
            commentPagingResponseDto.updateCommentVerifiedUser(cafeId, cafe.getName(), memberCafeTier.getExp(), cafe.getBrandType());
        }
        System.out.println("2차점검");
        System.out.println(commentPagingResponseDto.getVerifiedCafeName());


        // cafeAuth 를 통해 find 를 해야함.. (nickname)
        // like check commentLike 에서 findByIdandCommentId 하기
        // exp

        List<RepliesPagingResponseDto> repliesList = new ArrayList<>();

        Long parentId = commentGroupList.get(0).getId();
        for (Comment commentSlice : commentGroupList) {
            if (commentSlice.getStepNo() == 0) {

            } else {
                RepliesPagingResponseDto repliesPagingResponseDto = RepliesPagingResponseDto.RepliesResponseBuilder()
                        .commentId(commentSlice.getId())
                        .writerId(commentSlice.getMember().getId())
                        .writerNickname(commentSlice.getMember().getNickname())
                        .content(commentSlice.getContent())
                        .createdAt(commentSlice.getCreatedAt())
                        .commentLikeCnt(commentSlice.getCommentLikeList().size())
                        .parentId(parentId)
                        .writerType(false)
                        .build();
                System.out.println("빌드는됨!");

                Optional<CafeAuth> cafeOptional = cafeAuthRepository.findById(commentSlice.getMember().getNickname());
                if (cafeAuthOptional.isPresent()) {
                    Long cafeId = cafeOptional.get().getCafeId();
                    Cafe cafe = cafeRepository.findById(cafeId).get();
                    MemberCafeTier memberCafeTier = memberCafeTierRepository.findByMemberIdAndCafeId(commentSlice.getMember().getId(), cafeId).get();
                    repliesPagingResponseDto.updateCommentVerifiedUser(cafeId, cafe.getName(), memberCafeTier.getExp(), cafe.getBrandType());
                }
                System.out.println("업데이트도됨!");
                if (repliesPagingResponseDto != null) {
                    repliesList.add(repliesPagingResponseDto);
                }
            }

        }
        System.out.println("for 문의 끝!! ");
        if (!repliesList.isEmpty()) {
            commentPagingResponseDto.updateReplies(repliesList);
        }
        return commentPagingResponseDto;
    }


}
