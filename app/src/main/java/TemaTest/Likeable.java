package TemaTest;

import java.util.ArrayList;
import java.util.List;

public interface Likeable {
    List<String> createLikeList();

    void addLike(String username);

    void removeLike(String username);

}
