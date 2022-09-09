package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.comment.CommentCreateResponse;
import project.adam.controller.dto.comment.CommentFindResponse;
import project.adam.controller.dto.reply.ReplyListFindResponse;
import project.adam.entity.comment.Comment;
import project.adam.entity.member.Member;
import project.adam.security.SecurityUtil;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.ReplyService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentReportRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final MemberService memberService;
    private final CommentService commentService;
    private final ReplyService replyService;

    @Secured("ROLE_USER")
    @PostMapping
    public CommentCreateResponse createComment(@Validated @RequestBody CommentCreateRequest commentDto) {
        Member member = memberService.findByEmail(SecurityUtil.getCurrentMemberEmail());
        Comment savedComment = commentService.create(member, commentDto);
        return new CommentCreateResponse(savedComment);
    }

    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        return new CommentFindResponse(findComment);
    }

    @GetMapping("/{commentId}/replies")
    public ReplyListFindResponse findReplies(@PathVariable Long commentId, Pageable pageable) {
        return new ReplyListFindResponse(replyService.findAllByComment(commentId, pageable));
    }

    @Secured("ROLE_USER")
    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable Long commentId, @Validated @RequestBody CommentUpdateRequest commentDto) {
        Comment findComment = commentService.find(commentId);
        commentService.update(commentId, commentDto);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        commentService.remove(commentId);
    }

    @Secured("ROLE_USER")
    @PostMapping("/{commentId}/report")
    public void createCommentReport(@PathVariable Long commentId, @RequestBody CommentReportRequest request) {
        Member member = memberService.findByEmail(SecurityUtil.getCurrentMemberEmail());
        Comment findComment = commentService.find(commentId);
        commentService.createCommentReport(member, commentId, request);
    }
}
