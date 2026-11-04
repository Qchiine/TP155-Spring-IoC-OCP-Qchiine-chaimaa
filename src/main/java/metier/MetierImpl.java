package metier;

import dao.IDao;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Coeur de l'application : applique une regle metier a la valeur fournie par la
 * source de donnees.
 * <p>
 * Cette classe n'est <strong>jamais</strong> modifiee pour changer de source :
 * elle ne connait que l'interface {@link IDao}, et c'est le conteneur qui
 * fournit l'implementation.
 */
@Service
public class MetierImpl implements IMetier {

    /** Coefficient metier applique a la valeur lue. */
    private static final double COEFFICIENT = 2.0;

    private final IDao sourceDeDonnees;

    public MetierImpl(IDao sourceDeDonnees) {
        this.sourceDeDonnees = sourceDeDonnees;
    }

    @PostConstruct
    void tracerInjection() {
        System.out.println("[TRACE] source injectee dans MetierImpl : " + sourceDeDonnees.libelle());
    }

    @Override
    public double calcul() {
        return sourceDeDonnees.getValue() * COEFFICIENT;
    }
}
