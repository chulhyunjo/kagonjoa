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
import com.ssafy.backend.member.service.MemberService;
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

@RequiredArgsConstructor // ?????? ??????????
@Transactional(readOnly = true)
@Service
public class PagingUtil {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CafeAuthRepository cafeAuthRepository;
    private final CafeRepository cafeRepository;
    private final MemberCafeTierRepository memberCafeTierRepository;
    private final MemberService memberService;


    /**
     * 0. ?????? ??????
     **/
    public Slice<Post> getPostFeeds(List<Long> cafeIdList, Long postId, List<PostType> types, Pageable pageable) {
        Slice<Post> postSlice;
        if (types.contains(PostType.hot)) { // ??? ???????????? ???????????? ?????? ???

            if (postId == -1L) {
                // ?????? ???????????? (refresh)
                postSlice = postRepository.findHotPostNext(cafeIdList, Long.MAX_VALUE, pageable);
            } else {
                // ????????? ???????????? ????????? ??? (????????? ?????? pk ??? ???????????? ???)
                postSlice = postRepository.findHotPostNext(cafeIdList, postId, pageable);
                // ????????? ???????????? ?????????
            }

        } else { //

            if (postId == -1L) {
                // ?????? ???????????? (refresh)
                postSlice = postRepository.findNextFeed(Long.MAX_VALUE, types, cafeIdList, pageable);
            } else {
                // ????????? ???????????? ????????? ??? (????????? ?????? pk ??? ???????????? ???)
                postSlice = postRepository.findNextFeed(postId, types, cafeIdList, pageable);
                // ????????? ???????????? ?????????
            }
        }
        return postSlice;
    }


    public Slice<Post> getSearchedFeeds(List<Long> cafeIdList, Long postId, String searchKeyword, int searchType, Pageable pageable) {
        Slice<Post> postSlice;
        if (searchType == 1) { // ??? ???????????? ??????

            if (postId == -1L) {
                // ?????? ???????????? (refresh)
                postSlice = postRepository.findBySearchContentFirst(cafeIdList, searchKeyword, pageable);

            } else {
                // ????????? ???????????? ????????? ??? (????????? ?????? pk ??? ???????????? ???)
                postSlice = postRepository.findBySearchContentNext(cafeIdList, searchKeyword, postId, pageable);
                // ????????? ???????????? ?????????
            }

        } else if (searchType == 2) { // ?????? ???????????? ??????

            if (postId == -1L) {
                // ?????? ???????????? (refresh)
                postSlice = postRepository.findBySearchNicknameFirst(cafeIdList, searchKeyword, pageable);
            } else {
                // ????????? ???????????? ????????? ??? (????????? ?????? pk ??? ???????????? ???)
                postSlice = postRepository.findBySearchNicknameNext(cafeIdList, searchKeyword, postId, pageable);
                // ????????? ???????????? ?????????
            }

        } else {
            throw new PostException(PostExceptionType.NOT_ALLOWED_TYPE);
        }

        return postSlice;
    }

    public Slice<Post> findMyFeeds(Long postId, Long memberId, Pageable pageable) {
        Slice<Post> postSlice;
        if (postId == -1L) {
            // ?????? ???????????? (refresh)
            postSlice = postRepository.findAllMyFeed(Long.MAX_VALUE, memberId, pageable);

        } else {
            // ????????? ???????????? ????????? ??? (????????? ?????? pk ??? ???????????? ???)
            postSlice = postRepository.findAllMyFeed(postId, memberId, pageable);
            // ????????? ???????????? ?????????
        }
//         post??? slice ????????? ????????????

        if (postSlice.isEmpty() || postSlice == null) { // ????????? ???????????? ?????? ???
            throw new PostException(PostExceptionType.NO_POST_FEED);
        }
        return postSlice;

    }

    public Slice<Comment> findMyComments(Long commentId, Long memberId, Pageable pageable) {
        Slice<Comment> commentSlice;
        if (commentId == -1L) {
            // ?????? ???????????? (refresh)
            commentSlice = commentRepository.findAllByIdLessThanAndMemberId(Long.MAX_VALUE, memberId, pageable);

        } else {
            // ????????? ???????????? ????????? ??? (????????? ?????? pk ??? ???????????? ???)
            commentSlice = commentRepository.findAllByIdLessThanAndMemberId(commentId, memberId, pageable);
            // ????????? ???????????? ?????????
        }
//         post??? slice ????????? ????????????

        if (commentSlice.isEmpty() || commentSlice == null) { // ????????? ???????????? ?????? ???
            throw new PostException(PostExceptionType.NO_POST_FEED);
        }
        return commentSlice;

    }

    public Map.Entry<Long, Long> findGroupNo(Long postId, Long commentId) {
        Long groupNo;
        Long stepNo;
        Optional<Comment> commentOptional = commentRepository.findTopByPostIdOrderByIdDesc(postId);
        if (commentId == -1L) { // ??????
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
        return new AbstractMap.SimpleEntry<>(groupNo, stepNo);
    }


}
