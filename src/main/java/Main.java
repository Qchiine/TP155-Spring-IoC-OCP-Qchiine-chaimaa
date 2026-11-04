import presentation.DemonstrationOcp;

/**
 * Point d'entree de l'application.
 * <p>
 * Le scenario complet est delegue a {@link DemonstrationOcp} pour garder cette
 * classe limitee au demarrage.
 */
public final class Main {

    static {
        java.io.InputStream flux = Main.class.getClassLoader()
                .getResourceAsStream("logging.properties");
        if (flux != null) {
            try (java.io.InputStream aLire = flux) {
                java.util.logging.LogManager.getLogManager().readConfiguration(aLire);
            } catch (java.io.IOException e) {
                System.err.println("Configuration de journalisation ignoree : " + e.getMessage());
            }
        }
    }

    private Main() {
    }

    /**
     * @param args arguments de la ligne de commande, ignores
     */
    public static void main(String[] args) {
        DemonstrationOcp.executer();
    }
}
