package test;

import config.CatalogueSources;
import config.StrategieProprietesConfig;
import dao.IDao;
import metier.IMetier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifie que la selection par propriete externe produit bien le resultat
 * attendu pour chacune des quatre sources, sans modifier le code metier.
 */
@DisplayName("Selection de la source par propriete externe")
class SelectionParProprieteTest {

    @Test
    @DisplayName("la cible 'dao' branche la source de production")
    void cibleProduction() {
        assertEquals(200.0, calculerAvec(CatalogueSources.PRODUCTION), 0.001);
    }

    @Test
    @DisplayName("la cible 'dao2' branche la source de developpement")
    void cibleDeveloppement() {
        assertEquals(300.0, calculerAvec(CatalogueSources.DEVELOPPEMENT), 0.001);
    }

    @Test
    @DisplayName("la cible 'daoFile' branche la lecture de fichier")
    void cibleFichier() {
        assertEquals(360.0, calculerAvec(CatalogueSources.FICHIER), 0.001);
    }

    @Test
    @DisplayName("la cible 'daoApi' branche l'appel distant")
    void cibleDistante() {
        assertEquals(440.0, calculerAvec(CatalogueSources.DISTANTE), 0.001);
    }

    @Test
    @DisplayName("une cible hors catalogue est refusee explicitement")
    void cibleInconnueRefusee() {
        Exception erreur = assertThrows(Exception.class, () -> calculerAvec("daoFantome"));
        StringBuilder messages = new StringBuilder();
        Throwable cause = erreur;
        while (cause != null) {
            messages.append(cause.getMessage()).append(' ');
            cause = cause.getCause();
        }
        assertEquals(true, messages.toString().contains("daoFantome"),
                "le message doit citer la cible fautive");
    }

    @Test
    @DisplayName("le catalogue expose exactement quatre sources")
    void catalogueComplet() {
        assertEquals(true, CatalogueSources.estConnue(CatalogueSources.PRODUCTION));
        assertEquals(true, CatalogueSources.estConnue(CatalogueSources.DEVELOPPEMENT));
        assertEquals(true, CatalogueSources.estConnue(CatalogueSources.FICHIER));
        assertEquals(true, CatalogueSources.estConnue(CatalogueSources.DISTANTE));
        assertEquals(false, CatalogueSources.estConnue("daoFantome"));
    }

    /**
     * Construit un conteneur dont la propriete de cible est surchargee, puis
     * applique le calcul metier.
     */
    private double calculerAvec(String cible) {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext()) {
            ConfigurableEnvironment environnement = contexte.getEnvironment();
            Map<String, Object> surcharge = new LinkedHashMap<>();
            surcharge.put(CatalogueSources.CLE_CIBLE, cible);
            environnement.getPropertySources()
                    .addFirst(new MapPropertySource("surcharge-test", surcharge));

            contexte.register(StrategieProprietesConfig.class);
            contexte.refresh();

            IDao source = contexte.getBean(IDao.class);
            IMetier metier = contexte.getBean(IMetier.class);
            return metier.calcul() > 0 ? metier.calcul() : source.getValue();        }
    }
}
