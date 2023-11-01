package com.backend.blog.projectblogrestapi.controller;

import com.backend.blog.projectblogrestapi.dtoClasses.CommentDTO;
import com.backend.blog.projectblogrestapi.service.CommentService;
import com.backend.blog.projectblogrestapi.service.imple.CommentServiceImple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("posts/{postId}/comments")
    public ResponseEntity<CommentDTO> createComment(@PathVariable(value = "postId") long id, @RequestBody CommentDTO commentDTO){
        return new ResponseEntity<>(commentService.createComment(id,commentDTO), HttpStatus.CREATED);
    }
    @GetMapping("posts/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getAllCommentsByPostId(@PathVariable(value = "postId") long postId){
        return new ResponseEntity<>(commentService.getAllCommentsByPostId(postId),HttpStatus.OK);
    }
    @GetMapping("posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> getAllCommentsByPostIdAndCommentId
          (@PathVariable(value = "postId") long postId,@PathVariable (value = "commentId") long commentId){
        return new ResponseEntity<>(commentService.getAllCommentsByPostIdAndCommentId(postId,commentId),HttpStatus.OK);
    }

    @PutMapping("posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateCommentsByPostAndCommentId(@PathVariable(value = "postId") long postId,
                                                                       @PathVariable(value = "commentId") long commentId,
                                                                       @RequestBody CommentDTO commentDTO){
        return new ResponseEntity<>(commentService.updateComment(postId,commentId,commentDTO), HttpStatus.CREATED);
    }
    @DeleteMapping("posts/{postId}/comments/{commentId}")
    public ResponseEntity<String> deleteCommentPostById(@PathVariable (name = "postId") long postId,@PathVariable (name = "commentId")
                                                        long commentId){
        commentService.deleteCommentByID(postId,commentId);
        return new ResponseEntity<>("Comment is Deleted Successfully",HttpStatus.OK);
    }
}
