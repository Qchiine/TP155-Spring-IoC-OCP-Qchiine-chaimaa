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
 * Strategie B : un bean d'alias qui pointe vers l'implementation voulue.
 * <p>
 * Les classes de source restent intactes : seule la ligne de fabrication de
 * l'alias change pour basculer d'une implementation a une autre.
 */
@Configuration
@ComponentScan(basePackages = {"metier"})
public class StrategieAliasConfig {

    /**
     * Alias expose comme candidat par defaut.
     *
     * @return la source vers laquelle l'alias pointe
     */
    @Bean("sourceAlias")
    @Primary
    public IDao sourceAlias() {
        return new DaoApi();
    }

    /**
     * Source de production, presente mais non prioritaire.
     *
     * @return la source de production
     */
    @Bean("dao")
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
}