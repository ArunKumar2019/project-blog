package com.backend.blog.projectblogrestapi.repository;

import com.backend.blog.projectblogrestapi.entity.Comment;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPostId(long postId);

}
