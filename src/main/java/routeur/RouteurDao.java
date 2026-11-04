package routeur;

import dao.IDao;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Routeur de sources basculable a chaud.
 * <p>
 * Les quatre strategies du TP figent la source au demarrage : {@code @Primary}
 * demande une recompilation, l'alias et la propriete demandent un redemarrage du
 * conteneur. Ce routeur conserve en memoire toutes les sources disponibles et
 * expose la source courante derriere une reference atomique : le metier appelle
 * toujours la meme interface, mais la cible peut changer pendant l'execution.
 */
public class RouteurDao implements IDao {

    private final Map<String, IDao> sources;
    private final AtomicReference<String> cibleCourante;
    private final AtomicReference<IDao> active = new AtomicReference<>();

    /**
     * @param sources        sources disponibles, indexees par nom de bean
     * @param cibleInitiale  nom de la source active au demarrage
     */
    public RouteurDao(Map<String, IDao> sources, String cibleInitiale) {
        if (sources == null || sources.isEmpty()) {
            throw new IllegalArgumentException("Le routeur exige au moins une source");
        }
        this.sources = Collections.unmodifiableMap(new LinkedHashMap<>(sources));
        this.cibleCourante = new AtomicReference<>(cibleInitiale);
        this.active.set(resoudre(cibleInitiale));
    }

    /**
     * Bascule la source active sans reconstruire le conteneur.
     *
     * @param nom nom de la source visee
     * @return la source desormais active
     * @throws IllegalArgumentException si le nom est inconnu
     */
    public IDao basculerVers(String nom) {
        IDao suivante = resoudre(nom);
        cibleCourante.set(nom);
        active.set(suivante);
        return suivante;
    }

    /** @return le nom de la source couramment active */
    public String cibleCourante() {
        return cibleCourante.get();
    }

    /** @return la liste des noms de sources disponibles, dans l'ordre de declaration */
    public java.util.List<String> nomsDisponibles() {
        return java.util.List.copyOf(sources.keySet());
    }

    @Override
    public double getValue() {
        return active.get().getValue();
    }

    @Override
    public String libelle() {
        return active.get().libelle();
    }

    private IDao resoudre(String nom) {
        IDao trouvee = sources.get(nom);
        if (trouvee == null) {
            throw new IllegalArgumentException("Source inconnue du routeur : " + nom
                    + " (disponibles : " + String.join(", ", sources.keySet()) + ")");
        }
        return trouvee;
    }
}
