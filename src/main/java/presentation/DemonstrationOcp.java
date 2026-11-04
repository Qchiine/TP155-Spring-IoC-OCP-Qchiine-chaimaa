package presentation;

import config.AppConfig;
import config.CatalogueSources;
import config.RouteurConfig;
import config.StrategieAliasConfig;
import config.StrategiePrimaireConfig;
import config.StrategieProfilConfig;
import config.StrategieProprietesConfig;
import dao.DaoApi;
import dao.DaoFile;
import dao.DaoImpl;
import dao.DaoImpl2;
import dao.IDao;
import metier.IMetier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import routeur.RouteurDao;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Deroulement commente du TP : ambiguite, trois strategies de selection, profils,
 * puis les deux apports personnels (routeur a chaud et audit du principe OCP).
 */
public final class DemonstrationOcp {

    private DemonstrationOcp() {
    }

    /**
     * Execute les sept etapes du scenario.
     */
    public static void executer() {
        Affichage.bandeau(
                "TP 3 - SPRING IoC ET PRINCIPE OUVERT/FERME",
                "Selection d'implementation de DAO sans toucher au code metier",
                "Spring 5.3.30 - JDK 17 - projet Maven",
                "realise par Qchiine Chaimaa");

        etape1Ambiguite();
        etape2Primaire();
        etape3Alias();
        etape4Proprietes();
        etape5Profils();
        etape6Routeur();
        etape7Audit();

        Affichage.espace();
        Affichage.info("Scenario termine : le code metier n'a jamais ete modifie.");
    }

    private static void etape1Ambiguite() {
        Affichage.etape(1, "Ambiguite : quatre candidats pour une seule interface");
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            contexte.getBean(IMetier.class);
            Affichage.info("aucune exception : la configuration tranche deja le choix");
        } catch (RuntimeException e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            Affichage.info("exception levee : " + cause.getClass().getSimpleName());
            Affichage.info("message : " + cause.getMessage());
            Affichage.info("Spring refuse de deviner : c'est exactement le point a resoudre.");
            Affichage.info("quatre candidats vivent dans le conteneur, aucun n'est prioritaire.");
        }
    }

    private static void etape2Primaire() {
        Affichage.etape(2, "Strategie A - @Primary : un candidat par defaut");
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(StrategiePrimaireConfig.class)) {
            IMetier metier = contexte.getBean(IMetier.class);
            double resultat = metier.calcul();
            Affichage.info("source retenue : " + contexte.getBean(IDao.class).libelle());
            Affichage.info("resultat du calcul : " + resultat);
            Affichage.info("limite : changer de source impose de recompiler la configuration.");
        }
    }

    private static void etape3Alias() {
        Affichage.etape(3, "Strategie B - alias via @Bean : un pointeur intermediaire");
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(StrategieAliasConfig.class)) {
            IMetier metier = contexte.getBean(IMetier.class);
            double resultat = metier.calcul();
            Affichage.info("source retenue : " + contexte.getBean(IDao.class).libelle());
            Affichage.info("resultat du calcul : " + resultat);
            Affichage.info("avantage : la ligne de fabrication change, pas les classes de source.");
        }
    }

    private static void etape4Proprietes() {
        Affichage.etape(4, "Strategie C - propriete externe : aucune recompilation");

        Tableau tableau = new Tableau("CIBLE", "SOURCE REELLE", "RESULTAT");
        for (String cible : new String[]{
                CatalogueSources.PRODUCTION, CatalogueSources.DEVELOPPEMENT,
                CatalogueSources.FICHIER, CatalogueSources.DISTANTE}) {

            try (AnnotationConfigApplicationContext contexte =
                         new AnnotationConfigApplicationContext()) {
                ConfigurableEnvironment environnement = contexte.getEnvironment();
                Map<String, Object> surcharge = new LinkedHashMap<>();
                surcharge.put(CatalogueSources.CLE_CIBLE, cible);
                environnement.getPropertySources()
                        .addFirst(new MapPropertySource("surcharge-demo", surcharge));

                contexte.register(StrategieProprietesConfig.class);
                contexte.refresh();

                IMetier metier = contexte.getBean(IMetier.class);
                tableau.ligne(cible, contexte.getBean(IDao.class).libelle(), metier.calcul());
            }
        }
        System.out.println(tableau.rendu());
        Affichage.info("une seule propriete suffit : dao.target=<nom de la source>.");
    }

    private static void etape5Profils() {
        Affichage.etape(5, "Profils Spring : la source n'existe que sous 'prod'");

        try (AnnotationConfigApplicationContext sansProfil =
                     new AnnotationConfigApplicationContext()) {
            sansProfil.register(StrategieProfilConfig.class);
            sansProfil.refresh();
            Affichage.info("profil par defaut : " + sansProfil.getBean(IDao.class).libelle()
                    + " (bean prod absent du conteneur)");
        }

        try (AnnotationConfigApplicationContext avecProfil =
                     new AnnotationConfigApplicationContext()) {
            avecProfil.getEnvironment().setActiveProfiles("prod");
            avecProfil.register(StrategieProfilConfig.class);
            avecProfil.refresh();
            Affichage.info("profil 'prod' active : " + avecProfil.getBean(IDao.class).libelle());
            Affichage.info("valeur lue par la source prod : "
                    + avecProfil.getBean(IDao.class).getValue());
            Affichage.info("le bean standard n'est plus declare dans ce profil.");
        }
        Affichage.info("les profils separent les environnements, mais ne basculent pas a chaud.");
    }

    private static void etape6Routeur() {
        Affichage.etape(6, "Apport personnel - bascule de source a chaud");

        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(RouteurConfig.class)) {
            IMetier metier = contexte.getBean(IMetier.class);
            RouteurDao routeur = contexte.getBean(RouteurDao.class);

            Affichage.info("sources disponibles : " + String.join(", ", routeur.nomsDisponibles()));
            Affichage.espace();

            Tableau tableau = new Tableau("BASCULE", "SOURCE ACTIVE", "RESULTAT");
            tableau.ligne("depart", routeur.cibleCourante(), metier.calcul());
            for (String cible : routeur.nomsDisponibles()) {
                if (cible.equals(routeur.cibleCourante())) {
                    continue;
                }
                routeur.basculerVers(cible);
                tableau.ligne("-> " + cible, routeur.cibleCourante(), metier.calcul());
            }
            System.out.println(tableau.rendu());
            Affichage.info("meme instance de MetierImpl, quatre sources successives.");
            Affichage.info("aucun redemarrage du conteneur, aucune recompilation.");
        }
    }

    private static void etape7Audit() {
        Affichage.etape(7, "Apport personnel - audit automatise du principe OCP");

        var rapport = audit.AuditPrincipeOcp.auditer(metier.MetierImpl.class);
        Affichage.info(rapport.rendu().replace(System.lineSeparator(), System.lineSeparator() + "  "));

        var references = audit.AuditPrincipeOcp.referencesConcretes(
                metier.MetierImpl.class,
                java.util.List.of(DaoImpl.class, DaoImpl2.class, DaoFile.class, DaoApi.class));
        Affichage.info(references.isEmpty()
                ? "aucune classe concrete de source citee dans le metier : OCP respecte"
                : "references concretes trouvees : " + references);
        Affichage.info("le controle est rejoue automatiquement dans la suite de tests.");
    }
}
