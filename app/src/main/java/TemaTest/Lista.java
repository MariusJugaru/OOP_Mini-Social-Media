package TemaTest;

import java.util.List;

public class Lista {
    public static Utilizator gasesteUtilizator(List<Utilizator> listaUtilizatori, String username) {
        Utilizator utilizator = null;

        for (Utilizator aux : listaUtilizatori)
            if (aux.getUsername().compareTo(username) == 0) {
                utilizator = aux;
                break;
            }

        return utilizator;
    }

    public static boolean gasesteExistentaUtilizator(List<String> listaUtilizatori, String username) {

        for (String aux : listaUtilizatori)
            if (aux.compareTo(username) == 0) {
                return true;
            }

        return false;
    }

}
