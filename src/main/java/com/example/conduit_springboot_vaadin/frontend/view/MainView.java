package com.example.conduit_springboot_vaadin.frontend.view;

import com.example.conduit_springboot_vaadin.frontend.service.FrontendArticleService;
import com.example.conduit_springboot_vaadin.frontend.service.FrontendTagService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Route("")
public class MainView extends VerticalLayout {

    private final FrontendTagService tagService;
    private final FrontendArticleService articleService;

    private final Grid<String> tagsGrid;
    private final Grid<Map<String, Object>> articlesGrid;

    @Autowired
    public MainView(FrontendTagService tagService, FrontendArticleService articleService) {
        this.tagService = tagService;
        this.articleService = articleService;

        tagsGrid = new Grid<>();
        articlesGrid = new Grid<>();

        H1 title = new H1("Home Page");

        VerticalLayout tagsSection = createTagsSection();
        VerticalLayout articlesSection = createArticlesSection();
        add(title, new HorizontalLayout(tagsSection, articlesSection));
    }

    private VerticalLayout createTagsSection() {
        VerticalLayout layout = new VerticalLayout();

        H1 tagsTitle = new H1("Tags");
        Button loadTagsButton = new Button("Load Tags", click -> loadTags());

        tagsGrid.addColumn(tag -> tag).setHeader("Tags");
        tagsGrid.setWidth("400px");
        tagsGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::loadArticlesByTag));

        layout.add(tagsTitle, loadTagsButton, tagsGrid);
        return layout;
    }

    private VerticalLayout createArticlesSection() {
        VerticalLayout layout = new VerticalLayout();

        H1 articlesTitle = new H1("Articles");
        Button loadArticlesButton = new Button("Load All Articles", click -> loadArticles());

        articlesGrid.addColumn(article -> article.get("title")).setHeader("Title");
        articlesGrid.addColumn(article -> article.get("author")).setHeader("Author");
        articlesGrid.addColumn(article -> article.get("description")).setHeader("Description");
        articlesGrid.setWidth("800px");

        layout.add(articlesTitle, loadArticlesButton, articlesGrid);
        return layout;
    }


    private void loadTags() {
        List<String> tags = tagService.getTags();
        tagsGrid.setItems(tags);
    }

    private void loadArticles() {
        List<Map<String, Object>> articles = articleService.getArticles(null, null, null, 20, 0);
        articlesGrid.setItems(articles);
    }

    private void loadArticlesByTag(String tag) {
        List<Map<String, Object>> articles = articleService.getArticles(tag, null, null, 20, 0);
        if (articles != null && !articles.isEmpty()) {
            articlesGrid.setItems(articles);
            Notification.show("Articles loaded for tag: " + tag);
        } else {
            Notification.show("No articles found for tag: " + tag);
        }
    }

}
