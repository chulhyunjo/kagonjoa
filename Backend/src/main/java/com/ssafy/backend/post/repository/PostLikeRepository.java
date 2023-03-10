package com.ssafy.backend.post.repository;


import com.ssafy.backend.post.domain.entity.Post;
import com.ssafy.backend.post.domain.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    void deleteByPostIdAndMemberId(Long postId, Long memberId);

    int countByPostId(Long postId);

    Optional<PostLike> findByPostIdAndMemberId(Long postId, long memberId);
}
