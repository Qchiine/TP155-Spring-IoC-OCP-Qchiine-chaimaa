package config;

import dao.DaoApi;
import dao.DaoFile;
import dao.DaoImpl;
import dao.DaoImpl2;
import dao.IDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Strategie A : un candidat par defaut designe par {@code @Primary}.
 * <p>
 * Les quatre sources sont declarees explicitement pour garder un conteneur
 * lisible, et celle de production porte {@code @Primary} : c'est elle que
 * Spring retient des qu'une injection de {@code IDao} est demandee sans nom.
 */
@Configuration
@ComponentScan(basePackages = {"metier"})
public class StrategiePrimaireConfig {

    /**
     * Candidat par defaut : la source de production.
     *
     * @return l'implementation retenue par defaut
     */
    @Bean("dao")
    @Primary
    public IDao sourceProduction() {
        return new DaoImpl();
    }

    /**
     * Source de developpement, presente mais non prioritaire.
     *
     * @return la source de recette
     */
    @Bean("dao2")
    public IDao sourceDeveloppement() {
        return new DaoImpl2();
    }

    /**
     * Source fichier, presente mais non prioritaire.
     *
     * @return la source fichier
     */
    @Bean("daoFile")
    public IDao sourceFichier() {
        return new DaoFile();
    }

    /**
     * Source distante, presente mais non prioritaire.
     *
     * @return la source distante
     */
    @Bean("daoApi")
    public IDao sourceDistante() {
        return new DaoApi();
    }
}