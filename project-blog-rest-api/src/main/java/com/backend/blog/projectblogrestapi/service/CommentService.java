package com.backend.blog.projectblogrestapi.service;

import com.backend.blog.projectblogrestapi.dtoClasses.CommentDTO;

import java.util.List;

public interface CommentService {
    CommentDTO createComment(long postId,CommentDTO commentDTO);

    List<CommentDTO> getAllCommentsByPostId(long postId);
    CommentDTO getAllCommentsByPostIdAndCommentId(long postId,long commentId);

    CommentDTO updateComment(long postId,long commentId,CommentDTO commentReq);
     void deleteCommentByID(long postId, long commentId);


}
