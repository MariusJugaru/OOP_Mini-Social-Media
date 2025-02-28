package TemaTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Utilizator {
    private String username;
    private String password;
    private List<Postare> listaPostari;
    private List<String> followers;
    private List<String> following;
    private int nrLikes;

    // Instantiere lista utilizatori
    private static final List<Utilizator> listaUtilizatori = new ArrayList<>();
    public Utilizator(String username, String password) {
        this.username = username;
        this.password = password;
        this.listaPostari = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public static List<Utilizator> getListaUtilizatori() {
        return listaUtilizatori;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public static void createUser(String[] strings) {
        if (strings.length == 1) {
            System.out.println("{ 'status' : 'error', 'message' : 'Please provide username'}");
            return;
        }
        if (strings.length == 2) {
            System.out.println("{ 'status' : 'error', 'message' : 'Please provide password'}");
            return;
        }
        String username, password;

        username = App.extractString(strings[1]);
        password = App.extractString(strings[2]);

        if (Lista.gasesteUtilizator(getListaUtilizatori(), username) == null) {
            getListaUtilizatori().add(new Utilizator(username, password));
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'User already exists'}");
            return;
        }

        System.out.println("{ 'status' : 'ok', 'message' : 'User created successfully'}");
    }

    public static Utilizator loginUser(String[] strings) {
        if (strings.length == 1 || strings.length == 2) {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return null;
        }
        String username, password;

        username = App.extractString(strings[1]);
        password = App.extractString(strings[2]);


        Utilizator user = Lista.gasesteUtilizator(Utilizator.getListaUtilizatori(), username);
        if (user == null || password.compareTo(user.getPassword()) != 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
            return null;
        }
        return user;
    }

    public void addPost(String text) {
        this.listaPostari.add(new Postare(text));
    }

    public void addPostById(String text, int postId, String date) {
        this.listaPostari.add(new Postare(text, postId, date));
    }

    public int removePostById(int postId) {
        for (Postare aux : this.listaPostari) {
            if (aux.getPostId() == postId) {
                this.listaPostari.remove(aux);
                return 1;
            }
        }

        return 0;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void addFollower(String username) {
        this.followers.add(username);
    }

    public void removeFollower(String username) {
        this.followers.remove(username);
    }

    public void addFollowing(String username) {
        this.following.add(username);
    }

    public void removeFollowing(String username) {
        this.following.remove(username);
    }

    public static void followUser(String[] strings) {
        Utilizator user = loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No username to follow was provided'}");
            return;
        }
        String followUsername = App.extractString(strings[3]);
        Utilizator followUser = Lista.gasesteUtilizator(Utilizator.getListaUtilizatori(), followUsername);

        if (followUser == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to follow was not valid'}");
            return;
        }

        if (Lista.gasesteExistentaUtilizator(user.getFollowing(), followUsername)) {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to follow was not valid'}");
            return;
        }

        user.addFollowing(followUsername);
        followUser.addFollower(user.getUsername());
        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }

    public static void unfollowUser(String[] strings) {
        Utilizator user = loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No username to unfollow was provided'}");
            return;
        }
        String unfollowUsername = App.extractString(strings[3]);
        Utilizator unfollowUser = Lista.gasesteUtilizator(Utilizator.getListaUtilizatori(), unfollowUsername);

        if (unfollowUser == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to unfollow was not valid'}");
            return;
        }

        if (!Lista.gasesteExistentaUtilizator(user.getFollowing(), unfollowUsername)) {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to unfollow was not valid'}");
            return;
        }

        user.removeFollowing(unfollowUsername);
        unfollowUser.removeFollower(user.getUsername());
        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }

    public static void getFollowing(String[] strings) {
        Utilizator user = loginUser(strings);
        if (user == null)
            return;
        List<String> listFollowing = user.getFollowing();
        System.out.print("{ 'status' : 'ok', 'message' : [ ");
        for (int i = 0; i < listFollowing.size() ; i++) {
            System.out.print("'" + listFollowing.get(i) + "'");
            if (i < listFollowing.size() - 1)
                System.out.print(", ");
        }
        System.out.println(" ]}");
    }

    public static void getFollowers(String[] strings) {
        Utilizator user = loginUser(strings);
        if (user == null)
            return;
        if (strings.length == 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No username to list followers was provided'}");
            return;
        }
        String usernameFollowers = App.extractString(strings[3]);
        Utilizator userFollowers;
        userFollowers = getUserByUsername(usernameFollowers);

        if (userFollowers == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to list followers was not valid'}");
            return;
        }

        List<String> listFollowing = userFollowers.getFollowers();
        System.out.print("{ 'status' : 'ok', 'message' : [ ");
        for (int i = 0; i < listFollowing.size() ; i++) {
            System.out.print("'" + listFollowing.get(i) + "'");
            if (i < listFollowing.size() - 1)
                System.out.print(", ");
        }
        System.out.println(" ]}");
    }

    public static void getMostFollowedUsers(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;

        List<Utilizator> listaUtilizatoriTop = new ArrayList<>(getListaUtilizatori());

        Collections.sort(listaUtilizatoriTop, (o1, o2) -> {
            if (o1.getFollowers().size() == o2.getFollowers().size())
                return 0;
            if (o1.getFollowers().size() > o2.getFollowers().size())
                return -1;
            return 1;
        });

        System.out.print("{ 'status' : 'ok', 'message' : [");
        int userNumber;
        if (listaUtilizatoriTop.size() < 5)
            userNumber = listaUtilizatoriTop.size();
        else
            userNumber = 5;
        for (int i = 0 ; i < userNumber; i++) {
            user = listaUtilizatoriTop.get(i);
            System.out.print("{'username' : '" + user.getUsername() + "','number_of_followers' : ' " + user.getFollowers().size() + "' }");
            if (i < userNumber - 1)
                System.out.print(",");
        }

        System.out.println(" ]}");
    }

    public int getNrLikes() {
        return nrLikes;
    }

    public void setNrLikes(int nrLikes) {
        this.nrLikes = nrLikes;
    }

    public static void countNrLikesUser(Utilizator user) {
        int likes = 0;
        for (Utilizator auxUser : getListaUtilizatori()) {
            for (Postare auxPost : auxUser.getPosts()) {
                if (Objects.equals(Postare.getPostParent(auxPost.getPostId()), user))
                    likes += auxPost.getLikeList().size();

                for (Comentariu auxComment : auxPost.getCommentList()) {
                    if (auxComment.getUsername().compareTo(user.getUsername()) == 0)
                        likes += auxComment.getLikes().size();
                }
            }
        }

        user.setNrLikes(likes);
    }

    public static void getMostLikedUsers(String[] strings) {
        Utilizator user = Utilizator.loginUser(strings);
        if (user == null)
            return;

        List<Utilizator> listaUtilizatoriTop = new ArrayList<>(getListaUtilizatori());

        for(Utilizator auxUser : getListaUtilizatori())
            countNrLikesUser(auxUser);

        Collections.sort(listaUtilizatoriTop, (o1, o2) -> {
            if (o1.getNrLikes() == o2.getNrLikes())
                return 0;
            if (o1.getNrLikes() > o2.getNrLikes())
                return -1;
            return 1;
        });

        System.out.print("{ 'status' : 'ok', 'message' : [");
        int userNumber;
        if (listaUtilizatoriTop.size() < 5)
            userNumber = listaUtilizatoriTop.size();
        else
            userNumber = 5;
        for (int i = 0 ; i < userNumber; i++) {
            user = listaUtilizatoriTop.get(i);
            System.out.print("{'username' : '" + user.getUsername() + "','number_of_likes' : '" + user.getNrLikes() + "' }");
            if (i < userNumber - 1)
                System.out.print(",");
        }

        System.out.println("]}");

    }

    public static Utilizator getUserByUsername(String username) {
        for (Utilizator aux : listaUtilizatori) {
            if (aux.getUsername().compareTo(username) == 0)
                return aux;
        }
        return null;
    }

    public List<Postare> getPosts() {
        return listaPostari;
    }
}
