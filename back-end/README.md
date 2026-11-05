# Accounting App - Back-end
## Démarrer l'application en mode développement
### Base de données
L'utilisation d'une base de données est essentielle pour faire fonctionner l'application. Il faut donc avoir une base de
données vide PostgreSQL que l'on va connecter à l'application via un fichier d'environnement. La base de données sera
construite automatiquement au lancement de l'application.

[Guide officiel d'installation de PostgreSQL](https://www.postgresql.org/docs/current/tutorial-install.html)

### Fichier d'environnement
Plusieurs informations doivent être renseignées dans un fichier d'environnement pour faire fonctionner l'application.
Pour ce faire, il suffit de copier le fichier [`src/main/resources/env.properties.example`](src/main/resources/env.properties.example),
de placer la copie au même endroit et de la renommer `env.properties`. Il faut ensuite remplacer les informations avec
celles de votre environnement.

Sauf contre-indication, il est obligatoire de changer chaque variable d'environnement.

### Démarrer l'application
Le démarrage de l'application se fait via la classe [`com.dimtoups.accountingApp.core.CoreApplication`](src/main/java/com/dimtoups/accountingApp/core/CoreApplication.java).
Il faudra bien évidement avoir compilé l'application en amont. Pour cela, la commande maven `mvn clean install compile test`
suffira pour compiler et s'assurer que l'application fonctionne correctement.

Le lancement en lui-même du serveur se fait via un IDE sur la classe mentionnée auparavant.

## Lancer les tests
### Fichier d'environnement
Le lancement des tests demande à avoir un autre fichier d'environnement afin de ne pas utiliser la base de données de
développement. Tout comme pour le fichier d'environnement de développement, il est demandé de copier [`src/main/resources/env.properties.example`](src/main/resources/env.properties.example)
et de le mettre cette fois-ci dans le répertoire [src/test/resources](src/test/resources) avec le nom `env.properties`.
Il ne faudra pas oublier de créer une base de donnée dédiée aux tests et de mettre ses informations de connexion dans le
fichier copié.

### Lancement des tests
Le lancement des tests se fait simplement avec la commande `mvn clean install compile test`.

### Suivi des tests
Un plugin est présent pour observer la couverture de test. Une fois les tests lancés, il suffit d'ouvrir le fichier
[target/site/jacoco/index.html](target/site/jacoco/index.html).