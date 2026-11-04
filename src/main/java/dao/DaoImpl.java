package dao;

import org.springframework.stereotype.Component;

/**
 * Source de production : la valeur est la somme de quatre releves
 * trimestriels figes, comme le ferait un lecteur de table d'agregats.
 */
@Component("dao")
public class DaoImpl implements IDao {

    private static final double[] RELEVES_TRIMESTRIELS = {22.5, 27.0, 18.5, 32.0};

    @Override
    public double getValue() {
        double cumul = 0.0;
        for (double releve : RELEVES_TRIMESTRIELS) {
            cumul += releve;
        }
        System.out.println("[DAO] production : " + RELEVES_TRIMESTRIELS.length
                + " releves trimestriels cumules");
        return cumul;
    }
}
