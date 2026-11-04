package config;

/**
 * Noms des sources disponibles, avec leur valeur attendue.
 * <p>
 * Centraliser ce catalogue evite d'ecrire des chaines de caracteres en dur
 * partout et donne un point unique a completer quand une source s'ajoute.
 */
public final class CatalogueSources {

    /** Cle de propriete qui designe la source active. */
    public static final String CLE_CIBLE = "dao.target";

    /** Source de production. */
    public static final String PRODUCTION = "dao";

    /** Source de developpement. */
    public static final String DEVELOPPEMENT = "dao2";

    /** Source fichier. */
    public static final String FICHIER = "daoFile";

    /** Source distante. */
    public static final String DISTANTE = "daoApi";

    private CatalogueSources() {
    }

    /**
     * Indique si le nom fourni correspond a une source connue.
     *
     * @param nom nom de bean
     * @return vrai si la source fait partie du catalogue
     */
    public static boolean estConnue(String nom) {
        return PRODUCTION.equals(nom)
                || DEVELOPPEMENT.equals(nom)
                || FICHIER.equals(nom)
                || DISTANTE.equals(nom);
    }
}
