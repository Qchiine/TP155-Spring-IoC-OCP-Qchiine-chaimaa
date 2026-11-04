package test;

import config.StrategieAliasConfig;
import config.StrategiePrimaireConfig;
import dao.IDao;
import dao.DaoApi;
import dao.DaoImpl;
import metier.IMetier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static config.CatalogueSources.DEVELOPPEMENT;
import static config.CatalogueSources.PRODUCTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifie les deux strategies de selection figees : le candidat par defaut
 * designe par {@code @Primary} et l'alias cree par un bean de configuration.
 */
@DisplayName("Strategies de selection figees")
class StrategiesDeSelectionTest {

    @Test
    @DisplayName("sans candidat prioritaire, le conteneur signale l'ambiguite")
    void ambiguiteSignalee() {
        RuntimeException erreur = assertThrows(RuntimeException.class, () -> {
            try (AnnotationConfigApplicationContext contexte =
                         new AnnotationConfigApplicationContext()) {
                contexte.register(config.AppConfig.class);
                contexte.refresh();
            }
        });

        NoUniqueBeanDefinitionException ambiguite = chercherAmbiguite(erreur);
        assertTrue(ambiguite.getMessage().contains("expected single matching bean"),
                "le message doit signaler plusieurs candidats");
        assertTrue(ambiguite.getMessage().contains("dao"), "les candidats doivent etre listes");
    }

    /** Remonte la chaine des causes jusqu'a l'ambiguite de bean. */
    private static NoUniqueBeanDefinitionException chercherAmbiguite(Throwable depart) {
        Throwable courant = depart;
        while (courant != null) {
            if (courant instanceof NoUniqueBeanDefinitionException ambiguite) {
                return ambiguite;
            }
            courant = courant.getCause();
        }
        throw new AssertionError("aucune ambiguite de bean dans la chaine de causes", depart);
    }

    @Test
    @DisplayName("@Primary retient la source de production")
    void primaireRetientProduction() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(StrategiePrimaireConfig.class)) {
            assertEquals(DaoImpl.class, contexte.getBean(IDao.class).getClass());
            assertEquals(200.0, contexte.getBean(IMetier.class).calcul(), 0.001);
        }
    }

    @Test
    @DisplayName("l'alias pointe vers la source codee dans la configuration")
    void aliasPointeVersLaSourceChoisie() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(StrategieAliasConfig.class)) {
            assertEquals(DaoApi.class, contexte.getBean(IDao.class).getClass());
            assertEquals(440.0, contexte.getBean(IMetier.class).calcul(), 0.001);
        }
    }

    @Test
    @DisplayName("les deux strategies exposent quatre candidats dans le conteneur")
    void quatreCandidatsDeclares() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(StrategiePrimaireConfig.class)) {
            assertEquals(4, contexte.getBeansOfType(IDao.class).size());
            assertTrue(contexte.getBeansOfType(IDao.class).containsKey(PRODUCTION));
            assertTrue(contexte.getBeansOfType(IDao.class).containsKey(DEVELOPPEMENT));
        }
    }
}
