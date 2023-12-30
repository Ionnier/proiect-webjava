package org.dbahrim.forum.configuration;

import lombok.RequiredArgsConstructor;
import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.data.CategoryRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.data.UserRepository;
import org.dbahrim.forum.models.Category;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class InitializationConfig {
    private final String password = "ciscoconpa55";
    private final String domain = "@cti.ro";


    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final DataSourceProperties dataSourceProperties;

    private void createUser(String password, String email, String role) {
        User user = new User(passwordEncoder.encode(password), email);
        user.setRole(role);
        userRepository.save(user);
    }

    private void createCategory(String name, String description) {
        Category category = new Category(new Category.CategoryRequestBodyPost(name, description));
        categoryRepository.save(category);
    }

    private void createPost(Long categoryId, String userMail, String title, String content) throws ErrorController.NotFoundException {
        createPost(categoryId, userMail, title, content, null);
    }

    private void createPost(Long categoryId, String userMail, String title, String content, Date date) throws ErrorController.NotFoundException {
        Category category = categoryRepository.findById(categoryId).orElseThrow(ErrorController.NotFoundException::new);
        User user = userRepository.findByEmail(userMail);
        Post post = new Post(category, user, content, title);
        post.createdAt = date;
        postRepository.save(post);
    }

    private void createComment(Long postId, String userMail, String content) throws ErrorController.NotFoundException {
        Post post = postRepository.findById(postId).orElseThrow(ErrorController.NotFoundException::new);
        User user = userRepository.findByEmail(userMail);
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setContent(content);
        comment.postId = post.id;
        post.addComment(comment);
        postRepository.save(post);
    }

    @Bean
    public CommandLineRunner dataLoader() {
        return args -> {

            String dataSourceUrl = dataSourceProperties.determineUrl();

            if (!StringUtils.hasText(dataSourceUrl) && !dataSourceUrl.contains("mem") && !dataSourceUrl.contains("h2")) {
                return;
            }

            createUser(password, SecurityConfig.ROLE_ADMIN.toLowerCase() + domain, SecurityConfig.ROLE_ADMIN);
            createUser(password, SecurityConfig.ROLE_USER.toLowerCase() + domain, SecurityConfig.ROLE_USER);

            createCategory("Technology Hub", "Explore the latest in tech innovation, gadgets, and software. Share your insights, ask for advice, and stay updated on the rapidly evolving world of technology.");
            createCategory("Health and Wellness Corner", "Discuss holistic health, fitness routines, and mental well-being. Connect with others on their wellness journeys, exchange tips, and find inspiration for a healthier lifestyle.");
            createCategory("Creative Corner", "Unleash your creativity in this space dedicated to art, writing, and design. Share your work, seek feedback, and collaborate with fellow artists to foster a vibrant and supportive creative community.");
            createCategory("Travel Enthusiasts’ Haven", "Embark on a journey of discovery with fellow globetrotters. Share travel stories, tips, and recommendations. Whether you're a seasoned traveler or a dreamer, this is your destination to explore the world.");
            createCategory("Culinary Delights", "Indulge in the world of culinary arts. Exchange recipes, discuss cooking techniques, and share your gastronomic adventures. From novice chefs to seasoned cooks, everyone is welcome to savor the flavors of this forum.");

            createPost(1L, "user@cti.ro", "Exploring the Wonders of Machine Learning",
                    "Hey everyone! Just delved into the fascinating world of machine learning. From neural networks to algorithms, it's mind-blowing. Any recommendations on beginner-friendly resources? Let's embark on this learning journey together!");
            createPost(1L, "user@cti.ro", "Gourmet Adventures: Cooking Around the Globe",
                    "Greetings fellow food enthusiasts! Recently tried my hand at a Moroccan tagine recipe. The blend of spices was exquisite. Share your favorite international recipes and let's embark on a culinary journey together!");
            createPost(1L, "user@cti.ro", "Staying Zen in a Hectic World",
                    "Hello mindful souls! Life can be chaotic, but finding inner peace is essential. What are your go-to mindfulness practices? Let's swap tips and support each other on our journey to tranquility.");

            Instant now = Instant.now();
            Date dateTwoDaysAgo = Date.from(now.minusMillis(TimeUnit.DAYS.toMillis(2)));
            Date dateEightDaysAgo = Date.from(now.minusMillis(TimeUnit.DAYS.toMillis(8)));
            Date lastMonth = Date.from(now.minusMillis(TimeUnit.DAYS.toMillis(31)));

            createPost(1L, "user@cti.ro", "Whatever2Whatever2",
                    "Whatever2Whatever2Whatever2Whatever2Whatever2Whatever2Whatever2Whatever2Whatever2Whatever2", dateTwoDaysAgo);
            createPost(1L, "user@cti.ro", "Whatever3Whatever3",
                    "Whatever3Whatever3Whatever3Whatever3Whatever3Whatever3Whatever3Whatever3Whatever3Whatever3Whatever3Whatever3", dateEightDaysAgo);
            createPost(1L, "user@cti.ro", "Whatever4Whatever4",
                    "Whatever4Whatever4Whatever4Whatever4Whatever4Whatever4Whatever4Whatever4Whatever4Whatever4Whatever4Whatever4Whatever4Whatever4", lastMonth);

            createComment(1L, "user@cti.ro", "Great choice on diving into machine learning! If you're into books, 'Hands-On Machine Learning with Scikit-Learn and TensorFlow' is a must-read.");
            createComment(1L, "user@cti.ro", "I started with online courses on platforms like Coursera and Udacity. They offer a structured approach and hands-on projects. Good luck on your journey!");
            createComment(1L, "user@cti.ro", "Machine learning is a vast field! Don't hesitate to ask questions. The community here is always ready to help. Enjoy the learning process!");
            createComment(1L, "user@cti.ro", "Moroccan tagine sounds delicious! Have you tried experimenting with other North African cuisines? I'm curious to hear about your culinary adventures!");
            createComment(1L, "admin@cti.ro", "Mindfulness is key in today's fast-paced world. I find meditation and nature walks incredibly helpful. What about you? Share your favorite mindfulness practices!");
        };
    }
}
