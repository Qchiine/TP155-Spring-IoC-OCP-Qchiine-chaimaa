package test;

import config.RouteurConfig;
import dao.DaoApi;
import metier.IMetier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import routeur.RouteurDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifie l'apport personnel : le routeur conserve les quatre sources et permet
 * de basculer de l'une a l'autre sans reconstruire le conteneur.
 */
@DisplayName("Routeur de sources basculable a chaud")
class RouteurDaoTest {

    @Test
    @DisplayName("le routeur demarre sur la source de production")
    void demarrageSurProduction() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(RouteurConfig.class)) {
            RouteurDao routeur = contexte.getBean(RouteurDao.class);
            assertEquals("dao", routeur.cibleCourante());
            assertEquals("DaoImpl", routeur.libelle());
            assertEquals(200.0, contexte.getBean(IMetier.class).calcul(), 0.001);
        }
    }

    @Test
    @DisplayName("chaque bascule change la valeur sans toucher au metier")
    void basculeChangeLaValeur() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(RouteurConfig.class)) {
            IMetier metier = contexte.getBean(IMetier.class);
            RouteurDao routeur = contexte.getBean(RouteurDao.class);

            assertEquals(200.0, metier.calcul(), 0.001);

            routeur.basculerVers("dao2");
            assertEquals(300.0, metier.calcul(), 0.001);

            routeur.basculerVers("daoFile");
            assertEquals(360.0, metier.calcul(), 0.001);

            routeur.basculerVers("daoApi");
            assertEquals(440.0, metier.calcul(), 0.001);
        }
    }

    @Test
    @DisplayName("le retour a la source initiale restitue le premier resultat")
    void retourALaSourceInitiale() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(RouteurConfig.class)) {
            IMetier metier = contexte.getBean(IMetier.class);
            RouteurDao routeur = contexte.getBean(RouteurDao.class);

            routeur.basculerVers("daoApi");
            assertEquals(440.0, metier.calcul(), 0.001);

            routeur.basculerVers("dao");
            assertEquals(200.0, metier.calcul(), 0.001);
            assertEquals("dao", routeur.cibleCourante());
        }
    }

    @Test
    @DisplayName("une bascule vers une source inconnue est refusee")
    void basculeInconnueRefusee() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(RouteurConfig.class)) {
            RouteurDao routeur = contexte.getBean(RouteurDao.class);
            assertThrows(IllegalArgumentException.class, () -> routeur.basculerVers("daoInconnu"));
            assertEquals("dao", routeur.cibleCourante(), "la cible reste inchangee");
        }
    }

    @Test
    @DisplayName("le routeur publie les quatre sources du catalogue")
    void catalogueDuRouteur() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(RouteurConfig.class)) {
            RouteurDao routeur = contexte.getBean(RouteurDao.class);
            List<String> noms = routeur.nomsDisponibles();
            assertEquals(List.of("dao", "dao2", "daoFile", "daoApi"), noms);
        }
    }

    @Test
    @DisplayName("le routeur est lui-meme une source injectable")
    void routeurEstUneSource() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(RouteurConfig.class)) {
            assertTrue(contexte.getBean(dao.IDao.class) instanceof RouteurDao);
        }
    }

    @Test
    @DisplayName("une bascule n'instancie pas une nouvelle source a chaque lecture")
    void basculeReutiliseLaMemeInstance() {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(RouteurConfig.class)) {
            RouteurDao routeur = contexte.getBean(RouteurDao.class);
            routeur.basculerVers("daoApi");
            assertEquals(DaoApi.class.getSimpleName(), routeur.libelle());
            assertEquals(DaoApi.class.getSimpleName(), routeur.libelle());
        }
    }
}
