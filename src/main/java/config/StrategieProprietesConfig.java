package config;

import dao.DaoApi;
import dao.DaoFile;
import dao.DaoImpl;
import dao.DaoImpl2;
import dao.IDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

/**
 * Strategie C : la source est choisie par une propriete externe.
 * <p>
 * Le nom du bean cible est lu dans {@code app.properties} puis resolu dans le
 * catalogue. Changer de source ne demande ni recompilation, ni modification du
 * code metier : il suffit d'editer la propriete.
 */
@Configuration
@ComponentScan(basePackages = {"dao", "metier"})
@PropertySource("classpath:app.properties")
public class StrategieProprietesConfig {

    private final String cible;

    /**
     * @param cible nom de la source a brancher, lu dans le fichier de proprietes
     */
    public StrategieProprietesConfig(@Value("${" + CatalogueSources.CLE_CIBLE + ":dao}") String cible) {
        this.cible = cible;
    }

    /**
     * Fabrique la source designee par la propriete.
     *
     * @return l'implementation correspondant a la cible configuree
     */
    @Bean
    @Primary
    public IDao sourceConfiguree() {
        return instancier(cible);
    }

    /**
     * Resout un nom de source dans le catalogue.
     *
     * @param nom nom de bean demande
     * @return l'implementation associee
     * @throws IllegalArgumentException si le nom ne figure pas au catalogue
     */
    static IDao instancier(String nom) {
        switch (nom) {
            case CatalogueSources.PRODUCTION:
                return new DaoImpl();
            case CatalogueSources.DEVELOPPEMENT:
                return new DaoImpl2();
            case CatalogueSources.FICHIER:
                return new DaoFile();
            case CatalogueSources.DISTANTE:
                return new DaoApi();
            default:
                throw new IllegalArgumentException(
                        "Source inconnue : " + nom + " (attendu : dao, dao2, daoFile, daoApi)");
        }
    }
}
