package dao;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Lecture d'un fichier de releves place dans le classpath.
 * Chaque ligne utile a la forme {@code date;capteur;valeur} ; les commentaires
 * commencant par # et la ligne d'en-tete sont ignores.
 */
@Component("daoFile")
public class DaoFile implements IDao {

    private static final String CHEMIN = "/donnees/releves.csv";

    @Override
    public double getValue() {
        double cumul = 0.0;
        int lignes = 0;

        try (InputStream flux = DaoFile.class.getResourceAsStream(CHEMIN)) {
            if (flux == null) {
                throw new IllegalStateException("Ressource absente du classpath : " + CHEMIN);
            }
            try (BufferedReader lecteur = new BufferedReader(
                    new InputStreamReader(flux, StandardCharsets.UTF_8))) {
                String ligne;
                while ((ligne = lecteur.readLine()) != null) {
                    ligne = ligne.trim();
                    if (ligne.isEmpty() || ligne.startsWith("#") || ligne.startsWith("date;")) {
                        continue;
                    }
                    cumul += Double.parseDouble(ligne.substring(ligne.lastIndexOf(';') + 1));
                    lignes++;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Lecture impossible de " + CHEMIN, e);
        }

        System.out.println("[DAO] fichier : " + lignes + " mesures importees depuis " + CHEMIN);
        return cumul;
    }
}
