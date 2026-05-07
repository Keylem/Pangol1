# Documentation Pangol1

## Table des matières

1. [Introduction](#introduction)
2. [Architecture générale](#architecture-générale)
3. [Structure du projet](#structure-du-projet)
4. [Modules principaux](#modules-principaux)
5. [Installation et configuration](#installation-et-configuration)
6. [Guide d'utilisation](#guide-dutilisation)
7. [API et développement](#api-et-développement)
8. [Contribution](#contribution)

---

## Introduction

### Présentation générale

**Pangol1** est une application Java qui permet de **générer des graphiques SVG** à partir de fichiers de données (CSV, Parquet, etc.). L'application offre une interface graphique intuitive pour manipuler, analyser et visualiser des données sous forme de tableaux et de graphiques.

### Caractéristiques principales

- 📊 **Visualisation de données** : Création de graphiques SVG personnalisés
- 📈 **Statistiques avancées** : Calculs statistiques sur les colonnes (moyenne, médiane, skewness, corrélation, etc.)
- 🔄 **Filtrage et tri** : Outils puissants pour filtrer et trier les données
- 🌐 **Support multilingue** : 10 langues supportées (français, anglais, espagnol, russe, chinois, arabe, japonais, portugais, indonésien, turc)
- 🎨 **Thème clair/sombre** : Interface adaptative avec FlatLaf
- 📋 **Gestion de tables** : Import/export de données en plusieurs formats
- 📑 **Génération de rapports** : Export en PDF et HTML
- 🖥️ **Interface moderne** : Interface utilisateur intuitive et réactive

### Technologies utilisées

- **Java 21** : Langage de programmation principal
- **Maven** : Gestion des dépendances et construction
- **Swing** : Framework GUI
- **DuckDB JDBC** : Base de données en mémoire
- **FlatLaf** : Look and Feel moderne
- **Batik** : Transcoding SVG
- **OpenHTMLToPDF** : Génération de PDF
- **JUnit 4** : Tests unitaires
- **JavaFX** : Rendu HTML avancé

---

## Architecture générale

### Modèle MVC

Pangol1 suit le pattern **Modèle-Vue-Contrôleur (MVC)** :

```
┌─────────────────────────────────────────┐
│         Application (Pangol1)           │
├─────────────────────────────────────────┤
│  ┌──────────────────────────────────┐  │
│  │   GUI (Vue - Swing)              │  │
│  │  - MainView                      │  │
│  │  - Panels & Dialogs              │  │
│  │  - Table & Chart Views           │  │
│  └──────────────────────────────────┘  │
│                    ↑ ↓                  │
│  ┌──────────────────────────────────┐  │
│  │  GUIController (Contrôleur)      │  │
│  │  - Gestion des événements        │  │
│  │  - Orchestration                 │  │
│  └──────────────────────────────────┘  │
│                    ↑ ↓                  │
│  ┌──────────────────────────────────┐  │
│  │   Core Services (Métier)         │  │
│  │  - DataTable                     │  │
│  │  - Filter & Sort                 │  │
│  │  - Statistics                    │  │
│  │  - File I/O                      │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

### Architecture en couches

1. **Couche Présentation (GUI)**
   - Composants Swing
   - Dialogs et Panels
   - Rendu des tables et graphiques

2. **Couche Contrôle (GUIController)**
   - Gestion des événements
   - Orchestration des services
   - Gestion des tâches

3. **Couche Métier (Services)**
   - Logique de filtrage et tri
   - Calculs statistiques
   - Gestion des données

4. **Couche Données (I/O)**
   - Import/export CSV, Parquet, SVG
   - Persistance DuckDB
   - Sérialisation XML

---

## Structure du projet

### Arborescence complète

```
Pangol1/
├── src/fr/univrennes/istic/l2gen/application/
│   ├── Pangol1.java                    # Entrée principale
│   ├── core/                           # Cœur métier
│   │   ├── CoreApp.java                # Application abstraite
│   │   ├── CoreController.java         # Contrôleur abstrait
│   │   ├── TaskStatus.java             # États des tâches
│   │   ├── config/                     # Configuration
│   │   │   ├── Config.java             # Gestion des paramètres
│   │   │   ├── Lang.java               # Internationalisation
│   │   │   ├── Log.java                # Logging
│   │   │   └── Ico.java                # Icônes
│   │   ├── filter/                     # Système de filtrage
│   │   │   ├── Filter.java             # API filtres
│   │   │   ├── FilterBuilder.java      # Constructeur de filtres
│   │   │   ├── FilterCondition.java    # Conditions
│   │   │   ├── FilterFunction.java     # Fonctions
│   │   │   ├── FilterLogic.java        # Logique (AND/OR/NOT)
│   │   │   ├── FilterOperator.java     # Opérateurs
│   │   │   └── FilterSort.java         # Tri
│   │   ├── notebook/                   # Cahier de notes
│   │   │   ├── NoteBookText.java
│   │   │   ├── NoteBookChart.java
│   │   │   ├── NoteBookImage.java
│   │   │   └── NoteBookValue.java
│   │   ├── services/                   # Services métier
│   │   │   ├── FileService.java        # I/O fichiers
│   │   │   ├── TableService.java       # Gestion des tables
│   │   │   ├── stats/                  # Services statistiques
│   │   │   │   ├── StatisticService.java
│   │   │   │   └── [autres services]
│   │   │   └── notebook/               # Gestion du cahier
│   │   │       └── NoteBookService.java
│   │   └── table/                      # Modèle de données
│   │       ├── DataTable.java          # Table de données
│   │       └── DataType.java           # Types de données
│   ├── gui/                            # Interface utilisateur
│   │   ├── GUIApp.java                 # Démarrage GUI
│   │   ├── GUIController.java          # Contrôleur GUI
│   │   ├── main/                       # Fenêtre principale
│   │   │   ├── MainView.java
│   │   │   ├── MainViewMenu.java
│   │   │   └── SplashScreen.java
│   │   ├── panels/                     # Panneaux
│   │   │   ├── table/                  # Panel table
│   │   │   │   └── view/data/
│   │   │   │       ├── TableDataView.java
│   │   │   │       ├── TableColumnContextMenu.java
│   │   │   │       └── [autres]
│   │   │   ├── chart/                  # Panel graphiques
│   │   │   ├── report/                 # Panel rapport
│   │   │   └── [autres panels]
│   │   └── dialog/                     # Dialogues
│   │       ├── input/                  # Dialogues d'entrée
│   │       │   ├── InputStringDialog.java
│   │       │   ├── InputIntDialog.java
│   │       │   ├── InputDoubleDialog.java
│   │       │   └── [autres]
│   │       ├── stats/                  # Dialogues stats
│   │       └── [autres dialogues]
│   ├── geometry/                       # Géométrie (shapes)
│   │   ├── IShape.java                 # Interface forme
│   │   ├── AbstractShape.java          # Forme abstraite
│   │   ├── Point.java                  # Point 2D
│   │   ├── Path.java                   # Chemin
│   │   ├── Group.java                  # Groupe de formes
│   │   └── base/                       # Formes de base
│   │       ├── Circle.java
│   │       ├── Ellipse.java
│   │       ├── Line.java
│   │       ├── Rectangle.java
│   │       ├── Polygon.java
│   │       ├── PolyLine.java
│   │       ├── Triangle.java
│   │       └── Text.java
│   ├── io/                             # Import/Export
│   │   ├── svg/                        # SVG I/O
│   │   │   ├── SVGExport.java
│   │   │   └── SVGImport.java
│   │   └── xml/                        # XML I/O
│   │       ├── model/
│   │       └── parser/
│   ├── svg/                            # SVG avancé
│   │   ├── animations/                 # Animations SVG
│   │   │   ├── AbstractAnimate.java
│   │   │   ├── SVGAnimate.java
│   │   │   ├── SVGAnimateMotion.java
│   │   │   ├── SVGAnimateTransform.java
│   │   │   └── [propriétés d'animation]
│   │   ├── attributes/                 # Attributs SVG
│   │   │   ├── path/                   # Commandes path
│   │   │   ├── style/                  # Style
│   │   │   └── transform/              # Transformations
│   │   ├── color/                      # Gestion couleurs
│   │   │   └── Color.java
│   │   └── interfaces/                 # Interfaces SVG
│   │       └── ISVGShape.java
│   └── visustats/                      # Visualisation stats
│       ├── data/                       # Modèle de données
│       │   ├── DataGroup.java
│       │   ├── DataSet.java
│       │   ├── Label.java
│       │   └── Value.java
│       └── view/                       # Vues de visualisation
│           └── datagroup/
├── test/                               # Tests unitaires
│   └── fr/univrennes/istic/l2gen/application/
│       ├── Pangol1Test.java
│       ├── geometry/
│       ├── io/
│       └── visustats/
├── resources/                          # Ressources
│   ├── languages/                      # Fichiers de traduction
│   │   └── pangol1_*.properties        # 10 langues
│   ├── icons/                          # Icônes
│   ├── export/                         # Templates export
│   │   └── page.html
│   └── doc/                            # Documentation inline
├── uml/                                # Diagrammes UML
│   ├── application.puml
│   ├── geometry.puml
│   ├── io.puml
│   ├── svg.puml
│   └── visustats.puml
├── script/                             # Scripts utilitaires
│   ├── generate_csv.py                 # Génération données de test
│   ├── generate_jdoc.py                # Génération documentation
│   ├── generate_puml.py                # Génération UML
│   └── generate_svg.py                 # Génération SVG
├── docs/                               # Documentation
│   ├── DOCUMENTATION.md                # Cette documentation
│   ├── MEMBERS.md                      # Membres du projet
│   ├── SPRINT.md                       # Sprints et planification
│   ├── USERS-STORIES.md                # User stories
│   └── MANUEL-UTILISATEUR.tex          # Manuel LaTeX
├── pom.xml                             # Configuration Maven
└── README.md                           # Fichier README
```

---

## Modules principaux

### 1. Module Core (Cœur métier)

**Rôle** : Contient la logique métier et les services principaux.

#### Sous-modules

##### Config & Logging

- `Config.java` : Gestion centralisée de la configuration (properties)
- `Lang.java` : Système d'internationalisation (10 langues)
- `Log.java` : Logging structuré
- `Ico.java` : Gestion des icônes

##### Système de filtrage (Filter)

```java
// Exemple d'utilisation
Filter filter = Filter.sort(columnIndex, ascending);
Filter rangeFilter = Filter.byRange(columnIndex, min, max);
Filter categoryFilter = Filter.equals(columnIndex, category);
table.addFilter(filter);
```

Classes principales :

- `Filter.java` : Factory et API principale
- `FilterBuilder.java` : Constructeur fluide de filtres complexes
- `FilterCondition.java` : Conditions prédicats
- `FilterLogic.java` : Opérateurs logiques (AND, OR, NOT)
- `FilterSort.java` : Tri des données

##### Modèle de données (Table)

- `DataTable.java` : Représentation en mémoire des données
- `DataType.java` : Types supportés (STRING, INTEGER, DOUBLE, DATE, BOOLEAN)

##### Services métier

- `TableService.java` : CRUD sur les tables
- `FileService.java` : Import/export fichiers
- `StatisticService.java` : Calculs statistiques (moyenne, médiane, IQR, skewness, corrélation)
- `NoteBookService.java` : Gestion du cahier de notes

### 2. Module GUI (Interface utilisateur)

**Rôle** : Affichage et interaction utilisateur avec Swing.

#### Structure

```
GUI
├── MainView                    # Fenêtre principale
│   ├── MenuBar                 # Barre de menus
│   ├── TablePanel              # Affichage table
│   ├── ChartPanel              # Affichage graphiques
│   └── ReportPanel             # Affichage rapport
├── Dialogs                     # Dialogues modaux
│   ├── Input (String, Int, Double, Date, Select)
│   └── Stats (Affichage statistiques)
└── Panels                      # Panneaux réutilisables
    ├── Table                   # Gestion du tableau
    ├── Chart                   # Gestion des graphiques
    └── Report                  # Cahier de notes
```

#### GUIController

Singleton qui orchestre :

- Événements utilisateur
- Communication entre panels
- Gestion des tâches asynchrones
- Mise à jour de l'interface

### 3. Module Geometry (Géométrie)

**Rôle** : Modèle objet pour les formes géométriques.

Hiérarchie des classes :

```
IShape (interface)
 ↑
AbstractShape (classe abstraite)
 ├── Point
 ├── Path
 ├── Group
 └── Formes de base
     ├── Circle
     ├── Ellipse
     ├── Line
     ├── Rectangle
     ├── Polygon
     ├── PolyLine
     ├── Triangle
     └── Text
```

Utilisation :

```java
Circle circle = new Circle(100, 100, 50);
circle.setFill(new Color(255, 0, 0));
circle.setStroke(new Color(0, 0, 0), 2);

Group group = new Group();
group.add(circle);
group.add(new Rectangle(10, 10, 200, 150));
```

### 4. Module SVG (Vectoriel)

**Rôle** : Gestion avancée du format SVG et animations.

#### Animationsvg

- `SVGAnimate.java` : Animation d'attributs
- `SVGAnimateMotion.java` : Animation de mouvement
- `SVGAnimateTransform.java` : Animation de transformation

Propriétés :

- `AnimationDuration.java`
- `AnimationCount.java`
- `AnimationFill.java`
- `AnimationRestart.java`
- `AnimationTransformType.java`

#### Attributs et Transforms

- `path/` : Commandes de chemin (M, L, C, Q, A, etc.)
- `style/` : Attributs de style CSS
- `transform/` : Transformations (translate, rotate, scale, skew)

### 5. Module I/O (Import/Export)

**Rôle** : Lecture et écriture de fichiers.

#### SVG I/O

- `SVGExport.java` : Export données → SVG
- `SVGImport.java` : Import SVG → objets geometrie

#### XML I/O

- Parsing de fichiers XML
- Sérialisation de modèles

#### Formats supportés

- CSV (lecture/écriture)
- Parquet (lecture/écriture)
- SVG (lecture/écriture)
- XML (configuration)
- HTML (export rapport)
- PDF (export rapport)

### 6. Module VisuStats (Visualisation statistique)

**Rôle** : Modèle pour la visualisation de données statistiques.

Classes de données :

- `DataSet.java` : Ensemble de données
- `DataGroup.java` : Groupement de datasets
- `Label.java` : Étiquette
- `Value.java` : Valeur numérique

Types de visualisation :

- Graphiques en barres
- Graphiques en courbes
- Graphiques en points
- Diagrammes secteurs
- Histogrammes

---

## Installation et configuration

### Prérequis système

- **Java JDK 21+** : [Télécharger](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- **Maven 3.8+** : [Télécharger](https://maven.apache.org/download.cgi)
- **Git** : [Télécharger](https://git-scm.com)
- **VS Code** (recommandé) avec extensions :
  - Extension Pack for Java
  - Maven for Java

### Installation locale

#### 1. Cloner le dépôt

```bash
git clone https://github.com/jules1univ/Pangol1.git
cd Pangol1
```

#### 2. Construire le projet

```bash
# Avec Maven
mvn clean package

# Ou avec le wrapper Maven (inclus)
./mvnw clean package
```

#### 3. Exécuter l'application

```bash
# Via Maven
mvn exec:java

# Via JAR construit
java -jar target/Pangol1-1.0-SNAPSHOT.jar

# En développement (VS Code)
# Utiliser la commande "Run" depuis le terminal ou les raccourcis Java
```

### Configuration

#### Fichiers de configuration

**`config.properties`** (dans le dossier utilisateur ou ressources)

```properties
# Apparence
settings.appearance.use_flatlaf=true
settings.appearance.auto_start=18
settings.appearance.theme=LIGHT

# Langue (locale ISO 639-1)
settings.general.language=fr

# Table
settings.table.manual_typing=true
settings.table.auto_resize=false

# Export
settings.export.format=SVG
settings.export.quality=HIGH

# Performances
settings.performance.max_rows=100000
settings.performance.cache_enabled=true
```

#### Fichiers de langue

Les fichiers `.properties` sont dans `src/resources/languages/` :

```
pangol1_fr.properties    # Français
pangol1_en.properties    # Anglais
pangol1_es.properties    # Espagnol
pangol1_de.properties    # Allemand
pangol1_it.properties    # Italien
pangol1_zh.properties    # Chinois
pangol1_ja.properties    # Japonais
pangol1_ru.properties    # Russe
pangol1_ar.properties    # Arabe
pangol1_pt.properties    # Portugais
pangol1_id.properties    # Indonésien
pangol1_tr.properties    # Turc
```

Format des clés i18n :

```properties
# Format: categorie.sous.categorie.cle=Valeur
menu.file.open=Ouvrir...
menu.file.save=Enregistrer
dialog.confirm.title=Confirmation
table.type.string=Texte
table.type.integer=Entier
error.file_not_found=Fichier non trouvé
```

---

## Guide d'utilisation

### 1. Démarrage et interface principale

À l'ouverture de l'application :

1. Un écran de démarrage (SplashScreen) s'affiche
2. La configuration est chargée (thème, langue)
3. La fenêtre principale s'ouvre avec 3 panneaux :
   - **Panel Table** : Affichage des données
   - **Panel Graphique** : Visualisation graphique
   - **Panel Rapport** : Cahier de notes

### 2. Importer des données

#### Via le menu File → Import

1. Sélectionner le fichier source (CSV, Parquet, XLS, etc.)
2. Configurer l'import :
   - Séparateur (,, ;, TAB, etc.)
   - Encodage (UTF-8, ISO-8859-1, etc.)
   - Headers (première ligne = noms colonnes ?)
3. Cliquer "Import"

#### Formats supportés

| Format  | Lecteur            | Limites          |
| ------- | ------------------ | ---------------- |
| CSV     | Apache Commons CSV | Texte délimité   |
| Parquet | Apache Parquet     | Binaire columnar |
| Excel   | Apache POI         | .xls, .xlsx      |
| JSON    | Jackson            | Tableaux plats   |
| XML     | DOM Parser         | Tables XML       |

### 3. Manipuler le tableau

#### Trier une colonne

1. Clic droit sur en-tête de colonne
2. **Sort → Ascending** ou **Descending**

#### Filtrer les données

1. Clic droit sur en-tête
2. **Filter** → Choisir un type de filtre :
   - **Top N** : Les N premiers éléments
   - **Bottom N** : Les N derniers éléments
   - **Range** : Plage de valeurs
   - **Empty** : Afficher les lignes vides
   - **Non-empty** : Masquer les lignes vides
   - **By Category** : Filtrer par catégorie (menu ou saisie)
   - **By Value** : Filtrer par texte (recherche)

#### Types de filtre par type de données

| Type    | Filtres disponibles                                  |
| ------- | ---------------------------------------------------- |
| STRING  | Top N, Bottom N, Range (longueur), Categories, Value |
| INTEGER | Top N, Bottom N, Range (valeurs)                     |
| DOUBLE  | Top N, Bottom N, Range (valeurs)                     |
| DATE    | Top N, Bottom N, Range (dates)                       |
| BOOLEAN | Categories                                           |

### 4. Statistiques

Clic droit sur en-tête → **Stats** :

| Statistique               | Types applicables          |
| ------------------------- | -------------------------- |
| Résumé                    | Tous                       |
| Taux de null              | Tous                       |
| Ratio de cardinalité      | Tous                       |
| Écart interquartile (IQR) | Numérique                  |
| Asymétrie (Skewness)      | Numérique                  |
| Coefficient de variation  | Numérique                  |
| Corrélation               | Numérique (avec une autre) |

### 5. Créer un graphique

1. Accéder au **Panel Graphique**
2. Sélectionner le type de graphique :
   - Barres
   - Courbes
   - Points/Scatter
   - Secteurs (Pie)
   - Histogramme
3. Configurer :
   - Colonnes X et Y
   - Couleurs
   - Titre et étiquettes
4. Générer l'aperçu
5. Exporter en SVG/PNG

### 6. Générer des rapports

1. Accéder au **Panel Rapport**
2. Ajouter du contenu :
   - **Texte** : Annotations libres
   - **Graphiques** : Graphiques générés
   - **Tables** : Exports de tableaux
   - **Images** : Images SVG/PNG
   - **Valeurs** : Résultats statistiques
3. Organiser le rapport
4. Exporter :
   - **HTML** : Aperçu interactif
   - **PDF** : Document statique

### 7. Changer de langue

**Menu Settings → Language** :

- Français
- English
- Español
- Deutsch
- Italiano
- 中文 (Chinois)
- 日本語 (Japonais)
- Русский (Russe)
- العربية (Arabe)
- Português

### 8. Changer le thème

**Menu Settings → Appearance** :

- **Light** : Thème clair
- **Dark** : Thème sombre (après 18h par défaut)
- **Auto** : Bascule automatique selon l'heure

---

## API et développement

### Architecture des Services

#### TableService

```java
public class TableService {
    // Import/Export
    public static DataTable importCSV(File file, char separator);
    public static void exportCSV(DataTable table, File file);

    // Filtrage
    public static DataTable applyFilter(DataTable table, Filter filter);
    public static List<Filter> getActiveFilters(DataTable table);

    // Manipulation
    public static void addColumn(DataTable table, String name, DataType type);
    public static void removeColumn(DataTable table, int columnIndex);
    public static void renameColumn(DataTable table, int columnIndex, String newName);
}
```

#### StatisticService

```java
public class StatisticService {
    // Statistiques descriptives
    public static String computeSummary(DataTable table, int columnIndex);
    public static OptionalDouble computeNullRate(DataTable table, int columnIndex);
    public static OptionalDouble computeCardinalityRatio(DataTable table, int columnIndex);

    // Statistiques numériques
    public static OptionalDouble computeInterquartileRange(DataTable table, int columnIndex);
    public static OptionalDouble computeSkewness(DataTable table, int columnIndex);
    public static OptionalDouble computeCoefficientOfVariation(DataTable table, int columnIndex);

    // Analyse multivariée
    public static OptionalDouble computeCorrelation(DataTable table, int col1, int col2);

    // Catégories
    public static boolean hasColumnCategories(DataTable table, int columnIndex);
    public static List<String> getColumnCategories(DataTable table, int columnIndex);
}
```

#### Filter API

```java
// Création de filtres
Filter topN = Filter.topN(columnIndex, n);
Filter bottomN = Filter.bottomN(columnIndex, n);
Filter range = Filter.byRange(columnIndex, min, max);
Filter equals = Filter.equals(columnIndex, value);
Filter search = Filter.search(columnIndex, term);
Filter sort = Filter.sort(columnIndex, ascending);

// Filtres vides
Filter empty = Filter.showEmpty(columnIndex);
Filter nonEmpty = Filter.hideEmpty(columnIndex);

// Composition de filtres (via FilterBuilder)
Filter complex = new FilterBuilder()
    .and(Filter.sort(0, true))
    .and(Filter.byRange(1, 10, 100))
    .or(Filter.equals(2, "categorie"))
    .build();
```

### Modèle de données

#### DataTable

```java
public class DataTable {
    // Navigation
    public int getRowCount();
    public int getColumnCount();
    public String getColumnName(int columnIndex);
    public DataType getColumnType(int columnIndex);

    // Accès
    public Object getValue(int rowIndex, int columnIndex);
    public List<Object> getRow(int rowIndex);
    public List<Object> getColumn(int columnIndex);

    // Modification
    public void setValue(int rowIndex, int columnIndex, Object value);
    public void setColumnType(int columnIndex, DataType newType);

    // Filtrage
    public void addFilter(Filter filter);
    public void clearFilters();
    public void clearColumnFilter(int columnIndex);
}
```

#### DataType

```java
public enum DataType {
    STRING,        // Texte
    INTEGER,       // Nombres entiers
    DOUBLE,        // Nombres décimaux
    DATE,          // Dates (java.sql.Timestamp)
    BOOLEAN;       // Booléens

    public boolean isNumeric();
    public boolean isCategorical();
    public String getDisplayName();
}
```

### Extension de l'application

#### Créer un nouveau type de filtre

1. Créer une classe qui implémente `FilterCondition` :

```java
public class MyCustomFilter implements FilterCondition {
    @Override
    public boolean evaluate(Object value) {
        // Logique du filtre
        return true;
    }
}
```

2. Ajouter la factory dans `Filter.java` :

```java
public static Filter custom(int columnIndex, FilterCondition condition) {
    return new Filter(columnIndex, condition);
}
```

#### Créer un nouveau type de graphique

1. Créer une classe dans `visustats/view/` :

```java
public class MyChartView extends AbstractChartView {
    public MyChartView(DataSet dataSet) {
        super(dataSet);
    }

    @Override
    protected Component render() {
        // Rendu du graphique
        return new JPanel();
    }
}
```

2. Enregistrer dans le panel graphique :

```java
chartTypeCombo.addItem(new ChartType("My Chart", MyChartView.class));
```

#### Ajouter une nouvelle statistique

1. Ajouter la méthode dans `StatisticService` :

```java
public static OptionalDouble computeMyStatistic(DataTable table, int columnIndex) {
    // Calcul
    return OptionalDouble.of(result);
}
```

2. Ajouter l'option dans le menu contextuel :

```java
JMenuItem myStatItem = new JMenuItem("Ma statistique");
myStatItem.addActionListener(e -> {
    OptionalDouble result = StatisticService.computeMyStatistic(table, tableIndex);
    // Affichage du résultat
});
stats.add(myStatItem);
```

### Logging et débogage

#### Configuration du logging

`Log.java` fournit des méthodes statiques :

```java
Log.info("Message d'information");
Log.warn("Message d'avertissement");
Log.error("Message d'erreur");
Log.debug("Message de débogage");
```

Niveaux de log :

- `DEBUG` : Informations détaillées
- `INFO` : Informations générales
- `WARN` : Avertissements
- `ERROR` : Erreurs

### Tests unitaires

Localisation : `test/fr/univrennes/istic/l2gen/application/`

Exécution :

```bash
# Tous les tests
mvn test

# Test spécifique
mvn test -Dtest=PointTest

# Avec couverture de code
mvn test jacoco:report
```

Exemple de test :

```java
import org.junit.Test;
import static org.junit.Assert.*;

public class PointTest {
    @Test
    public void testDistance() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(3, 4);
        assertEquals(5.0, p1.distance(p2), 0.001);
    }
}
```

---

## Contribution

### Environnement de développement

#### Démarrage

1. Fork et cloner le repository
2. Créer une branche feature : `git checkout -b feature/ma-feature`
3. Configurer VS Code :
   - Installer les extensions Java
   - Configurer le JDK 21
   - Importer le projet Maven

#### Conventions de code

**Nommage**

- Classes : `PascalCase` (ex: `TableDataView`)
- Méthodes : `camelCase` (ex: `addFilter()`)
- Constantes : `UPPER_SNAKE_CASE` (ex: `MAX_ROWS`)
- Variables : `camelCase` (ex: `columnIndex`)

**Format**

- Indentation : 4 espaces
- Largeur max : 120 caractères
- Encodage : UTF-8

**Documentation**

```java
/**
 * Description brève de la méthode.
 *
 * Description détaillée si nécessaire.
 *
 * @param param1 Description du paramètre
 * @return Description du retour
 * @throws Exception Description de l'exception
 */
public void maMethode(String param1) throws Exception {
    // Implémentation
}
```

### Workflow de contribution

1. **Fork** le repository
2. **Clone** votre fork localement
3. **Créer une branche** : `git checkout -b feature/description`
4. **Développer** et tester localement
5. **Commit** avec messages explicites :
   ```bash
   git commit -m "feat: ajout de la fonctionnalité X"
   git commit -m "fix: correction du bug Y"
   git commit -m "refactor: refactorisation du module Z"
   ```
6. **Push** vers votre fork
7. **Créer une Pull Request** sur le repo principal
8. **Répondre** aux revues de code

### Process de review

Les Pull Requests sont revues selon :

- ✅ Tests unitaires passants
- ✅ Couverture de code > 80%
- ✅ Respect des conventions
- ✅ Documentation à jour
- ✅ Performance acceptable

### Signaler des bugs

Utiliser le template issue GitHub :

```markdown
## Description

Décrire le bug clairement.

## Reproduction

Étapes pour reproduire le problème :

1. ...
2. ...
3. ...

## Comportement observé

...

## Comportement attendu

...

## Environnement

- OS : Windows/Linux/macOS
- Java : JDK 21
- Version : v1.0
```

### Proposer des améliorations

Créer une issue avec le label `enhancement` :

```markdown
## Titre

Une description claire de l'amélioration.

## Motivation

Pourquoi cette amélioration est-elle nécessaire ?

## Solution proposée

Comment implémenter cette amélioration ?

## Cas d'usage

Exemples concrets d'utilisation.
```

---

## Dépannage

### Problèmes courants

#### L'application ne démarre pas

**Cause possible** : JDK non configuré ou version incorrecte

**Solution** :

```bash
# Vérifier la version Java
java --version
# Doit afficher : Java 21.x

# Configurer JAVA_HOME
export JAVA_HOME=/path/to/jdk21
```

#### Interface GUI défaillante

**Cause possible** : FlatLaf non initialisé

**Solution** :

```bash
# Vérifier dans les logs
mvn exec:java -X | grep -i flatlaf

# Réinstaller les dépendances
mvn clean dependency:resolve
```

#### Erreurs d'import/export

**Cause possible** : Format de fichier non supporté

**Solution** :

- Vérifier le format du fichier
- Vérifier les droits d'accès au fichier
- Consulter les logs dans `File → View Logs`

#### Performances lentes

**Cause possible** : Grande quantité de données

**Solutions** :

- Augmenter la mémoire JVM : `java -Xmx4G -jar Pangol1.jar`
- Filtrer les données avant d'appliquer des statistiques
- Activer le cache : `settings.performance.cache_enabled=true`

---

## Ressources supplémentaires

### Documentation externe

- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Maven Guide](https://maven.apache.org/guides/)
- [Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
- [SVG Specification](https://www.w3.org/TR/SVG2/)
- [DuckDB Documentation](https://duckdb.org/docs/)

### Fichiers de projet

- [README.md](../README.md) : Présentation générale
- [CONTRIBUTING.md](../CONTRIBUTING.md) : Guide de contribution
- [MEMBERS.md](MEMBERS.md) : Membres du projet
- [USERS-STORIES.md](USERS-STORIES.md) : User stories
- [SPRINT.md](SPRINT.md) : Planification des sprints
- [MANUEL-UTILISATEUR.tex](MANUEL-UTILISATEUR.tex) : Manuel LaTeX

### Diagrammes UML

- [application.puml](../uml/application.puml)
- [geometry.puml](../uml/geometry.puml)
- [io.puml](../uml/io.puml)
- [svg.puml](../uml/svg.puml)
- [visustats.puml](../uml/visustats.puml)

### Scripts utilitaires

- `script/generate_csv.py` : Générer des données de test
- `script/generate_jdoc.py` : Générer la documentation Javadoc
- `script/generate_puml.py` : Générer les diagrammes UML
- `script/generate_svg.py` : Générer des graphiques SVG

---

## Contact et support

Pour toute question ou demande d'assistance :

- 📧 **Email** : [contact@pangol1.fr](mailto:contact@pangol1.fr)
- 🐛 **Issues** : [GitHub Issues](https://github.com/jules1univ/Pangol1/issues)
- 💬 **Discussions** : [GitHub Discussions](https://github.com/jules1univ/Pangol1/discussions)
- 📝 **Wiki** : [Wiki du projet](https://github.com/jules1univ/Pangol1/wiki)

---

**Dernière mise à jour** : 7 mai 2026
**Version** : 1.0-SNAPSHOT
**Auteurs** : [Voir MEMBERS.md](MEMBERS.md)
**Licence** : MIT
