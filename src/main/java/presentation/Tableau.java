package presentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Rendu d'un tableau texte a bordures simples.
 * <p>
 * Les colonnes prennent la largeur de leur contenu le plus long, ce qui evite
 * d'aligner les espaces a la main dans chaque affichage.
 */
public final class Tableau {

    private final List<String> entetes;
    private final List<List<String>> lignes = new ArrayList<>();

    /**
     * @param entetes titres des colonnes
     */
    public Tableau(String... entetes) {
        this.entetes = List.of(entetes);
    }

    /**
     * Ajoute une ligne de valeurs.
     *
     * @param valeurs une valeur par colonne
     * @return le tableau, pour enchainer les ajouts
     */
    public Tableau ligne(Object... valeurs) {
        if (valeurs.length != entetes.size()) {
            throw new IllegalArgumentException("Attendu " + entetes.size()
                    + " valeurs, recu " + valeurs.length);
        }
        List<String> cellules = new ArrayList<>(valeurs.length);
        for (Object valeur : valeurs) {
            cellules.add(valeur == null ? "" : String.valueOf(valeur));
        }
        lignes.add(cellules);
        return this;
    }

    /**
     * Produit le rendu complet, bordures comprises.
     *
     * @return le tableau sous forme de texte multi-lignes
     */
    public String rendu() {
        int[] largeurs = new int[entetes.size()];
        for (int i = 0; i < entetes.size(); i++) {
            largeurs[i] = entetes.get(i).length();
        }
        for (List<String> ligne : lignes) {
            for (int i = 0; i < ligne.size(); i++) {
                largeurs[i] = Math.max(largeurs[i], ligne.get(i).length());
            }
        }

        StringBuilder texte = new StringBuilder();
        texte.append(separation(largeurs, '┌', '┬', '┐')).append(System.lineSeparator());
        texte.append(cellule(entetes, largeurs)).append(System.lineSeparator());
        texte.append(separation(largeurs, '├', '┼', '┤')).append(System.lineSeparator());
        for (List<String> ligne : lignes) {
            texte.append(cellule(ligne, largeurs)).append(System.lineSeparator());
        }
        texte.append(separation(largeurs, '└', '┴', '┘'));
        return texte.toString();
    }

    private static String cellule(List<String> valeurs, int[] largeurs) {
        StringBuilder ligne = new StringBuilder("│");
        for (int i = 0; i < valeurs.size(); i++) {
            ligne.append(' ').append(valeurs.get(i));
            ligne.append(" ".repeat(largeurs[i] - valeurs.get(i).length())).append(" │");
        }
        return ligne.toString();
    }

    private static String separation(int[] largeurs, char gauche, char milieu, char droite) {
        StringBuilder ligne = new StringBuilder().append(gauche);
        for (int i = 0; i < largeurs.length; i++) {
            ligne.append("─".repeat(largeurs[i] + 2));
            ligne.append(i == largeurs.length - 1 ? droite : milieu);
        }
        return ligne.toString();
    }
}
