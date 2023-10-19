package com.backend.blog.projectblogrestapi.service;

import com.backend.blog.projectblogrestapi.dtoClasses.PostDTO;

import java.util.List;

public interface PostService {
    PostDTO createPost(PostDTO postDTO);
    List<PostDTO> getAllPosts(int pageNo,int pageSize);
    PostDTO getPostById(long id);
    PostDTO updatePost(PostDTO postDTO,long id);
    void deletePostById(long id);

}
