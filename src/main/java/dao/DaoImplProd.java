package dao;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Variante active uniquement sous le profil "prod".
 * <p>
 * Le bean n'existe dans le conteneur que si le profil correspondant est active,
 * ce qui evite de le compter parmi les candidats dans les autres scenarios.
 */
@Component("daoProd")
@Profile("prod")
public class DaoImplProd implements IDao {

    private static final double[] CHARGES_FIXES = {55.0, 20.0, 25.0};

    @Override
    public double getValue() {
        double total = 0.0;
        for (double charge : CHARGES_FIXES) {
            total += charge;
        }
        System.out.println("[DAO] profil prod : " + CHARGES_FIXES.length + " postes de charges");
        return total;
    }
}
