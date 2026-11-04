package test;

import audit.AuditPrincipeOcp;
import audit.RapportAudit;
import dao.DaoApi;
import dao.DaoFile;
import dao.DaoImpl;
import dao.DaoImpl2;
import metier.IMetier;
import metier.MetierImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifie l'audit automatise du principe Ouvert/Ferme : le metier ne doit citer
 * aucune classe concrete de source.
 */
@DisplayName("Audit automatise du principe OCP")
class AuditPrincipeOcpTest {

    private static final List<Class<? extends dao.IDao>> SOURCES_CONCRETES =
            List.of(DaoImpl.class, DaoImpl2.class, DaoFile.class, DaoApi.class);

    @Test
    @DisplayName("le metier reel est declare conforme")
    void metierConforme() {
        RapportAudit rapport = AuditPrincipeOcp.auditer(MetierImpl.class);
        assertTrue(rapport.conforme(), rapport.rendu());
        assertEquals("MetierImpl", rapport.classeAuditee());
        assertTrue(rapport.constats().isEmpty());
    }

    @Test
    @DisplayName("le rendu du rapport annonce la conformite")
    void renduAnnonceConformite() {
        String rendu = AuditPrincipeOcp.auditer(MetierImpl.class).rendu();
        assertTrue(rendu.contains("CONFORME"), rendu);
        assertTrue(rendu.contains("MetierImpl"), rendu);
    }

    @Test
    @DisplayName("aucune source concrete n'apparait dans les types du metier")
    void aucuneReferenceConcrete() {
        assertTrue(AuditPrincipeOcp.referencesConcretes(MetierImpl.class, SOURCES_CONCRETES).isEmpty());
    }

    @Test
    @DisplayName("une classe metier couplee a une source concrete est demasquee")
    void metierCoupleDemasque() {
        RapportAudit rapport = AuditPrincipeOcp.auditer(MetierCouple.class);
        assertFalse(rapport.conforme(), "le champ concret doit etre signale");
        assertTrue(rapport.rendu().contains("DaoImpl"), rapport.rendu());
    }

    @Test
    @DisplayName("la reference concrete explicite est elle aussi detectee")
    void referenceConcreteDetectee() {
        List<String> trouvees = AuditPrincipeOcp.referencesConcretes(MetierCouple.class, SOURCES_CONCRETES);
        assertEquals(List.of("DaoImpl"), trouvees);
    }

    /** Contre-exemple volontaire : ce metier depend d'une implementation concrete. */
    static class MetierCouple implements IMetier {

        private final DaoImpl source = new DaoImpl();

        @Override
        public double calcul() {
            return source.getValue() * 2.0;
        }
    }
}
