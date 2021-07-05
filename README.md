# E-Lift

Projet MasterCamp 2021

## Table of Contents

1. [General Info](#general-info)
2. [Technologies](#technologies)
3. [Installation](#installation)
4. [Premier lancement](#first-use)
5. [Exploration de l’application](#uses)
6. [Tests](#tests)
7. [FAQs](#faqs)

<a name="general-info"></a>

### General Info

Dans le cadre de notre MasterCamp, nous avons réalisé un projet centré sur la filière que nous avons choisie (la filière
IT).

Nous sommes donc un groupe constitué de 6 personnes :

* Marie Alessandri
* Antoine Claudin
* Julie Claudin
* Paul Godin
* Titouan Lellouche
* Josselin Renan

![Image text](/img/E-LIFT.png)

Parmi les différents choix qui étaient à notre disposition nous avons choisi le concernant la __gestion des pannes
d'ascenseurs__.

Un sujet touchant plusieurs points importants aussi bien au niveau technique que logistique.

Tous les projets de ce MasterCamp ont comme objectif de nous entrainer sur un appel d'offres, permettant de simuler une
situation de travail réelle avec un client et une équipe.

Le cahier des charges étant peu exhaustif, celui-ci nous permettait d'être assez libre dans notre solution technique et
de réfléchir sur les réels besoins du client.

Ce projet nous a permis de livrer un produit fonctionnel répondant aux différents critères requis concernant
l'automatisation de la maintenance des ascenseurs.

https://user-images.githubusercontent.com/61010983/123442327-11e67b80-d5d5-11eb-9463-4e95b5e72821.mp4

<a name="technologies"></a>

### Technologies

Pour réaliser notre projet nous avons utilisé :

* __JAVA__ et __JavaFX__ pour l'interface et pour la connexion avec la base de données


* __MySQL__ pour la base de données


* Les bibliothèques __mapjfx__ pour l'affichage de la carte et __Jackson__ pour le traitement des résultats renvoyés par
  l'API [adresse](https://geo.api.gouv.fr/adresse) de __Etalab__.

dans le but de créer une application capable de satisfaire les principaux intéressés à savoir les gestionnaires
d'ascenseurs et les ascensoristes.

<a name="installation"></a>

### Installation

Pour installer notre application bureau :

* Après avoir téléchargé notre projet, installer la __dernière__ version de __MySQL__ et appliquer le script de création
  de la base de données en tant que `root`, par exemple via l'interface officielle __MySQL Workbench__.


* Installer la __dernière__ version de __Java__ (Java 16) ainsi que l'IDE __Intellij IDEA__.


* Ouvrir le projet `E-Lift.iml` et attendez que les dépendances du projet soit téléchargées.

    * Les dépendances sont en principe téléchargées dans `path/to/project/E-Lift/lib`.


* Depuis __Java 11__, __JavaFX__ n'est plus inclus dans le JDK de base. Il faut donc faire de la réflexion afin de
  charger cette bibliothèque.

  Pour ce faire :
    * Aller dans `Run -> Edit Configurations...`. Une boîte de dialog vient de s'ouvrir.
    * Ajouter ceci
      ```
      --module-path lib --add-modules=javafx.graphics,javafx.fxml,javafx.media,javafx.web 
      --add-reads javafx.graphics=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.iio=ALL-UNNAMED 
      --add-opens javafx.graphics/com.sun.javafx.iio.common=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.css=ALL-UNNAMED 
      --add-opens javafx.base/com.sun.javafx.runtime=ALL-UNNAMED
      ```
      dans les __VM Options__ (Faites <kbd>Alt</kbd> + <kbd>V</kbd> pour les afficher si masquer)
    * Ajouter si besoin `main.view.Main` dans __Main Class__.
    * Appliquer les paramètres et vous êtes enfin prêt à lancer le programme.

  Pour plus d'informations concernant cette partie, cliquez [ici](https://openjfx.io/openjfx-docs/#IDE-Intellij).

<a name="first-use"></a>

### Premier lancement

Lors de chaque lancement de l'application, __E-Lift__ vous demandera d'entrer vos indentifiants.

Comme pour le moment, il y a ni ascensoristes, ni gestionnaires dans la base de données, il vous faudra vous connecter
en tant que `root` ou avec un utilisateur ayant les droits similaires sur votre BDD.

Créer vos premiers utilisateurs en créant des ascensoristes (_Vos employés_) et les gestionnaires (_Vos clients_).

Vous pouvez maintenant commencer à profiter pleinement de __E-Lift__.

<a name="uses"></a>

### Exploration de l’application

Après vous êtes connecté, vous arrivez sur la page d'accueil où vous avez un aperçu de l'état de votre parc d'ascenseurs
actualisé périodiquement.

Selon le type de compte auquel vous vous êtes connecté, vous avez accès aux fonctionnalités suivantes avec notre 
interface graphique intuitive et facile d'utilisation :

|                                    | Administrateur              | Ascensoriste                             | Gestionnaire                                    |
------------------------------------:|-----------------------------|------------------------------------------|-------------------------------------------------|
| **Gestion immeuble**               | Visualisation uniquement    | Non                                      | Oui, seulement les miens                        |
| **Gestion ascenseur**              | Visualisation uniquement    | Non                                      | Oui, seulement ceux qui sont dans mes immeubles |
| **Gestion employés**               | Oui                         | Oui                                      | Non                                             |
| **Gestion clients**                | Oui                         | Oui                                      | Non                                             |
| **Vue d'ensemble ascenseur**       | Tous les ascenseurs         | Tous les ascenseurs                      | Seulement ceux présents dans mes immeubles      |
| **Planning**                       | Seulement mes interventions | Seulement ceux concernant mes ascenseurs | Non                                             |
| **Mis à jour statut intervention** | Non                         | Oui, seulement les miens                 | Non                                             |

<a name="tests"></a>

### Tests

<a name="faqs"></a>

Nous avons utilisé __JUnit__ pour les tests unitaires.

Ces tests sont situés dans le `/test`. Vous pouvez directement les lancez depuis __Intellij IDEA__.

N'oubliez pas de mettre en place la base de données en amont.

### FAQs

FAQs
