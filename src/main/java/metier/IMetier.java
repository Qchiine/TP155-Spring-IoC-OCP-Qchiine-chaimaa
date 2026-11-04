package metier;

/**
 * Contrat du metier. Aucune implementation de {@link dao.IDao} n'apparait ici :
 * c'est le conteneur qui decide de la source a brancher.
 */
public interface IMetier {

    /**
     * Applique la regle metier a la valeur lue dans la source de donnees.
     *
     * @return le resultat du calcul
     */
    double calcul();
}
