package com.example.conduit_springboot_vaadin.service;

import com.example.conduit_springboot_vaadin.dto.article.*;
import com.example.conduit_springboot_vaadin.dto.comment.AddCommentDto;
import com.example.conduit_springboot_vaadin.dto.comment.CommentDto;
import com.example.conduit_springboot_vaadin.dto.comment.CommentResponseDto;
import com.example.conduit_springboot_vaadin.exception.AccessDeniedException;
import com.example.conduit_springboot_vaadin.exception.ArticleNotFoundException;
import com.example.conduit_springboot_vaadin.exception.CommentNotFoundException;
import com.example.conduit_springboot_vaadin.mapper.ArticleMapper;
import com.example.conduit_springboot_vaadin.mapper.CommentMapper;
import com.example.conduit_springboot_vaadin.model.Article;
import com.example.conduit_springboot_vaadin.model.Comment;
import com.example.conduit_springboot_vaadin.model.User;
import com.example.conduit_springboot_vaadin.repository.ArticleRepository;
import com.example.conduit_springboot_vaadin.repository.CommentRepository;
import com.example.conduit_springboot_vaadin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class ArticleService{

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public ArticleService (
            ArticleRepository articleRepository,
            ArticleMapper articleMapper,
            UserRepository userRepository,
            CommentMapper commentMapper,
            CommentRepository commentRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
    }

    /**
     * Creates a new article with the provided details.
     * <p>
     * This method accepts a {@link CreateArticleDto} object containing details of the article to be created.
     * It generates a unique slug based on the article title, maps the DTO to an {@link Article} entity,
     * saves the article to the repository, and then maps the saved entity back to an {@link ArticleDto}.
     * </p>
     * @param createArticleDto The DTO containing article creation data.
     * @param authorUsername   The username of the author creating the article.
     * @param currentUserId    The ID of the current user performing the operation.
     * @return The DTO representing the created article.
     */
    public ArticleDto createArticle(
            CreateArticleDto createArticleDto,
            String authorUsername,
            String currentUserId
    ) {

        log.info("Creating article with : {}", createArticleDto.getTitle());
        log.debug("Create article with data: {}", createArticleDto);

        String slug = generateUniqueSlug(createArticleDto.getTitle());
        log.debug("Unique generated article slug: {}", slug);

        Article article = articleMapper.createArticleDtoToArticle(createArticleDto, authorUsername, slug);

        Article savedArticle = articleRepository.save(article);

        log.info("Article created successfully: {}", savedArticle.getTitle());
        log.debug("savedArticle created with data: {}", savedArticle);

        return articleMapper.articleToArticleDto(savedArticle, currentUserId);
    }

    /**
     * Retrieves a single article by its unique slug.
     * <p>
     * This method looks up the corresponding article in the repository by slug, and
     * if found, returns a mapped {@link ArticleDto} object. If no matching article is
     * found, an {@link ArticleNotFoundException} is thrown.
     * </p>
     *
     * @param slug the slug of the article to retrieve
     * @return an {@link ArticleDto} containing the article’s data
     * @throws ArticleNotFoundException if an article with the specified slug does not exist
     */
    public ArticleDto getArticleBySlug(String slug) {

        log.info("Retrieving article with slug: {}", slug);

        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));

        log.info("Article retrieved successfully: {}", article.getTitle());
        log.debug("Article data: {}", article);

        return articleMapper.articleToArticleDto(article, null);
    }

    /**
     * Retrieves a list of articles filtered and paginated according to the provided parameters.
     * <p>
     * The returned articles are ordered by their creation date in descending order (most recent first).
     * Filters can be applied based on:
     * <ul>
     *     <li><strong>tag</strong>: Only articles containing the specified tag.</li>
     *     <li><strong>authorUsername</strong>: Only articles authored by the specified user.</li>
     *     <li><strong>favoritedUsername</strong>: Only articles favorited by the specified user.</li>
     * </ul>
     * If multiple filters are provided, only articles matching all of them are returned.
     * If a specified username does not exist for the "favorited" filter, an empty result is returned.
     * If no filters are provided, all articles are returned.
     *
     * @param tag              The tag to filter articles by (optional).
     * @param authorUsername   The username of the author to filter articles by (optional).
     * @param favoritedUsername The username of the user who has favorited the articles (optional).
     * @param limit            The maximum number of articles to return (default is 20).
     * @param offset           The number of articles to skip before starting to collect the result set (default is 0).
     * @param currentUserId    The ID of the currently authenticated user (used to determine if articles are favorited by the current user).
     * @return An {@link ArticleListResponseDto} containing the filtered list of articles and the total article count.
     */
    public ArticleListResponseDto listArticles(
            String tag,
            String authorUsername,
            String favoritedUsername,
            int limit,
            int offset,
            String currentUserId
    ) {
        log.info("Filtering articles with filters: tag: {}, author: {}, favorited: {}, limit: {}, offset: {}",
                tag, authorUsername, favoritedUsername, limit, offset);

        PageRequest pageRequest = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        log.debug("Page request created with page: {}, size: {}, sort: {}",
                offset / limit, limit, "createdAt DESC");

        String favoritedUserId = null;
        if (favoritedUsername != null && !favoritedUsername.isEmpty()) {
            log.debug("Looking up user by favorited username: {}", favoritedUsername);
            User favoritedUser = userRepository.findByUsername(favoritedUsername).orElse(null);

            if (favoritedUser != null) {
                favoritedUserId = favoritedUser.getId();
                log.debug("Favorited user found: {} with ID {}", favoritedUser.getUsername(), favoritedUserId);
            } else {
                log.warn("No user found for favorited username: {}. Returning empty results.", favoritedUsername);
                return new ArticleListResponseDto(List.of(), 0);
            }
        }

        Page<Article> page;

        if (authorUsername != null && !authorUsername.isEmpty() && tag != null && !tag.isEmpty() && favoritedUserId != null) {
            log.debug("Querying articles by author: {}, tag: {}, and favoritedUserId: {}",
                    authorUsername, tag, favoritedUserId);
            page = articleRepository.findByAuthorAndTagListAndFavoritedBy(authorUsername, tag, favoritedUserId, pageRequest);
        } else if (authorUsername != null && !authorUsername.isEmpty() && tag != null && !tag.isEmpty()) {
            log.debug("Querying articles by author: {} and tag: {}", authorUsername, tag);
            page = articleRepository.findByAuthorAndTagList(authorUsername, tag, pageRequest);
        } else if (authorUsername != null && !authorUsername.isEmpty() && favoritedUserId != null) {
            log.debug("Querying articles by author: {} and favoritedUserId: {}", authorUsername, favoritedUserId);
            page = articleRepository.findByAuthorAndFavoritedBy(authorUsername, favoritedUserId, pageRequest);
        } else if (tag != null && !tag.isEmpty() && favoritedUserId != null) {
            log.debug("Querying articles by tag: {} and favoritedUserId: {}", tag, favoritedUserId);
            page = articleRepository.findByTagListAndFavoritedBy(tag, favoritedUserId, pageRequest);
        } else if (authorUsername != null && !authorUsername.isEmpty()) {
            log.debug("Querying articles by author: {}", authorUsername);
            page = articleRepository.findByAuthor(authorUsername, pageRequest);
        } else if (tag != null && !tag.isEmpty()) {
            log.debug("Querying articles by tag: {}", tag);
            page = articleRepository.findByTagList(tag, pageRequest);
        } else if (favoritedUserId != null) {
            log.debug("Querying articles by favoritedUserId: {}", favoritedUserId);
            page = articleRepository.findByFavoritedBy(favoritedUserId, pageRequest);
        } else {
            log.debug("No filters provided. Querying all articles.");
            page = articleRepository.findAll(pageRequest);
        }

        List<ArticleListDto> articleListDtos = page.getContent().stream()
                .map(article -> articleMapper.articleToArticleListDto(article, currentUserId))
                .toList();

        log.debug("Mapped {} articles to ArticleListDto", articleListDtos.size());

        ArticleListResponseDto response = ArticleListResponseDto.builder()
                .articles(articleListDtos)
                .articlesCount((int) page.getTotalElements())
                .build();

        log.info("Returning {} articles with a total of {} articles found",
                articleListDtos.size(), page.getTotalElements());

        return response;
    }

    /**
     * Retrieves a personalized feed of articles for the current user.
     * <p>
     * This method generates a feed consisting of the most recent articles
     * written by users followed by the current user. The feed is paginated
     * based on the specified limit and offset parameters, and articles
     * are sorted by their creation date in descending order (most recent first).
     * If the user does not follow any other users, the feed will be empty.
     * </p>
     *
     * @param currentUserId The ID of the currently authenticated user requesting the feed.
     * @param limit         The maximum number of articles to return in the feed (pagination size).
     * @param offset        The number of articles to skip before starting to collect results (pagination offset).
     * @return An {@link ArticleListResponseDto} containing the list of articles in the feed and the total article count.
     * @throws RuntimeException If the current user is not found in the system.
     */
    public ArticleListResponseDto getFeed(String currentUserId, int limit, int offset) {
        log.info("Generating feed for user with ID: {}", currentUserId);

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found with ID: " + currentUserId));

        List<String> followedUserIds = currentUser.getFollowing();
        if (followedUserIds.isEmpty()) {
            return new ArticleListResponseDto(List.of(), 0);
        }

        List<String> followedUsernames = followedUserIds.stream()
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(User::getUsername)
                .toList();

        if (followedUsernames.isEmpty()) {
            return new ArticleListResponseDto(List.of(), 0);
        }

        PageRequest pageRequest = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        log.debug("Feed PageRequest created: page={}, size={}, sort=createdAt DESC", offset / limit, limit);

        Page<Article> page = articleRepository.findByAuthorIn(followedUsernames, pageRequest);

        List<ArticleListDto> articleListDtos = page.getContent().stream()
                .map(article -> articleMapper.articleToArticleListDto(article, currentUserId))
                .toList();

        log.info("Returning feed with {} articles out of total {}", articleListDtos.size(), page.getTotalElements());

        return ArticleListResponseDto.builder()
                .articles(articleListDtos)
                .articlesCount((int) page.getTotalElements())
                .build();
    }

    /**
     * Updates an existing article identified by its slug.
     * <p>
     * This method allows the author of an article to update its title, description, or body.
     * If the title is updated, a new unique slug is generated for the article. The method
     * also updates the `updatedAt` timestamp to reflect the modification time. Only the
     * author of the article is permitted to make updates; otherwise, an exception is thrown.
     * </p>
     *
     * @param slug           The unique identifier (slug) of the article to be updated.
     * @param updateDto      A DTO containing the updated article fields.
     * @param currentUserId  The ID of the currently authenticated user making the request.
     * @param currentUsername The username of the currently authenticated user making the request.
     * @return An {@link ArticleDto} containing the updated article details.
     * @throws ArticleNotFoundException If no article is found with the provided slug.
     * @throws AccessDeniedException    If the current user is not the author of the article.
     */
    public ArticleDto updateArticle(
            String slug,
            UpdateArticleDto updateDto,
            String currentUserId,
            String currentUsername
    ) {
        log.info("Updating article with slug: {}", slug);
        log.debug("Updating article with data: {}", updateDto);
        log.debug("Logged user ID: {}", currentUserId);
        log.debug("Logged user username: {}", currentUsername);

        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));

        if (!article.getAuthor().equals(currentUsername)) {
            log.warn("User {} tried to update article by author {}", currentUsername, article.getAuthor());
            throw new AccessDeniedException("You are not allowed to update this article.");
        }

        if (updateDto.getTitle() != null && !updateDto.getTitle().isBlank()) {
            String newTitle = updateDto.getTitle().trim();

            if (!article.getTitle().equals(newTitle)) {
                log.info("Title updated from '{}' to '{}'.", article.getTitle(), newTitle);
                article.setTitle(newTitle);
                String newSlug = generateUniqueSlug(newTitle);
                article.setSlug(newSlug);
            }
        }
        if (updateDto.getDescription() != null && !updateDto.getDescription().isBlank()) {
            article.setDescription(updateDto.getDescription().trim());
        }
        if (updateDto.getBody() != null && !updateDto.getBody().isBlank()) {
            article.setBody(updateDto.getBody().trim());
        }

        article.setUpdatedAt(Instant.now());

        Article savedArticle = articleRepository.save(article);
        log.info("Article with slug '{}' updated successfully.", savedArticle.getSlug());

        return articleMapper.articleToArticleDto(savedArticle, currentUserId);
    }

    /**
     * Deletes an article by its slug.
     * <p>
     * Only the article's author can perform this deletion; otherwise,
     * an {@link AccessDeniedException} is thrown.
     * </p>
     *
     * @param slug            The slug of the article to delete.
     * @param currentUsername The username of the currently authenticated user.
     * @throws ArticleNotFoundException If no article is found with the provided slug.
     * @throws AccessDeniedException    If the current user is not the author of the article.
     */
    public void deleteArticle(String slug, String currentUsername) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));

        if (!article.getAuthor().equals(currentUsername)) {
            log.warn("User '{}' attempted to delete article by author '{}'", currentUsername, article.getAuthor());
            throw new AccessDeniedException("You are not allowed to delete this article.");
        }

        articleRepository.delete(article);
        log.info("Article with slug '{}' has been deleted successfully by user '{}'.", slug, currentUsername);
    }

    /**
     * Generates a unique slug for the article based on its title.
     *
     * @param title The title of the article.
     * @return A unique slug string.
     */
    private String generateUniqueSlug(String title) {

        String baseSlug = title.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        String slug = baseSlug;
        int count = 1;

        while (articleRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count++;
        }

        return slug;
    }

    /**
     * Adds a new comment to the specified article.
     * <p>
     * This method retrieves the article identified by its slug and, if it exists,
     * creates a new comment using the provided comment body. The comment is associated
     * with the article slug and with the username of the user making the request.
     * </p>
     *
     * @param slug            The slug of the article to which the comment is being added.
     * @param addCommentDto   A DTO containing the body text of the comment.
     * @param currentUsername The username of the authenticated user adding the comment.
     * @param currentUserId   The ID of the authenticated user.
     * @return A {@link CommentDto} representing the newly created comment, including its ID, body, timestamps, and author details.
     * @throws ArticleNotFoundException If no article is found with the given slug.
     */
    public CommentDto addCommentToArticle(String slug, AddCommentDto addCommentDto, String currentUsername, String currentUserId) {

        log.info("Adding comment to article with slug: {}", slug);

        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));

        Comment comment = commentMapper.addCommentDtoToComment(addCommentDto, currentUsername, article.getSlug());
        Comment savedComment = commentRepository.save(comment);

        log.info("Comment added successfully: {}", savedComment.getId());
        log.debug("savedComment created with data: {}", savedComment);

        return commentMapper.commentToCommentDto(savedComment, currentUserId, comment.getAuthor());
    }

    /**
     * Retrieves a list of comments associated with a specific article identified by its slug.
     * <p>
     * This method logs the retrieval request, verifies the existence of the article by its slug,
     * and fetches the associated comments. Each {@link Comment}
     * is then mapped to a {@link CommentDto} based on the current user's context. Finally, it returns
     * a list of {@link CommentDto} objects representing the article's comments.
     * </p>
     *
     * @param slug           The unique slug identifier of the article for which comments are to be fetched.
     * @param currentUserId  The ID of the currently authenticated user.
     * @return               A {@link List} of {@link CommentDto} objects containing the details of each comment associated
     *                       with the specified article.
     * @throws ArticleNotFoundException if no article exists with the provided slug.
     */
    public List<CommentDto> getCommentsFromArticle(String slug, String currentUserId) {

        log.info("Fetching comments for article: {}", slug);

        if(!articleRepository.existsBySlug(slug)) {
            throw new ArticleNotFoundException(slug);
        }

        List<Comment> comments = commentRepository.findByArticle(slug);

        log.info("Comments fetched successfully for article: {}", slug);

        return comments.stream()
                .map(comment -> commentMapper.commentToCommentDto(comment, currentUserId, comment.getAuthor()))
                .toList();
    }

    /**
     * Deletes a comment identified by its ID and associated article slug.
     * <p>
     * This method logs the deletion request, fetches the comment by its ID and article slug,
     * verifies that the current user is the author of the comment, and deletes the comment
     * from the repository.
     * </p>
     *
     * @param slug           The unique slug identifier of the article.
     * @param commentId      The unique identifier of the comment to be deleted.
     * @param currentUsername The username of the currently authenticated user attempting the deletion.
     * @throws CommentNotFoundException if no comment exists with the provided ID for the specified article.
     * @throws AccessDeniedException    if the current user is not the author of the comment.
     */
    public void deleteComment(String slug, String commentId, String currentUsername) {

        log.info("Request received to delete comment with ID: {} from article: {}", commentId, slug);

        Comment comment = commentRepository.findByIdAndArticle(commentId, slug)
                .orElseThrow(() -> new CommentNotFoundException(commentId, slug));

        log.debug("Comment found: {}", comment);

        if (!comment.getAuthor().equals(currentUsername)) {
            log.warn("User '{}' attempted to delete comment '{}' authored by '{}'",
                    currentUsername, commentId, comment.getAuthor());
            throw new AccessDeniedException("You are not authorized to delete this comment.");
        }

        commentRepository.delete(comment);
        log.info("Comment with ID: {} successfully deleted from article: {}", commentId, slug);
    }


}
