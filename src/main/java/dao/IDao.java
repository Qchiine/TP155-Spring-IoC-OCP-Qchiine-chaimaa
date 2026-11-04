package dao;

/**
 * Contrat commun a toutes les sources de donnees.
 * <p>
 * Le metier ne connait que cette interface : c'est le point de couplage faible
 * qui rend le principe Ouvert/Ferme applicable. Ajouter une source revient a
 * fournir une nouvelle implementation, sans retoucher le code metier.
 */
public interface IDao {

    /**
     * Valeur numerique agregee lue dans la source de donnees.
     *
     * @return la valeur lue
     */
    double getValue();

    /**
     * Libelle court de l'implementation, pratique pour tracer le bean elu.
     *
     * @return le nom simple de la classe
     */
    default String libelle() {
        return getClass().getSimpleName();
    }
}
