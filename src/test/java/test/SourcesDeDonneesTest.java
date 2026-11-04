package test;

import config.CatalogueSources;
import dao.DaoApi;
import dao.DaoFile;
import dao.DaoImpl;
import dao.DaoImpl2;
import dao.IDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifie que chaque source renvoie la valeur attendue par l'enonce
 * (100, 150, 180, 220) et que le contrat commun reste homogene.
 */
@DisplayName("Contrat des sources de donnees")
class SourcesDeDonneesTest {

    @Test
    @DisplayName("la source de production vaut 100")
    void productionVautCent() {
        assertEquals(100.0, new DaoImpl().getValue(), 0.001);
    }

    @Test
    @DisplayName("la source de developpement vaut 150")
    void developpementVautCentCinquante() {
        assertEquals(150.0, new DaoImpl2().getValue(), 0.001);
    }

    @Test
    @DisplayName("la source fichier vaut 180")
    void fichierVautCentQuatreVingts() {
        assertEquals(180.0, new DaoFile().getValue(), 0.001);
    }

    @Test
    @DisplayName("la source distante vaut 220")
    void distanteVautDeuxCentVingt() {
        assertEquals(220.0, new DaoApi().getValue(), 0.001);
    }

    @Test
    @DisplayName("chaque source annonce son propre libelle")
    void libellesDistincts() {
        IDao[] sources = {new DaoImpl(), new DaoImpl2(), new DaoFile(), new DaoApi()};
        for (IDao source : sources) {
            assertEquals(source.getClass().getSimpleName(), source.libelle());
        }
    }

    @Test
    @DisplayName("les noms de beans correspondent au catalogue")
    void nomsConformesAuCatalogue() {
        assertTrue(CatalogueSources.estConnue(CatalogueSources.PRODUCTION));
        assertTrue(CatalogueSources.estConnue(CatalogueSources.DEVELOPPEMENT));
        assertTrue(CatalogueSources.estConnue(CatalogueSources.FICHIER));
        assertTrue(CatalogueSources.estConnue(CatalogueSources.DISTANTE));
    }
}
