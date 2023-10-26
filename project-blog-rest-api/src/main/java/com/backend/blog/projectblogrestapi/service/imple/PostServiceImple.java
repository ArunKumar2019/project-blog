package com.backend.blog.projectblogrestapi.service.imple;

import com.backend.blog.projectblogrestapi.dtoClasses.PostDTO;
import com.backend.blog.projectblogrestapi.dtoClasses.PostResponse;
import com.backend.blog.projectblogrestapi.entity.Post;
import com.backend.blog.projectblogrestapi.exceptions.ResourceNotFoundExeptions;
import com.backend.blog.projectblogrestapi.repository.PostRepository;
import com.backend.blog.projectblogrestapi.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImple implements PostService {
          @Autowired
           private PostRepository postRepository;
            //   @Autowired
            //   public PostServiceImple(PostRepository postRepository){
            //        this.postRepository=postRepository;
            //    }
    @Override
    public PostDTO createPost(PostDTO postDTO) {
        Post post=mapToEntity(postDTO);
        Post created=postRepository.save(post);
        //Convert to DTO
        PostDTO response=mapToDto(created);
        return response;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
//        create pageable instance
        Sort sort=sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :Sort.by(sortBy).descending();
        Pageable pageable= PageRequest.of(pageNo,pageSize, sort);
        Page<Post> getAll=postRepository.findAll(pageable);
//        get the content for page object
        List<Post> listOfposts=getAll.getContent();
        List<PostDTO> content=listOfposts.stream().map(post -> mapToDto(post)).collect(Collectors.toList());
        PostResponse postResponse=new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(getAll.getNumber());
        postResponse.setPageSize(getAll.getSize());
        postResponse.setTotalElement(getAll.getTotalElements());
        postResponse.setTotalPage(getAll.getTotalPages());
        postResponse.setLast(getAll.isLast());
        return postResponse;
    }

    @Override
    public PostDTO getPostById(long id) {
        Post post=postRepository.findById(id).orElseThrow(()->new ResourceNotFoundExeptions("Post","id",id));
        PostDTO response=mapToDto(post);
        return response;
    }

    @Override
    public PostDTO updatePost(PostDTO postDTO, long id) {
        Post post=postRepository.findById(id).orElseThrow(()->new ResourceNotFoundExeptions("Post","id",id));

        post.setContent(postDTO.getContent());
        post.setDescription(postDTO.getDescription());
        post.setTitle(postDTO.getTitle());

        Post updated=postRepository.save(post);

        return mapToDto(updated);

    }

    @Override
    public void deletePostById(long id) {
        Post post=postRepository.findById(id).orElseThrow(()->new ResourceNotFoundExeptions("Post","id",id));
       //if it is there in db then only deleted or else it throws exception
        postRepository.delete(post);
    }

    private PostDTO mapToDto(Post post){
        PostDTO response=new PostDTO();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setDescription(post.getDescription());
        response.setTitle(post.getTitle());
        return response;
    }
    private Post mapToEntity(PostDTO postDTO){
        Post post=new Post();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setContent(postDTO.getContent());
        return post;
    }
}
;