package org.dbahrim.forum.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import java.util.Objects;

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
    private final CommentMapper commentMapper;

    @GetMapping
    @Operation(summary = "Get all posts")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                content = {
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Post.class)
                        )
                }
            )
        }
    )
    public Iterable<Post> getAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one post")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Post.class)
                    )
                }
            )
        }
    )
    public Post getOne(@PathVariable Long id) throws NotFoundException {
        return postRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @PostMapping
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Create a post")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Post.class)
                    )
                }
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "403",
                content = @Content
            )
        }
    )
    public Post insert(@RequestBody @Valid Post.PostPostRequest dto, @AuthenticationPrincipal User user) throws NotFoundException {
        Category category = categoryRepository.findById(dto.categoryId).orElseThrow(NotFoundException::new);
        Post post = postMapper.toPost(dto);
        post.category = category;
        post.user = user;
        return postRepository.save(post);
    }

    @PostMapping("/{id}")
    @Transactional
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Add a comment")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Post.class)
                    )
                }
            ),
            @ApiResponse(
                responseCode = "400",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "403",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "404",
                content = @Content
            )
        }
    )
    public Post comment(@PathVariable Long id, @RequestBody @Valid Comment.CommentPost commentPost, @AuthenticationPrincipal User user) throws NotFoundException {
        Post post = postRepository.findById(id).orElseThrow(NotFoundException::new);
        Comment comment = commentMapper.sourceToDestination(commentPost);
        comment.setUser(user);
        comment.postId = post.id;
        post.addComment(comment);
        return postRepository.save(post);
    }

    @PutMapping
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Fully edit a post")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Post.class)
                    )
                }
            ),
            @ApiResponse(
                responseCode = "400",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "403",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "404",
                content = @Content
            )
        }
    )
    public Post edit(@RequestBody @Valid Post.PostPutPatchRequest postPutPatchRequest, @AuthenticationPrincipal User user) throws NotFoundException {
        Post post = postRepository.findById(postPutPatchRequest.getId()).orElseThrow(NotFoundException::new);
        if (!Objects.equals(post.user.getId(), user.getId())) {
            throw new AccessDeniedException("Requires same user");
        }
        Category newCategory = null;
        if (postPutPatchRequest.categoryId != null) {
            newCategory = categoryRepository.findById(postPutPatchRequest.categoryId).orElseThrow(NotFoundException::new);
        }
        return postRepository.save(post.fromPatch(postPutPatchRequest, newCategory, false).orElseThrow(NotFoundException::new));
    }

    @PatchMapping
    @SecurityRequirement(name = "bearer")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Partially edit a post")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        content = {
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Post.class)
                                )
                        }
                ),
                @ApiResponse(
                        responseCode = "400",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "403",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        content = @Content
                )
            }
    )
    public Post patch(@RequestBody @Valid Post.PostPutPatchRequest postPutPatchRequest, @AuthenticationPrincipal User user) throws NotFoundException {
        Post post = postRepository.findById(postPutPatchRequest.getId()).orElseThrow(NotFoundException::new);
        if (!Objects.equals(post.user.getId(), user.getId())) {
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
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Delete a post")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content
                    )
            }
    )
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user) throws NotFoundException {
        Post post = postRepository.findById(id).orElseThrow(NotFoundException::new);
        if (!Objects.equals(post.user.getId(), user.getId())) {
            throw new AccessDeniedException("Requires same user");
        }
        postRepository.deleteById(id);
    }

}
