package com.socialapi.socialapi.config;

import com.socialapi.socialapi.entity.Comment;
import com.socialapi.socialapi.entity.Post;
import com.socialapi.socialapi.entity.User;
import com.socialapi.socialapi.repository.CommentRepository;
import com.socialapi.socialapi.repository.PostRepository;
import com.socialapi.socialapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public DataSeeder(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = User.create("email" + i + "@socialapi.com", "name" + i);
            user.setPassword(passwordEncoder.encode("password"));
            users.add(userRepository.save(user));
        }

        for (User author : users) {
            for (int j = 1; j <= 5; j++) {
                Post post = Post.create("content " + j, author);
                postRepository.save(post);
                Collections.shuffle(users, random);
                users.stream().limit(5).forEach(post::likedBy);
                postRepository.save(post);

                for (int k = 1; k <= 10; k++) {
                    User commentAuthor = users.get(random.nextInt(users.size()));
                    Comment comment = Comment.create("Message " + k, commentAuthor, post);
                    commentRepository.save(comment);
                }
            }
        }
        System.out.println("Seed terminé : " + users.size() + " utilisateurs créés");
    }
}
