package com.backend.blog.projectblogrestapi.service;

import com.backend.blog.projectblogrestapi.dtoClasses.PostDTO;
import com.backend.blog.projectblogrestapi.dtoClasses.PostResponse;

public interface PostService {
    PostDTO createPost(PostDTO postDTO);
    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
    PostDTO getPostById(long id);
    PostDTO updatePost(PostDTO postDTO,long id);
    void deletePostById(long id);

}
