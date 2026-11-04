package audit;

import dao.IDao;
import metier.IMetier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Controle automatise du principe Ouvert/Ferme.
 * <p>
 * Plutot que de relire le code a la main, l'audit inspecte le contrat du metier
 * par reflexion : si une classe concrete de source apparait dans les champs ou
 * dans les parametres de constructeur de {@link IMetier}, le principe est
 * enfreint, car changer de source obligerait a modifier la classe.
 */
public final class AuditPrincipeOcp {

    private AuditPrincipeOcp() {
    }

    /**
     * Verifie que le metier ne depend que d'abstractions.
     *
     * @param typeMetier classe metier a inspecter
     * @return le rapport d'audit, vide en cas de conformite
     */
    public static RapportAudit auditer(Class<? extends IMetier> typeMetier) {
        List<String> constats = new ArrayList<>();

        for (Field champ : typeMetier.getDeclaredFields()) {
            if (Modifier.isStatic(champ.getModifiers())) {
                continue;
            }
            Class<?> type = champ.getType();
            if (!type.isInterface() && !type.isPrimitive() && !type.isEnum()) {
                constats.add("champ " + champ.getName() + " : type concret " + type.getName());
            }
        }

        for (Constructor<?> constructeur : typeMetier.getDeclaredConstructors()) {
            for (Class<?> parametre : constructeur.getParameterTypes()) {
                if (!parametre.isInterface() && !parametre.isPrimitive() && !parametre.isEnum()) {
                    constats.add("constructeur : parametre concret " + parametre.getName());
                }
            }
        }

        return new RapportAudit(typeMetier.getSimpleName(), constats);
    }

    /**
     * Verifie qu'aucun nom de source concrete n'apparait dans les types declares
     * du metier, en comparant avec les implementations fournies.
     *
     * @param typeMetier  classe metier a inspecter
     * @param concretes   implementations de {@link IDao} a rechercher
     * @return les references concretes trouvees, vides si le principe tient
     */
    public static List<String> referencesConcretes(Class<? extends IMetier> typeMetier,
                                                   List<Class<? extends IDao>> concretes) {
        List<String> trouvees = new ArrayList<>();
        for (Class<?> type : typesDeclares(typeMetier)) {
            for (Class<? extends IDao> concrete : concretes) {
                if (type.equals(concrete)) {
                    trouvees.add(concrete.getSimpleName());
                }
            }
        }
        return trouvees;
    }

    private static List<Class<?>> typesDeclares(Class<?> type) {
        List<Class<?>> types = new ArrayList<>();
        for (Field champ : type.getDeclaredFields()) {
            types.add(champ.getType());
        }
        for (Constructor<?> constructeur : type.getDeclaredConstructors()) {
            types.addAll(List.of(constructeur.getParameterTypes()));
        }
        return types;
    }
}
