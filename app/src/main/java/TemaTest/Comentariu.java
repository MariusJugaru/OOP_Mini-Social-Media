package TemaTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Comentariu implements Likeable, Comparable<Comentariu>{
    private final String comentariu;
    private static int contor;
    private final int commentId;
    private final List<String> likes;
    private final String currentDateAsString;

    public List<String> getLikes() {
        return this.likes;
    }

    public static void setContor(int contor) {
        Comentariu.contor = contor;
    }

    private final String username;

    public String getUsername() {
        return username;
    }

    public int getCommentId() {
        return commentId;
    }


    public Comentariu(String comentariu, String username) {
        contor++;
        this.comentariu = comentariu;
        this.commentId = contor;
        this.username = username;
        this.likes = createLikeList();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        this.currentDateAsString = dateFormat.format(date);
    }

    public Comentariu(String comentariu, int commentId, String username, String date) {
        contor = commentId;
        this.comentariu = comentariu;
        this.commentId = contor;
        this.username = username;
        this.likes = createLikeList();
        this.currentDateAsString = date;
    }

    public String getCurrentDateAsString() {
        return currentDateAsString;
    }

    public String getComentariu() {
        return comentariu;
    }

    public static void addComment(String text, int postId, String username) {
        Postare post = Postare.searchForPost(postId);
        if (post == null) {
            System.out.println("Error");
            return;
        }

        post.getCommentList().add(new Comentariu(text, username));
    }

    public static void removeComment(int commentId) {
        Comentariu comment = getCommentById(commentId);
        if (comment == null) {
            System.out.println("Error");
            return;
        }

        Postare post = getPostByCommentId(commentId);
        if (post == null) {
            System.out.println("Error");
            return;
        }
        post.getCommentList().remove(comment);
    }

    public static void createComment(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3 || strings.length == 4) {
            System.out.println("{ 'status' : 'error', 'message' : 'No text provided'}");
            return;
        }
        int postId = Integer.parseInt(App.extractString(strings[3]));
        String text = App.extractString(strings[4]);
        if (text.length() > 300) {
            System.out.println("{ 'status' : 'error', 'message' : 'Comment text length exceeded'}");
            return;
        }

        addComment(text, postId, user.getUsername());
        System.out.println("{ 'status' : 'ok', 'message' : 'Comment added successfully'}");
    }

    public static void deleteComment(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No identifier was provided'}");
            return;
        }
        int commentId = Integer.parseInt(App.extractString(strings[3]));
        Utilizator commentParent = getCommentParent(commentId);
        if (commentParent == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The identifier was not valid'}");
            return;
        }

        if (commentParent == user) {
            removeComment(commentId);
            System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
            return;
        }

        Comentariu comment = getCommentById(commentId);
        if (comment == null) {
            System.out.println("Error");
            return;
        }
        if (user.getUsername().compareTo(comment.getUsername()) == 0) {
            removeComment(commentId);
            System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
            return;
        }
        System.out.println("{ 'status' : 'error', 'message' : 'The identifier was not valid'}");
    }

    public static void getMostCommentedPosts(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;

        List<Postare> listaPostari = new ArrayList<>();

        for (Utilizator auxUser : Utilizator.getListaUtilizatori()) {
            listaPostari.addAll(auxUser.getPosts());
        }

        Collections.sort(listaPostari, (o1, o2) -> {
            if (o1.getCommentList().size() == o2.getCommentList().size())
                return 0;
            if (o1.getCommentList().size() > o2.getCommentList().size())
                return -1;
            return 1;
        });

        System.out.print("{ 'status' : 'ok', 'message' : [");
        int postNumber;
        if (listaPostari.size() < 5)
            postNumber = listaPostari.size();
        else
            postNumber = 5;
        Postare post;
        for (int i = 0 ; i < postNumber; i++) {
            post = listaPostari.get(i);
            System.out.print("{'post_id' : '" + post.getPostId() +
                    "','post_text' : '" + post.getPostare() + "', 'post_date' : '"+ post.getCurrentDateAsString() +
                    "', 'username' : '" + Postare.getPostParent(post.getPostId()).getUsername() + "', 'number_of_comments' : '" + post.getCommentList().size() + "' }");
            if (i < postNumber - 1)
                System.out.print(",");
        }

        System.out.println("]}");
    }

    public static Utilizator getCommentParent(int commentId) {
        for (Utilizator user : Utilizator.getListaUtilizatori())
            for (Postare post : user.getPosts())
                for (Comentariu comment : post.getCommentList())
                    if (comment.getCommentId() == commentId)
                        return user;
        return null;
    }

    public static Comentariu getCommentById(int commentId) {
        for (Utilizator user : Utilizator.getListaUtilizatori())
            for (Postare post : user.getPosts())
                for (Comentariu comment : post.getCommentList())
                    if (comment.getCommentId() == commentId)
                        return comment;
        return null;
    }

    public static Postare getPostByCommentId(int commentId) {
        for (Utilizator user : Utilizator.getListaUtilizatori())
            for (Postare post : user.getPosts())
                for (Comentariu comment : post.getCommentList())
                    if (comment.getCommentId() == commentId)
                        return post;
        return null;
    }

    @Override
    public List<String> createLikeList() {
        return new ArrayList<>();
    }

    @Override
    public void addLike(String username) {
        this.likes.add(username);
    }

    @Override
    public void removeLike(String username) {

    }

    public static void likeComment(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No comment identifier to like was provided'}");
            return;
        }
        int commentId = Integer.parseInt(App.extractString(strings[3]));

        // Check if comment exists
        Comentariu comment = getCommentById(commentId);
        if (comment == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The comment identifier to like was not valid'}");
            return;
        }

        // Check if it is the user's comment
        if (user == getCommentParent(commentId)) {
            System.out.println("{ 'status' : 'error', 'message' : 'The comment identifier to like was not valid'}");
            return;
        }

        // Check if it is already liked
        for (String auxLikes : comment.likes) {
            if (auxLikes.compareTo(user.getUsername()) == 0) {
                System.out.println("{ 'status' : 'error', 'message' : 'The comment identifier to like was not valid'}");
                return;
            }
        }

        comment.addLike(user.getUsername());
        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }

    public static void unlikeComment(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No comment identifier to unlike was provided'}");
            return;
        }
        int commentId = Integer.parseInt(App.extractString(strings[3]));

        // Check if comment exists
        Comentariu comment = getCommentById(commentId);
        if (comment == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The comment identifier to unlike was not valid'}");
            return;
        }

        // Check if it is the user's comment
        if (user == getCommentParent(commentId)) {
            System.out.println("{ 'status' : 'error', 'message' : 'The comment identifier to unlike was not valid'}");
            return;
        }

        // Check if it is already unliked
        int ok = 0;
        for (String auxLikes : comment.likes) {
            if (auxLikes.compareTo(user.getUsername()) == 0) {
                ok = 1;
                break;
            }
        }
        if (ok == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'The comment identifier to unlike was not valid'}");
            return;
        }

        comment.removeLike(user.getUsername());
        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }

    public int compareTo(Comentariu c) {
        if (getCurrentDateAsString() == null || c.getCurrentDateAsString() == null)
            return 0;
        if (getCurrentDateAsString().compareTo(c.getCurrentDateAsString()) == 0)
            return getCommentId() > c.getCommentId() ? 1 : 0;
        return getCurrentDateAsString().compareTo(c.getCurrentDateAsString());
    }
}
