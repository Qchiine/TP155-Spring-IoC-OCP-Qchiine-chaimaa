package audit;

import java.util.Collections;
import java.util.List;

/**
 * Resultat d'un audit du principe Ouvert/Ferme.
 * <p>
 * Le rapport est immuable : il transporte la classe auditee et la liste des
 * manquements constates, vide lorsque le principe est respecte.
 */
public final class RapportAudit {

    private final String classeAuditee;
    private final List<String> constats;

    /**
     * @param classeAuditee nom simple de la classe inspectee
     * @param constats      manquements releves
     */
    public RapportAudit(String classeAuditee, List<String> constats) {
        this.classeAuditee = classeAuditee;
        this.constats = List.copyOf(constats);
    }

    /** @return le nom de la classe auditée */
    public String classeAuditee() {
        return classeAuditee;
    }

    /** @return les manquements constates, vide si le principe est respecte */
    public List<String> constats() {
        return Collections.unmodifiableList(constats);
    }

    /** @return vrai si aucun manquement n'a ete releve */
    public boolean conforme() {
        return constats.isEmpty();
    }

    /**
     * Rendu lisible du rapport, sur une ligne par manquement.
     *
     * @return le texte du rapport
     */
    public String rendu() {
        StringBuilder texte = new StringBuilder();
        texte.append("Audit OCP de ").append(classeAuditee).append(" : ")
                .append(conforme() ? "CONFORME" : constats.size() + " manquement(s)");
        for (String constat : constats) {
            texte.append(System.lineSeparator()).append("  - ").append(constat);
        }
        return texte.toString();
    }
}
