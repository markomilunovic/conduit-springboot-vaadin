package com.example.conduit_springboot_vaadin.service;

import com.example.conduit_springboot_vaadin.dto.article.ArticleDto;
import com.example.conduit_springboot_vaadin.dto.article.ArticleListDto;
import com.example.conduit_springboot_vaadin.dto.article.CreateArticleDto;
import com.example.conduit_springboot_vaadin.exception.ArticleNotFoundException;
import com.example.conduit_springboot_vaadin.mapper.ArticleMapper;
import com.example.conduit_springboot_vaadin.model.Article;
import com.example.conduit_springboot_vaadin.model.User;
import com.example.conduit_springboot_vaadin.repository.ArticleRepository;
import com.example.conduit_springboot_vaadin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.conduit_springboot_vaadin.dto.article.ArticleListResponseDto;

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

    public ArticleService (
            ArticleRepository articleRepository,
            ArticleMapper articleMapper,
            UserRepository userRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.userRepository = userRepository;
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
}
