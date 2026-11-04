package config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration de base : detection des composants et chargement du fichier de
 * proprietes.
 * <p>
 * Prise seule, elle laisse volontairement l'ambiguite : quatre beans
 * implementent {@code IDao} et aucun n'est designe comme candidat par defaut.
 */
@Configuration
@ComponentScan(basePackages = {"dao", "metier"})
@PropertySource("classpath:app.properties")
public class AppConfig {
}
