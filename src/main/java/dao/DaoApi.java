package dao;

import org.springframework.stereotype.Component;

/**
 * Appel a un service distant : le transport est simule par une latence, et la
 * valeur est reconstituee a partir d'une charge utile JSON (montant de base
 * augmente des frais de dossier).
 */
@Component("daoApi")
public class DaoApi implements IDao {

    private static final long LATENCE_SIMULEE_MS = 120L;
    private static final String CHARGE_UTILE = "{\"montantBase\":190.0,\"fraisDossier\":30.0}";

    @Override
    public double getValue() {
        long debut = System.nanoTime();
        try {
            Thread.sleep(LATENCE_SIMULEE_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Appel distant interrompu", e);
        }
        long duree = (System.nanoTime() - debut) / 1_000_000L;

        double montantBase = extraire(CHARGE_UTILE, "montantBase");
        double fraisDossier = extraire(CHARGE_UTILE, "fraisDossier");
        System.out.println("[DAO] service distant : reponse en " + duree
                + " ms, montant " + montantBase + " + frais " + fraisDossier);
        return montantBase + fraisDossier;
    }

    /** Extraction sans dependance externe : on isole la valeur numerique qui suit la cle. */
    private static double extraire(String json, String cle) {
        int positionCle = json.indexOf('"' + cle + '"');
        if (positionCle < 0) {
            throw new IllegalStateException("Cle absente de la reponse : " + cle);
        }
        int debutValeur = json.indexOf(':', positionCle) + 1;
        int finValeur = debutValeur;
        while (finValeur < json.length() && (Character.isDigit(json.charAt(finValeur))
                || json.charAt(finValeur) == '.' || json.charAt(finValeur) == '-')) {
            finValeur++;
        }
        return Double.parseDouble(json.substring(debutValeur, finValeur));
    }
}
