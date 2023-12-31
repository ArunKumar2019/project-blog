package com.backend.blog.projectblogrestapi.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(
        name = "posts",uniqueConstraints ={@UniqueConstraint (columnNames = "title")}
)
public class Post {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
   private long id;

   @Column(name = "title",nullable = false)
   private String title;

   @Column(name = "description",nullable = false)
   private String description;

   @Column(name = "content",nullable = false)
   private String content;

   @OneToMany(mappedBy = "post",cascade =CascadeType.ALL)
   private Set<Comment> commentSet=new HashSet<>();
}

