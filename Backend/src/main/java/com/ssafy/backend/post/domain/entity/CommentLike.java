package com.ssafy.backend.post.domain.entity;

import com.ssafy.backend.member.domain.entity.Member;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Table(name = "comment_likes")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommentLike {

    //== Column  ==//

        /** 1. 댓글 좋아요 ID  **/
        @Id
        @GeneratedValue
        @Column(columnDefinition = "INT UNSIGNED")
        private Long id;

        /** 2. 댓글 pk를 comment 테이블과 조인을 이용하여 사용 - 게시글이 삭제되면 모든 댓글 좋아요 삭제   **/
//        @ManyToOne(fetch = LAZY)
//        @JoinColumn(name = "post_id")
//        @OnDelete(action = OnDeleteAction.CASCADE)
//        private Comment comment;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn (name = "comment_id")
        @OnDelete(action = OnDeleteAction.CASCADE)
        private Comment comment;

        /** 3. 멤버 - 좋아요를 누른 멤버의 id 를 저장   **/
        // 알림 다이어트를 해서, 멤버 좋아요는 필요없을듯 함. 그래도 혹시모르니 매핑해놓겠음
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn (name = "member_id")
        private Member member;

        @Builder(builderMethodName = "CommentLikeBuilder", builderClassName = "CommentLikeBuilder")
        CommentLike(Comment comment, Member member) {
                this.comment = comment;
                this.member = member;
        }


}