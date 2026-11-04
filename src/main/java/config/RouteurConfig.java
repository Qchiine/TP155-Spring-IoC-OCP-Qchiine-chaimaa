package config;

import dao.IDao;
import dao.DaoApi;
import dao.DaoFile;
import dao.DaoImpl;
import dao.DaoImpl2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import routeur.RouteurDao;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Declare le routeur de sources comme bean du conteneur.
 * <p>
 * Les quatre implementations sont enregistrees sous leur nom de catalogue, puis
 * confiees au routeur. Le metier recoit {@link RouteurDao} : il ne sait pas que
 * la source peut changer, et pourtant elle le peut.
 */
@Configuration
@ComponentScan(basePackages = {"metier"})
public class RouteurConfig {

    /**
     * Source routeuse, exposee comme candidat par defaut.
     *
     * @return le routeur pret a l'emploi
     */
    @Bean
    @Primary
    public IDao routeurDao() {
        Map<String, IDao> sources = new LinkedHashMap<>();
        sources.put(CatalogueSources.PRODUCTION, new DaoImpl());
        sources.put(CatalogueSources.DEVELOPPEMENT, new DaoImpl2());
        sources.put(CatalogueSources.FICHIER, new DaoFile());
        sources.put(CatalogueSources.DISTANTE, new DaoApi());
        return new RouteurDao(sources, CatalogueSources.PRODUCTION);
    }
}
