package com.backend.blog.projectblogrestapi.repository;

import com.backend.blog.projectblogrestapi.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
//we dont need add externally add @Repository by default it takes from simplejpaReps
public interface PostRepository extends JpaRepository<Post, Long> {
}