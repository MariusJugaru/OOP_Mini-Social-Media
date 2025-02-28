package TemaTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Postare implements Likeable, Comparable<Postare>{
    private String postare;
    private static int contor;
    private int postId;
    private final List<String> likes;
    private final List<Comentariu> comentarii = new ArrayList<>();

    private final String currentDateAsString;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public static void setContor(int contor) {
        Postare.contor = contor;
    }

    public String getCurrentDateAsString() {
        return currentDateAsString;
    }

    public Postare(String postare) {
        this.postare = postare;
        contor++;
        this.postId = contor;
        this.likes = createLikeList();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        this.currentDateAsString = dateFormat.format(date);
    }

    public Postare(String postare, int postId, String date) {
        this.postare = postare;
        contor = postId;
        this.postId = contor;
        this.likes = createLikeList();
        this.currentDateAsString = date;
    }

    public String getPostare() {
        return postare;
    }

    public List<Comentariu> getCommentList() {
        return this.comentarii;
    }

    public static void createPost(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No text provided'}");
            return;
        }
        String text = App.extractString(strings[3]);
        if (text.length() > 300) {
            System.out.println("{ 'status' : 'error', 'message' : 'Post text length exceeded'}");
            return;
        }

        user.addPost(text);
        System.out.println("{ 'status' : 'ok', 'message' : 'Post added successfully'}");
    }

    public static void deletePost(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No identifier was provided'}");
            return;
        }
        String auxString = App.extractString(strings[3]);
        int postId = Integer.parseInt(auxString);

        if (user.removePostById(postId) == 1) {
            System.out.println("{ 'status' : 'ok', 'message' : 'Post deleted successfully'}");
            return;
        }
        System.out.println("{ 'status' : 'error', 'message' : 'The identifier was not valid'}");
    }

    // Likeable
    @Override
    public List<String> createLikeList() {
        return new ArrayList<>();
    }

    @Override
    public void addLike(String username) {
        this.likes.add(username);
    }

    public void removeLike(String username) {
        this.likes.remove(username);
    }

    public List<String> getLikeList() {
        return this.likes;
    }

    public static void likePost(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No post identifier to like was provided'}");
            return;
        }
        int postId = Integer.parseInt(App.extractString(strings[3]));

        // Check if post exists
        Postare post = searchForPost(postId);
        if (post == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to like was not valid'}");
            return;
        }

        // Check if it is the user's post
        if (user == getPostParent(postId)) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to like was not valid'}");
            return;
        }

        // Check if it is already liked
        for (String auxLikes : post.likes) {
            if (auxLikes.compareTo(user.getUsername()) == 0) {
                System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to like was not valid'}");
                return;
            }
        }

        post.addLike(user.getUsername());
        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }

    public static void unlikePost(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No post identifier to unlike was provided'}");
            return;
        }
        int postId = Integer.parseInt(App.extractString(strings[3]));

        // Check if post exists
        Postare post = searchForPost(postId);
        if (post == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to unlike was not valid'}");
            return;
        }

        // Check if it is the user's post
        if (user == getPostParent(postId)) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to like was not valid'}");
            return;
        }

        // Check if it is already unliked
        int ok = 0;
        for (String auxLikes : post.likes) {
            if (auxLikes.compareTo(user.getUsername()) == 0) {
                ok = 1;
                break;
            }
        }
        if (ok == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to unlike was not valid'}");
            return;
        }

        post.removeLike(user.getUsername());
        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }

    public static Postare searchForPost(int postId) {
        for (Utilizator utilizator : Utilizator.getListaUtilizatori()) {
            for (Postare post : utilizator.getPosts()) {
                if (post.getPostId() == postId) {
                    return post;
                }
            }
        }
        return null;
    }

        public static Utilizator getPostParent(int postId) {
        for (Utilizator utilizator : Utilizator.getListaUtilizatori()) {
            for (Postare post : utilizator.getPosts()) {
                if (post.getPostId() == postId) {
                    return utilizator;
                }
            }
        }
        return null;
    }

    public static void getFollowingsPosts(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;

        List<Postare> listaPostari = new ArrayList<>();

        Utilizator followingUser;
        for (String auxUsername : user.getFollowing()) {
            followingUser = Utilizator.getUserByUsername(auxUsername);
            if (followingUser == null) {
                System.out.println("Error");
                return;
            }

            listaPostari.addAll(followingUser.getPosts());
        }

        Collections.sort(listaPostari);
        Collections.reverse(listaPostari);

        Postare posts;

        System.out.print("{ 'status' : 'ok', 'message' : [");
        for (int i = 0; i < listaPostari.size(); i++) {
            posts = listaPostari.get(i);
            System.out.print("{'post_id' : '"+ posts.getPostId() + "', 'post_text' : '" + posts.getPostare() +
                    "', 'post_date' : '" + posts.getCurrentDateAsString() + "', 'username' : '" + getPostParent(posts.getPostId()).getUsername() + "'}");
            if (i < listaPostari.size() - 1)
                System.out.print(",");
        }
        System.out.println("]}");
    }

    public static void getUserPosts(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No username to list posts was provided'}");
            return;
        }
        String followingUsername = App.extractString(strings[3]);

        // Check if own post
        if (followingUsername.compareTo(user.getUsername()) != 0) {
            // Check if following
            if (!checkIfFollowing(user, followingUsername)) return;
        }

        Utilizator followingUser = Utilizator.getUserByUsername(followingUsername);
        if (followingUser == null) {
            System.out.println("Error");
            return;
        }
        List<Postare> listaPostari = new ArrayList<>(followingUser.getPosts());

        Collections.sort(listaPostari);
        Collections.reverse(listaPostari);

        Postare posts;
        System.out.print("{ 'status' : 'ok', 'message' : [");
        for (int i = 0; i < listaPostari.size(); i++) {
            posts = listaPostari.get(i);
            System.out.print("{'post_id' : '"+ posts.getPostId() + "', 'post_text' : '" + posts.getPostare() +
                    "', 'post_date' : '" + posts.getCurrentDateAsString() + "'}");
            if (i < listaPostari.size() - 1)
                System.out.print(",");
        }
        System.out.println("]}");
    }

    public static void getPostDetails(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No post identifier was provided'}");
            return;
        }
        int postId = Integer.parseInt(App.extractString(strings[3]));
        Utilizator followingUser = getPostParent(postId);
        if (followingUser == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier was not valid'}");
            return;
        }
        String followingUsername = followingUser.getUsername();

        // Check if own post
        if (followingUsername.compareTo(user.getUsername()) != 0) {
            // Check if following
            if (!checkIfFollowing(user, followingUsername)) return;
        }

        Postare post = searchForPost(postId);
        System.out.print("{'status' : 'ok', 'message' : [{'post_text' : '" + post.getPostare() + "', 'post_date' :'" +
                post.getCurrentDateAsString() + "', 'username' : '" + followingUsername +"', 'number_of_likes' :" +
                " '" + post.getLikeList().size() + "', 'comments' : [");

        if (post.getCommentList() == null) {
            System.out.println("] }] }");
            return;
        }

        List<Comentariu> listaComentarii = new ArrayList<>(searchForPost(postId).getCommentList());

        Collections.sort(listaComentarii);
        Collections.reverse(listaComentarii);

        Comentariu comments;
        for (int i = 0; i < listaComentarii.size(); i++) {
            comments = listaComentarii.get(i);
            System.out.print("{'comment_id' : '" + comments.getCommentId() + "' ," +
                    " 'comment_text' : '" + comments.getComentariu() + "', 'comment_date' : '" + comments.getCurrentDateAsString() + "', " +
                    "'username' : '" + Comentariu.getCommentById(comments.getCommentId()).getUsername() + "', 'number_of_likes' : '" + comments.getLikes().size() + "'}");
            if (i < listaComentarii.size() - 1)
                System.out.print(",");
        }
        System.out.println("] }] }");
    }

    public static void getMostLikedPosts(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;

        List<Postare> listaPostari = new ArrayList<>();

        for (Utilizator auxUser : Utilizator.getListaUtilizatori()) {
            listaPostari.addAll(auxUser.getPosts());
        }

        Collections.sort(listaPostari, (o1, o2) -> {
            if (o1.getLikeList().size() == o2.getLikeList().size())
                return 0;
            if (o1.getLikeList().size() > o2.getLikeList().size())
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
                    "', 'username' : '" + getPostParent(post.getPostId()).getUsername() + "', 'number_of_likes' : '" + post.getLikeList().size() + "' }");
            if (i < postNumber - 1)
                System.out.print(",");
        }

        System.out.println(" ]}");
    }

    private static boolean checkIfFollowing(Utilizator user, String followingUsername) {
        int ok = 0;
        for (String auxFollowing : user.getFollowing())
            if (auxFollowing.compareTo(followingUsername) == 0) {
                ok = 1;
                break;
            }
        if (ok == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to list posts was not valid'}");
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Postare p) {
        if (getCurrentDateAsString() == null || p.getCurrentDateAsString() == null)
            return 0;
        if (getCurrentDateAsString().compareTo(p.getCurrentDateAsString()) == 0)
            return getPostId() > p.getPostId() ? 1 : 0;
        return getCurrentDateAsString().compareTo(p.getCurrentDateAsString());
    }
}
