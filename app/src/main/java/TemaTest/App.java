/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package TemaTest;

import java.io.*;

public class App {
    
public App() {/* compiled code */}
    private static final String postsCsv = "posts.csv";
    private static final String usersCsv = "users.csv";
    private static final String followersCsv = "followers.csv";
    private static final String followingCsv = "following.csv";
    private static final String likesPostsCsv = "likesPosts.csv";
    private static final String commentsCsv = "comments.csv";
    private static final String likesCommentsCsv = "likesComments.csv";

    public static void main(java.lang.String[] strings) {
        if (strings == null) {
            System.out.print("Hello world!");
            return;
        }

        // Load data from csv
        loadData();

        switch (strings[0]) {
            case "-cleanup-all":
                cleanData();
                return;
            case "-create-user":
                Utilizator.createUser(strings);
                break;
            case "-create-post":
                Postare.createPost(strings);
                break;
            case "-delete-post-by-id":
                Postare.deletePost(strings);
                break;
            case "-follow-user-by-username":
                Utilizator.followUser(strings);
                break;
            case "-unfollow-user-by-username":
                Utilizator.unfollowUser(strings);
                break;
            case "-like-post":
                Postare.likePost(strings);
                break;
            case "-unlike-post":
                Postare.unlikePost(strings);
                break;
            case "-comment-post":
                Comentariu.createComment(strings);
                break;
            case "-delete-comment-by-id":
                Comentariu.deleteComment(strings);
                break;
            case "-like-comment":
                Comentariu.likeComment(strings);
                break;
            case "-unlike-comment":
                Comentariu.unlikeComment(strings);
                break;
            case "-get-followings-posts":
                Postare.getFollowingsPosts(strings);
                break;
            case "-get-user-posts":
                Postare.getUserPosts(strings);
                break;
            case "-get-post-details":
                Postare.getPostDetails(strings);
                break;
            case "-get-following":
                Utilizator.getFollowing(strings);
                break;
            case "-get-followers":
                Utilizator.getFollowers(strings);
                break;
            case "-get-most-liked-posts":
                Postare.getMostLikedPosts(strings);
                break;
            case "-get-most-commented-posts":
                Comentariu.getMostCommentedPosts(strings);
                break;
            case "-get-most-followed-users":
                Utilizator.getMostFollowedUsers(strings);
                break;
            case "-get-most-liked-users":
                Utilizator.getMostLikedUsers(strings);
                break;
            default:
                System.out.println("Error, command not found");
        }

        // Save data
        saveData();
    }

    private static void cleanData() {
        deleteAllFiles();
        deleteLists();
    }

    private static void deleteLists() {
        // delete all users
        while (!Utilizator.getListaUtilizatori().isEmpty())
            Utilizator.getListaUtilizatori().remove(0);
    }

    private static void deleteFile(String fileName) {
        File myFile = new File(fileName);
        if (myFile.isFile())
            if(!myFile.delete())
                System.out.println("Delete failed");
    }

    private static void deleteAllFiles() {
        // delete user file
        deleteFile(usersCsv);

        // delete posts file
        deleteFile(postsCsv);
        Postare.setContor(0);

        // delete followers and following files
        deleteFile(followersCsv);
        deleteFile(followingCsv);

        // delete likes
        deleteFile(likesPostsCsv);
        deleteFile(likesCommentsCsv);

        // delete comments
        deleteFile(commentsCsv);
        Comentariu.setContor(0);
    }

    private static void saveData() {
        deleteAllFiles();

        String username, password, text, aux, date;
        int postId;
        for (Utilizator users : Utilizator.getListaUtilizatori()) {
            username = users.getUsername();
            password = users.getPassword();
            for (Postare posts : users.getPosts()) {
                text = posts.getPostare();
                postId = posts.getPostId();
                date = posts.getCurrentDateAsString();
                saveToCsv(postsCsv, new String[]{username, text, String.valueOf(postId), date});

                // Save post likes
                if (!posts.getLikeList().isEmpty()) {
                    aux = username + "," + postId + ",";
                    for (String auxLikes : posts.getLikeList())
                        aux += auxLikes + ",";
                    saveToCsv(likesPostsCsv, aux.split(","));
                }

                // Save comments
                if(!posts.getCommentList().isEmpty()) {
                    aux = username + "," + postId + ",";
                    for (Comentariu comments : posts.getCommentList())
                        aux += comments.getUsername() + "," + comments.getCommentId() + "," + comments.getComentariu() + "," + comments.getCurrentDateAsString() + ",";
                    saveToCsv(commentsCsv, aux.split(","));
                }

                // Save comments likes
                if(!posts.getCommentList().isEmpty()) {
                    for (Comentariu comments : posts.getCommentList()) {
                        if (!comments.getLikes().isEmpty()) {
                            aux = comments.getCommentId() + ",";
                            for (String auxLikes : comments.getLikes())
                                aux += auxLikes + ",";
                            saveToCsv(likesCommentsCsv, aux.split(","));
                        }

                    }
                }
            }
            aux = username + ",";
            for (String followers : users.getFollowers())
                aux += followers + ",";
            if (aux.compareTo(username + ",") != 0)
                saveToCsv(followersCsv, aux.split(","));

            aux = username + ",";
            for (String following: users.getFollowing())
                aux += following + ",";
            if (aux.compareTo(username + ",") != 0)
                saveToCsv(followingCsv, aux.split(","));
            saveToCsv(usersCsv, new String[]{username, password});
        }
    }

    private static void loadData() {
        deleteLists();
        String username, password, text, line, commentUsername, date;
        String[] auxSplit;
        Utilizator user;
        int postId, commentId;

        try (BufferedReader br = new BufferedReader(new FileReader(usersCsv))) {
            while ((line = br.readLine()) != null) {
                username = line.substring(0, line.indexOf(","));
                password = line.substring(line.indexOf(",") + 1);

                Utilizator.getListaUtilizatori().add(new Utilizator(username, password));
            }
        } catch (IOException ignored) {}

        try (BufferedReader br = new BufferedReader(new FileReader(postsCsv))) {
            while ((line = br.readLine()) != null) {
                auxSplit = line.split(",");
                username = auxSplit[0];
                text = auxSplit[1];
                postId = Integer.parseInt(auxSplit[2]);
                date = auxSplit[3];

                for (Utilizator aux : Utilizator.getListaUtilizatori()) {
                    if (aux.getUsername().compareTo(username) == 0) {
                        aux.addPostById(text, postId, date);
                        break;
                    }
                }
            }
        } catch (IOException ignored) {}

        try (BufferedReader br = new BufferedReader(new FileReader(followingCsv))) {
            while ((line = br.readLine()) != null) {
                auxSplit = line.split(",");
                username = auxSplit[0];
                user = Lista.gasesteUtilizator(Utilizator.getListaUtilizatori(), username);
                for (int i = 1; i < auxSplit.length; i ++)
                    user.addFollowing(auxSplit[i]);
            }
        } catch (IOException ignored) {}

        try (BufferedReader br = new BufferedReader(new FileReader(followersCsv))) {
            while ((line = br.readLine()) != null) {
                auxSplit = line.split(",");
                username = auxSplit[0];
                user = Lista.gasesteUtilizator(Utilizator.getListaUtilizatori(), username);
                for (int i = 1; i < auxSplit.length; i ++)
                    user.addFollower(auxSplit[i]);
            }
        } catch (IOException ignored) {}

        try (BufferedReader br = new BufferedReader(new FileReader(likesPostsCsv))) {
            while ((line = br.readLine()) != null) {
                auxSplit = line.split(",");
                username = auxSplit[0];
                postId = Integer.parseInt(auxSplit[1]);
                user = Lista.gasesteUtilizator(Utilizator.getListaUtilizatori(), username);
                for (Postare post : user.getPosts()) {
                    if (post.getPostId() == postId) {
                        for (int i = 2; i < auxSplit.length; i++)
                            post.addLike(auxSplit[i]);
                        break;
                    }
                }
            }
        } catch (IOException ignored) {}

        try (BufferedReader br = new BufferedReader(new FileReader(commentsCsv))) {
            while ((line = br.readLine()) != null) {
                auxSplit = line.split(",");
                username = auxSplit[0];
                commentId = Integer.parseInt(auxSplit[1]);

                Postare post = Postare.searchForPost(commentId);
                if (post == null) {
                    System.out.println("Error");
                    return;
                }
                for (int i = 2; i < auxSplit.length; i += 4) {
                    commentUsername = auxSplit[i];
                    commentId = Integer.parseInt(auxSplit[i + 1]);
                    text = auxSplit[i + 2];
                    date = auxSplit[i + 3];
                    post.getCommentList().add(new Comentariu(text, commentId, commentUsername, date));
                }
            }
        } catch (IOException ignored) {}

        try (BufferedReader br = new BufferedReader(new FileReader(likesCommentsCsv))) {
            while ((line = br.readLine()) != null) {
                auxSplit = line.split(",");
                commentId = Integer.parseInt(auxSplit[0]);

                Comentariu comment = Comentariu.getCommentById(commentId);
                if (comment == null) {
                    System.out.println("Error");
                    return;
                }
                for (int i = 1; i < auxSplit.length; i++) {
                    username = auxSplit[i];
                    comment.addLike(username);
                }
            }
        } catch (IOException ignored) {}
    }

    private static void saveToCsv(String file, String[] strings) {
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            if (strings == null) {
                System.out.println("Failed to write to file");
                return;
            }

            int i = 0;
            while (i < strings.length - 1) {
                out.print(strings[i] + ",");
                i++;
            }
            out.println(strings[i]);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractString(String string) {
        String retString;
        retString = string.substring(string.indexOf("'") + 1);
        retString = retString.substring(0, retString.length() - 1);
        return retString;
    }
}