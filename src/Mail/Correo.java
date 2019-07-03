package Mail;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bosco Garita
 */
public class Correo {
    /**
     * Este método busca algunos patrones comunes para determinar si la
     * dirección de correo está mal formada o incluye secuencias de caracteres
     * que pueden ser detectadas como spam.
     *
     * @author Bosco Garita 15/01/2015
     * @param mail String dirección de correo a revisar
     * @return true=malformado, false=bien formado
     */
    public static boolean malformado(String mail) {
        boolean mailmalformado = false;
        mail = mail.trim();

        if (!mail.contains("@")) {
            return true;
        } // end if

        List<String> posibleSpamContent;
        posibleSpamContent = new ArrayList<>();
        posibleSpamContent.add("aa@aa");
        posibleSpamContent.add("abuse");
        posibleSpamContent.add(" ");
        posibleSpamContent.add(",");
        posibleSpamContent.add("?");
        posibleSpamContent.add("..");
        posibleSpamContent.add("<");
        posibleSpamContent.add(">");
        posibleSpamContent.add("/");
        posibleSpamContent.add("\\");
        posibleSpamContent.add(":");
        posibleSpamContent.add(".@");
        posibleSpamContent.add("..com");
        posibleSpamContent.add(" com");
        posibleSpamContent.add(". ");
        posibleSpamContent.add("à");

        for (int i = 0; i < posibleSpamContent.size(); i++) {
            if (mail.contains(posibleSpamContent.get(i))) {
                mailmalformado = true;
                break;
            } // end if
        } // end if
        // Fin Bosco modificado 05/05/2015, CR11609

        return mailmalformado;
    } // end malformado

}
