package config;

import dao.DaoImplProd;
import dao.IDao;
import metier.IMetier;
import metier.MetierImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuration du scenario par profils.
 * <p>
 * Sous le profil {@code prod}, une source dediee est enregistree et devient le
 * candidat par defaut. Hors de ce profil, seule la source standard reste
 * disponible.
 */
@Configuration
@ComponentScan(basePackages = {"metier"})
public class StrategieProfilConfig {

    /**
     * Source propre au profil prod.
     *
     * @return l'implementation reservee a la production
     */
    @Bean("daoProd")
    @Primary
    @Profile("prod")
    public IDao sourceProfil() {
        return new DaoImplProd();
    }

    /**
     * Source standard, disponible dans tous les profils.
     *
     * @return l'implementation par defaut hors production
     */
    @Bean("daoStandard")
    @Primary
    @Profile("!prod")
    public IDao sourceStandard() {
        return new dao.DaoImpl();
    }
}
