package test;

import config.StrategieProfilConfig;
import dao.DaoImplProd;
import dao.IDao;
import metier.IMetier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Verifie le mecanisme de profils : la source de production n'est declaree que
 * lorsque le profil {@code prod} est actif.
 */
@DisplayName("Selection de la source par profil Spring")
class ProfilSpringTest {

    @Test
    @DisplayName("hors profil prod, la source standard est utilisee")
    void horsProfilSourceStandard() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext()) {
            contexte.register(StrategieProfilConfig.class);
            contexte.refresh();

            assertEquals("DaoImpl", contexte.getBean(IDao.class).libelle());
            assertFalse(contexte.getBeansOfType(DaoImplProd.class).size() > 0,
                    "le bean prod ne doit pas exister hors profil");
        }
    }

    @Test
    @DisplayName("sous profil prod, la source dediee prend la main")
    void sousProfilSourceDediee() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext()) {
            contexte.getEnvironment().setActiveProfiles("prod");
            contexte.register(StrategieProfilConfig.class);
            contexte.refresh();

            assertEquals("DaoImplProd", contexte.getBean(IDao.class).libelle());
            assertEquals(100.0, contexte.getBean(IDao.class).getValue(), 0.001);
            assertEquals(200.0, contexte.getBean(IMetier.class).calcul(), 0.001);
        }
    }

    @Test
    @DisplayName("activer le profil ne change pas le code metier")
    void metierInchange() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext()) {
            contexte.getEnvironment().setActiveProfiles("prod");
            contexte.register(StrategieProfilConfig.class);
            contexte.refresh();

            assertEquals("MetierImpl",
                    contexte.getBean(IMetier.class).getClass().getSimpleName());
        }
    }
}
