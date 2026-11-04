package presentation;

/**
 * Mise en forme des sorties console : titres d'etape et encadre de fin.
 */
public final class Affichage {

    private static final String LIGNE = "=".repeat(78);

    private Affichage() {
    }

    /**
     * Affiche un titre d'etape.
     *
     * @param numero numero de l'etape
     * @param titre  intitule de l'etape
     */
    public static void etape(int numero, String titre) {
        System.out.println();
        System.out.println(LIGNE);
        System.out.println(" ETAPE " + numero + " - " + titre);
        System.out.println(LIGNE);
    }

    /**
     * Affiche une ligne d'information indented.
     *
     * @param message texte a afficher
     */
    public static void info(String message) {
        System.out.println("  " + message);
    }

    /** Affiche une ligne vide. */
    public static void espace() {
        System.out.println();
    }

    /**
     * Encadre un titre de bandeau.
     *
     * @param lignes lignes du bandeau
     */
    public static void bandeau(String... lignes) {
        System.out.println("+" + "-".repeat(74) + "+");
        for (String ligne : lignes) {
            System.out.println("| " + ligne + " ".repeat(Math.max(0, 73 - ligne.length())) + "|");
        }
        System.out.println("+" + "-".repeat(74) + "+");
    }
}
