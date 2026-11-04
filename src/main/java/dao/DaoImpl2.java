package dao;

import org.springframework.stereotype.Component;

/**
 * Source de developpement : trois mesures issues d'un environnement de recette.
 * Additionnees, elles donnent la valeur attendue pour cet environnement.
 */
@Component("dao2")
public class DaoImpl2 implements IDao {

    private static final double[] MESURES_RECETTE = {42.0, 61.5, 46.5};

    @Override
    public double getValue() {
        double total = 0.0;
        for (double mesure : MESURES_RECETTE) {
            total += mesure;
        }
        System.out.println("[DAO] developpement : " + MESURES_RECETTE.length
                + " mesures de recette cumulees");
        return total;
    }
}
