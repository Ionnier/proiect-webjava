package org.dbahrim.forum.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbahrim.forum.data.CategoryRepository;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.models.Category;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.models.mappers.CommentMapper;
import org.dbahrim.forum.models.mappers.PostMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.dbahrim.forum.controllers.ErrorController.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins="*")
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;

    @GetMapping
    public Iterable<Post> getAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public Post getOne(@PathVariable Long id) throws NotFoundException {
        return postRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @PostMapping
    public Post insert(@RequestBody @Valid Post.PostPostRequest dto, @AuthenticationPrincipal User user) throws NotFoundException {
        Category category = categoryRepository.findById(dto.categoryId).orElseThrow(NotFoundException::new);
        Post post = postMapper.toPost(dto);
        post.category = category;
        return postRepository.save(post);
    }

    @PostMapping("/{id}")
    @Transactional
    public Post comment(@PathVariable Long id, @RequestBody @Valid Comment.CommentPost commentPost, @AuthenticationPrincipal User user, CommentMapper commentMapper) throws NotFoundException {
        Post post = postRepository.findById(id).orElseThrow(NotFoundException::new);
        Comment comment = commentMapper.sourceToDestination(commentPost);
        comment.setUser(user);
        post.addComment(comment);
        commentRepository.save(comment);
        return postRepository.save(post);
    }

    @PutMapping
    public Post edit(@RequestBody @Valid Post.PostPutPatchRequest postPutPatchRequest, @AuthenticationPrincipal User user) throws NotFoundException {
        Post post = postRepository.findById(postPutPatchRequest.getId()).orElseThrow(NotFoundException::new);
        if (post.user != user) {
            throw new AccessDeniedException("Requires same user");
        }
        Category newCategory = null;
        if (postPutPatchRequest.categoryId != null) {
            newCategory = categoryRepository.findById(postPutPatchRequest.categoryId).orElseThrow(NotFoundException::new);
        }
        return postRepository.save(post.fromPatch(postPutPatchRequest, newCategory, false).orElseThrow(NotFoundException::new));
    }

    @PatchMapping
    public Post patch(@RequestBody @Valid Post.PostPutPatchRequest postPutPatchRequest, @AuthenticationPrincipal User user) throws NotFoundException {
        Post post = postRepository.findById(postPutPatchRequest.getId()).orElseThrow(NotFoundException::new);
        if (post.user != user) {
            throw new AccessDeniedException("Requires same user");
        }
        Category newCategory = null;
        if (postPutPatchRequest.categoryId != null) {
            newCategory = categoryRepository.findById(postPutPatchRequest.categoryId).orElseThrow(NotFoundException::new);
        }
        return postRepository.save(post.fromPatch(postPutPatchRequest, newCategory, true).orElseThrow(NotFoundException::new));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user) throws NotFoundException {
        Post post = postRepository.findById(id).orElseThrow(NotFoundException::new);
        if (post.user != user) {
            throw new AccessDeniedException("Requires same user");
        }
        postRepository.deleteById(id);
    }

}
