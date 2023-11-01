package com.backend.blog.projectblogrestapi.service.imple;

import com.backend.blog.projectblogrestapi.dtoClasses.CommentDTO;
import com.backend.blog.projectblogrestapi.entity.Comment;
import com.backend.blog.projectblogrestapi.entity.Post;
import com.backend.blog.projectblogrestapi.exceptions.BlogAPIException;
import com.backend.blog.projectblogrestapi.exceptions.ResourceNotFoundExeptions;
import com.backend.blog.projectblogrestapi.repository.CommentRepository;
import com.backend.blog.projectblogrestapi.repository.PostRepository;
import com.backend.blog.projectblogrestapi.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImple implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Override
    public CommentDTO createComment(long postId, CommentDTO commentDTO) {
        Comment comment=mapToEntity(commentDTO);
        Post post=postRepository.findById(postId).orElseThrow(
                ()-> new ResourceNotFoundExeptions("Post","id",postId)
        );
        comment.setPost(post);
        Comment newComment =commentRepository.save(comment);
        return mapToDto(newComment);
    }

    @Override
    public List<CommentDTO> getAllCommentsByPostId(long postId) {
        List<Comment> comments=commentRepository.findByPostId(postId);
        return comments.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CommentDTO getAllCommentsByPostIdAndCommentId(long postId, long commentId) {
        Post post=postRepository.findById(postId).orElseThrow(
                ()-> new ResourceNotFoundExeptions("Post","id",postId)
        );

        Comment comment=commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundExeptions("Comment","id", commentId)
        );
        if(comment.getPost().getId() != post.getId()){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Comment does not belong to post");
        }
        return mapToDto(comment);
    }

    @Override
    public CommentDTO updateComment(long postId,long commentId, CommentDTO commentReq) {
        Post post=postRepository.findById(postId).orElseThrow(
                ()-> new ResourceNotFoundExeptions("Post","id",postId)
        );
        Comment comment=commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundExeptions("Comment","id", commentId)
        );
        if(comment.getPost().getId()!=post.getId()){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Comment does not belong to post");
        }
        comment.setBody(commentReq.getBody());
        comment.setName(commentReq.getName());
        comment.setEmail(commentReq.getEmail());
        Comment updateComment= commentRepository.save(comment);
        return mapToDto(updateComment);
    }

    @Override
    public void deleteCommentByID(long postId, long commentId) {
        Post post=postRepository.findById(postId).orElseThrow(
                ()-> new ResourceNotFoundExeptions("Post","id",postId)
        );
        Comment comment=commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundExeptions("Comment","id", commentId)
        );
        if(comment.getPost().getId() != post.getId()){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Comment does not belong to post");

        }
        commentRepository.delete(comment);

    }

    private CommentDTO mapToDto(Comment comment){
        CommentDTO commentDTO=new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setName(comment.getName());
        commentDTO.setBody(comment.getBody());
        commentDTO.setEmail(comment.getEmail());
        return  commentDTO;
    }
    private Comment mapToEntity(CommentDTO commentDTO){
        Comment comment =new Comment();
        comment.setId(commentDTO.getId());
        comment.setName(commentDTO.getName());
        comment.setBody(commentDTO.getBody());
        comment.setEmail(commentDTO.getEmail());
        return comment;
    }
}
