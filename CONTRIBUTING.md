# Guide de Contribution - Pangol1

Bienvenue ! Merci de votre intérêt pour contribuer à **Pangol1**. Ce guide vous explique comment configurer votre environnement, respecter les conventions de code, et soumettre vos contributions.

## Table des matières

1. [Mise en place de l'environnement](#mise-en-place-de-lenvironnement)
2. [Conventions de code](#conventions-de-code)
3. [Workflow de contribution](#workflow-de-contribution)
4. [Tests et qualité](#tests-et-qualité)
5. [Documentation](#documentation)
6. [Process de review](#process-de-review)
7. [Signaler des problèmes](#signaler-des-problèmes)

## Mise en place de l'environnement

### Prérequis

- **Java JDK 21** : [Télécharger](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- **Maven 3.8+** : [Télécharger](https://maven.apache.org/download.cgi)
- **Git 2.30+** : [Télécharger](https://git-scm.com)
- **VS Code** avec les extensions :
  - Extension Pack for Java (Microsoft)
  - Maven for Java (Microsoft)

### Configuration initiale

#### 1. Forker et cloner le repository

```bash
# Fork sur GitHub via l'interface web
# Puis cloner votre fork
git clone https://github.com/YOUR_USERNAME/Pangol1.git
cd Pangol1

# Ajouter le repository original comme remote
git remote add upstream https://github.com/jules1univ/Pangol1.git
```

#### 2. Configurer Java dans VS Code

```bash
# Vérifier la version de Java
java --version
# Doit afficher : openjdk version "21.*"

# Configurer JAVA_HOME (si nécessaire)
export JAVA_HOME=$(dirname $(dirname $(which java)))

# Vérifier Maven
mvn --version
```

#### 3. Construire le projet localement

```bash
# Nettoyer et construire
mvn clean install

# Ou avec le wrapper inclus
./mvnw clean install

# Lancer les tests
mvn test

# Générer la documentation
mvn javadoc:javadoc
```

#### 4. Lancer l'application en développement

```bash
# Via Maven
mvn exec:java

# Via VS Code : F5 ou Run → Run Without Debugging
# Via IDE : Ctrl+F5 (ou Cmd+F5 sur Mac)
```

## Conventions de code

### 1. Nommage des classes

**Règle** : `PascalCase`, descriptif, pas de préfixes/suffixes génériques

```java
// ✅ BON
public class TableColumnContextMenu extends JPopupMenu { }
public class StatisticService { }
public class DataTable { }
public class FilterBuilder { }

// ❌ MAUVAIS
public class TableMenu { }  // Trop court, manque de contexte
public class TDCM { }       // Trop condensé
public class TableColumnContextMenuImpl { }  // Suffixe redondant
```

**Interfaces** : Préfixe `I`

```java
// ✅ BON
public interface IShape { }
public interface ISVGAttribute { }

// ❌ MAUVAIS
public interface Shape { }
public interface ShapeInterface { }
```

**Classes abstraites** : Préfixe `Abstract`

```java
// ✅ BON
public abstract class AbstractShape implements IShape { }
public abstract class AbstractAnimate { }

// ❌ MAUVAIS
public abstract class Shape { }
public abstract class BaseShape { }
```

**Énumérations** : `PascalCase` avec valeurs en `SCREAMING_SNAKE_CASE`

```java
// ✅ BON
public enum DataType {
    STRING,
    INTEGER,
    DOUBLE,
    DATE,
    BOOLEAN
}

// ❌ MAUVAIS
public enum dataType { }
public enum DATA_TYPE { }
```

### 2. Nommage des méthodes

**Règle** : `camelCase`, verbes d'action, pas de fluff

```java
// ✅ BON
public void addFilter(Filter filter) { }
public List<Object> getColumn(int index) { }
public boolean hasContent() { }
public void removeFilter(int index) { }

// ❌ MAUVAIS
public void add_filter(Filter filter) { }  // Snake case
public void doAddFilter(Filter filter) { }  // Redondant
public void getFilterList() { }  // Pour un setter → setFilterList()
```

**Getters/Setters** : Pattern standard

```java
// ✅ BON
public int getWidth() { return width; }
public void setWidth(int width) { this.width = width; }
public boolean hasChildren() { return children.size() > 0; }

// ❌ MAUVAIS
public int width() { }  // Pas de "get" → ambiguïté
public void width(int w) { }  // Pas de "set"
public int getChild(int i) { return children.get(i); }  // Pas de "is/has"
```

**Méthodes statiques** : Verbes ou adjectifs

```java
// ✅ BON
public static Filter topN(int columnIndex, int n) { }
public static Filter sort(int columnIndex, boolean ascending) { }

// ❌ MAUVAIS
public static Filter top_n(int columnIndex, int n) { }
```

### 3. Nommage des variables

**Règle** : `camelCase`, descriptif, éviter les abbréviations

```java
// ✅ BON
int tableIndex = 0;
String columnName = "Age";
DataType columnType = DataType.INTEGER;
List<Object> columnValues = new ArrayList<>();

// ❌ MAUVAIS
int tIdx = 0;  // Trop abrégé
int ti = 0;  // Non descriptif
int idx = 0;  // Vague
List vals = new ArrayList();  // Type raw + abrégé
```

**Boucles** : Noms explicites sauf contexte mathématique

```java
// ✅ BON
for (IShape shape : shapes) { }
for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) { }

// Pour les calculs géométriques : acceptable
for (int i = 0; i < points.length; i++) { }  // Points Bézier

// ❌ MAUVAIS
for (IShape s : shapes) { }  // Trop court
for (int i = 0; i < rows.size(); i++) { }  // Si pas contexte mathématique
```

**Paramètres** : Noms significatifs

```java
// ✅ BON
public void setPosition(double x, double y) { }
public Filter byRange(int columnIndex, double min, double max) { }

// ❌ MAUVAIS
public void setPosition(double a, double b) { }
public Filter byRange(int i, double minVal, double maxVal) { }  // Incohérent
```

### 4. Constantes

**Règle** : `SCREAMING_SNAKE_CASE`, `static final`, visibilité explicite

```java
// ✅ BON
private static final int MAX_CATEGORIES = 25;
private static final String DEFAULT_ENCODING = "UTF-8";
public static final double EPSILON = 1e-10;

// Constantes métier
static final int DEFAULT_WINDOW_WIDTH = 800;
static final int DEFAULT_WINDOW_HEIGHT = 600;

// ❌ MAUVAIS
static final int maxCategories = 25;  // camelCase
public final int MAX = 25;  // Non descriptif
static int MAX_CATEGORIES = 25;  // Pas final
```

### 5. Formatage et indentation

**Indentation** : **4 espaces** (pas de tabs)

```java
// ✅ BON
public class Example {
    private int field;

    public void method() {
        if (condition) {
            for (int i = 0; i < 10; i++) {
                statement();
            }
        }
    }
}

// ❌ MAUVAIS
public class Example {
  private int field;  // 2 espaces

  public void method() {
      if (condition) {  // 4 espaces pour if, mais incohérent
          statement();
      }
  }
}
```

**Accolades** : Style égyptien (ouvrante sur la même ligne)

```java
// ✅ BON
public class MyClass {
    public void method() {
        if (condition) {
            statement();
        }
    }
}

// ❌ MAUVAIS (style Allman)
public class MyClass
{
    public void method()
    {
        if (condition)
        {
            statement();
        }
    }
}
```

**Espaces** : Autour des opérateurs et après les virgules

```java
// ✅ BON
int result = a + b - c * d;
if (x > 0 && y < 10) { }
List<String> items = Arrays.asList("a", "b", "c");
methodCall(param1, param2, param3);

// ❌ MAUVAIS
int result=a+b-c*d;
if(x>0&&y<10) { }
List<String> items = Arrays.asList("a","b","c");
methodCall(param1,param2,param3);
```

**Longueur de ligne** : Maximum **120 caractères**

```java
// ✅ BON
String longMessage = String.format(
    "The value %s is invalid for column %s with type %s",
    value, columnName, columnType);

// ❌ MAUVAIS
String longMessage = String.format("The value %s is invalid for column %s with type %s", value, columnName, columnType);
```

### 6. Importations

**Règle** : Alphabétiques, organisées par groupe, explicites (pas de wildcards)

```java
// ✅ BON
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.univrennes.istic.l2gen.application.core.filter.Filter;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.gui.dialog.input.InputIntDialog;

// ❌ MAUVAIS
import java.util.*;  // Wildcard
import fr.univrennes.istic.l2gen.application.*;  // Wildcard
import java.util.List;
import java.util.ArrayList;  // Non-alphabétique

import fr.univrennes.istic.l2gen.application.gui.dialog.input.InputIntDialog;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;  // Mal organisé
```

### 7. Modificateurs d'accès

**Règle** : Explicite, préférer `private` par défaut

```java
// ✅ BON
public class TableColumnContextMenu extends JPopupMenu {
    private final DataTable table;
    private final int tableIndex;

    public void refresh() { }  // API publique

    private JMenu buildFilterMenu() { }  // Interne
    private void updateUI() { }  // Interne
}

// ❌ MAUVAIS
public class TableColumnContextMenu extends JPopupMenu {
    DataTable table;  // Package-private (implicite)
    int tableIndex;  // Pas final (mutabilité)

    void refresh() { }  // Package-private (non intentionnel ?)
}
```

**Final sur les classes** : Quand pas d'héritage prévu

```java
// ✅ BON
public final class Point implements IShape { }
public final class TableColumnContextMenu extends JPopupMenu { }

// ❌ MAUVAIS
public class Point implements IShape { }  // Pas final (peut être subclassé accidentellement)
```

### 8. Javadoc et commentaires

**Javadoc en français** : Obligatoire pour toute API publique

```java
// ✅ BON
/**
 * Ajoute un filtre à la table de données.
 *
 * Le filtre est appliqué immédiatement et met à jour
 * l'affichage du tableau.
 *
 * @param filter le filtre à ajouter
 * @throws IllegalArgumentException si le filtre est null
 */
public void addFilter(Filter filter) {
    // ...
}

// ❌ MAUVAIS
public void addFilter(Filter filter) {  // Pas de Javadoc
    // ...
}

/** Adds a filter */  // En anglais
public void addFilter(Filter filter) { }

/** @param f the filter */  // Pas d'explication
public void addFilter(Filter f) { }
```

**Commentaires explicatifs** : Au-dessus du code, brefs

```java
// ✅ BON
// Limiter le nombre de catégories pour éviter un menu trop grand
if (categories.size() > MAX_CATEGORIES) {
    // Utiliser un dialogue de sélection au lieu d'un sous-menu
    showSelectionDialog(categories);
}

// ❌ MAUVAIS
if (categories.size() > MAX_CATEGORIES) {  // if categories > 25
    showSelectionDialog(categories);  // show dialog
}

// TODO: optimize this later
for (int i = 0; i < 1000000; i++) {  // Commentaire interne
    // ...
}
```

**@Override systématique** : Sur toutes les méthodes surchargées

```java
// ✅ BON
@Override
public String toString() {
    return String.format("Point(%f, %f)", x, y);
}

@Override
public boolean equals(Object obj) {
    if (!(obj instanceof Point)) return false;
    Point other = (Point) obj;
    return x == other.x && y == other.y;
}

// ❌ MAUVAIS
public String toString() {  // Pas @Override
    return String.format("Point(%f, %f)", x, y);
}
```

### 9. Gestion des exceptions

**Exceptions spécifiques** : Attraper les exceptions les plus précises possibles

```java
// ✅ BON
try {
    int value = Integer.parseInt(input);
    table.addFilter(Filter.topN(columnIndex, value));
} catch (NumberFormatException e) {
    showErrorDialog("Veuillez entrer un nombre entier");
    Log.warn("Invalid integer input: " + input);
} catch (IllegalArgumentException e) {
    showErrorDialog("Paramètres invalides: " + e.getMessage());
    Log.error("Invalid filter parameters", e);
}

// ❌ MAUVAIS
try {
    // ...
} catch (Exception e) {  // Trop général
    // Silent failure - pas bon
}

try {
    // ...
} catch (Exception ignored) {  // Peut cacher des bugs
}
```

**Exceptions ignorées** : Variable nommée `ignored`, rare et justifié

```java
// ✅ BON (quand vraiment OK d'ignorer)
try {
    // Tentative de cleanup optionnel
    resource.close();
} catch (IOException ignored) {
    // Le resource n'existe peut-être pas, c'est OK
}

// ❌ MAUVAIS
try {
    // ...
} catch (Exception e) {
    e.printStackTrace();  // Jamais!
}
```

### 10. Organisation d'une classe

**Ordre typique des membres** :

```java
import statements;
import ...;

/**
 * Javadoc de la classe.
 */
@Annotations
public class MyClass extends Parent implements Interface1, Interface2 {

    // 1. Constantes statiques
    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_NAME = "default";

    // 2. Champs d'instance (avec annotations)
    private int count;
    private String name;
    private List<Item> items;

    // 3. Constructeurs
    public MyClass() {
        this(DEFAULT_NAME, 0);
    }

    public MyClass(String name) {
        this(name, 0);
    }

    public MyClass(String name, int initialCount) {
        this.name = name;
        this.count = initialCount;
        this.items = new ArrayList<>();
    }

    // 4. Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    // 5. Méthodes publiques
    public void add(Item item) {
        items.add(item);
        count++;
    }

    public void remove(Item item) {
        items.remove(item);
        count--;
    }

    // 6. Méthodes surchargées (@Override)
    @Override
    public String toString() {
        return String.format("%s (count: %d)", name, count);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyClass)) return false;
        MyClass other = (MyClass) obj;
        return name.equals(other.name) && count == other.count;
    }

    // 7. Méthodes privées/helper
    private void validate() {
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
    }
}
```

## Workflow de contribution

### 1. Créer une branche pour votre feature

```bash
# À jour depuis upstream
git fetch upstream
git rebase upstream/main

# Créer une branche feature
git checkout -b feature/description-courte
```

**Conventions de noms de branches** :

- `feature/` : Nouvelle fonctionnalité
- `fix/` : Correction de bug
- `docs/` : Documentation
- `refactor/` : Refactorisation
- `test/` : Ajout de tests

Exemples :

```bash
git checkout -b feature/add-correlation-filter
git checkout -b fix/table-update-not-showing
git checkout -b docs/update-api-docs
git checkout -b refactor/simplify-filter-logic
```

### 2. Développer votre feature

```bash
# Travailler sur votre branche
# Commits réguliers
git add .
git commit -m "feat: brève description"

# Avant de pusher : mettre à jour depuis upstream
git fetch upstream
git rebase upstream/main
```

### 3. Messages de commit

**Format** : `type: description`

Types :

- `feat` : Nouvelle fonctionnalité
- `fix` : Correction de bug
- `docs` : Documentation
- `style` : Formatage, points-virgules (pas de logique)
- `refactor` : Refactorisation sans changement de comportement
- `perf` : Amélioration de performance
- `test` : Ajout/modification de tests
- `chore` : Maintenance (dépendances, config)

**Exemples** :

```bash
git commit -m "feat: add correlation filter for numeric columns"
git commit -m "fix: menu items not displayed when updated in SwingWorker"
git commit -m "docs: add complete API documentation"
git commit -m "test: add unit tests for FilterBuilder"
git commit -m "refactor: simplify StatisticService with streams"
```

**Corps du message** (optionnel mais recommandé pour les changements complexes) :

```
feat: add support for correlation statistics

- Implements Pearson correlation coefficient calculation
- Adds correlation menu item to column context menu
- Handles edge cases (constant columns, null values)
- Includes comprehensive tests

Fixes #42
```

### 4. Tester localement

```bash
# Construire et tester
mvn clean verify

# Exécuter les tests spécifiques
mvn test -Dtest=StatisticServiceTest

# Lancer l'application
mvn exec:java

# Vérifier la couverture de code
mvn jacoco:report
```

### 5. Pousser et créer une Pull Request

```bash
# Pusher votre branche
git push origin feature/description-courte

# Puis créer une PR via GitHub
```

**Template de PR** :

```markdown
## Description

Brève description de la feature ou du fix.

## Type de changement

- [ ] Nouvelle fonctionnalité
- [ ] Correction de bug
- [ ] Documentation
- [ ] Refactorisation
- [ ] Amélioration de performance

## Tests effectués

- [ ] Tests unitaires écrits/modifiés
- [ ] Tests manuels effectués
- Décrire les scénarios testés...

## Screenshots (si applicable)

Ajouter des screenshots pour les changements GUI.

## Checklist

- [ ] Mon code suit les conventions de style du projet
- [ ] J'ai ajouté de la documentation (Javadoc)
- [ ] Je n'ai pas introduit de regressions
- [ ] Les tests passent localement

## Lié à

Closes #42
```

## Tests et qualité

### Structure des tests

```
test/fr/univrennes/istic/l2gen/application/
├── geometry/
│   ├── AbstractShapeTest.java      # Tests de base
│   ├── PointTest.java
│   ├── CircleTest.java
│   └── ...
├── io/
│   └── svg/
│       └── SVGExportTest.java
├── visustats/
│   └── ...
└── application/
    └── core/
        ├── FilterTest.java
        └── StatisticServiceTest.java
```

### Écrire des tests

**Conventions de test** :

```java
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Tests pour la classe Point.
 */
public class PointTest extends AbstractShapeTest<Point> {

    private Point point;

    /**
     * Initialisation avant chaque test.
     */
    @Before
    public void setUp() {
        point = new Point(3.0, 4.0);
    }

    /**
     * Crée une instance de Point pour les tests.
     * @return une nouvelle instance de Point
     */
    @Override
    protected Point create() {
        return new Point(0.0, 0.0);
    }

    /**
     * Test de la distance entre deux points.
     */
    @Test
    public void testDistance() {
        Point other = new Point(6.0, 8.0);
        double distance = point.distance(other);
        assertEquals(5.0, distance, 1e-10);  // Avec tolérance
    }

    /**
     * Test du calcul de vecteur.
     */
    @Test
    public void testVector() {
        Point origin = new Point(0.0, 0.0);
        Point result = point.sub(origin);
        assertEquals(3.0, result.getX(), 1e-10);
        assertEquals(4.0, result.getY(), 1e-10);
    }

    /**
     * Test d'égalité de points.
     */
    @Test
    public void testEquality() {
        Point other = new Point(3.0, 4.0);
        assertTrue(point.equals(other));
    }

    /**
     * Test des valeurs limites.
     */
    @Test
    public void testBoundaryValues() {
        Point zero = new Point(0.0, 0.0);
        Point huge = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        // ...
    }
}
```

**Couverture de code** : Minimum **80%**

```bash
# Générer un rapport de couverture
mvn jacoco:report

# Voir le résultat
open target/site/jacoco/index.html
```

### Exécuter les tests

```bash
# Tous les tests
mvn test

# Test spécifique
mvn test -Dtest=PointTest

# Test avec un pattern
mvn test -Dtest=*ServiceTest

# Avec verbose output
mvn test -X

# Arrêter à la première erreur
mvn test -f
```

## Documentation

### Javadoc obligatoire

**Classes publiques** :

```java
/**
 * Représentation d'un point en 2D.
 *
 * Un point est défini par ses coordonnées (x, y).
 * Cette classe fournit des opérations vectorielles courantes.
 */
public class Point implements IShape { }
```

**Méthodes publiques** :

```java
/**
 * Calcule la distance entre ce point et un autre.
 *
 * @param other le point avec lequel calculer la distance
 * @return la distance euclidienne entre les deux points
 * @throws NullPointerException si other est null
 */
public double distance(Point other) { }
```

**Champs publics** (si nécessaire) :

```java
/**
 * L'index de la colonne active.
 */
private int columnIndex;
```

### Documentation dans le code

Ajouter une section "Documentation" à votre PR décrivant :

- Nouvelles classes/méthodes publiques
- APIs modifiées
- Changements de comportement

```markdown
## Documentation

### Nouvelles méthodes

- `Filter.correlation(int col1, int col2)` : Filtre par corrélation entre deux colonnes
- `StatisticService.computeCorrelation()` : Calcule la corrélation de Pearson

### APIs modifiées

- `TableColumnContextMenu.buildStatsMenu()` : Ajout du menu "Corrélation"
```

## Process de review

### Checklist pour le revieweur

- ✅ Code suit les conventions du projet
- ✅ Javadoc complète pour l'API publique
- ✅ Tests passent et couverture > 80%
- ✅ Pas de regressions
- ✅ Performance acceptable
- ✅ Messages de commit clairs
- ✅ Pas de code mort
- ✅ Gestion d'erreurs appropriée

### Demander des modifications

Si des modifications sont demandées :

```bash
# Apporter les modifications
git add .
git commit -m "fix review comments"

# Ou amender le dernier commit
git add .
git commit --amend --no-edit
git push -f origin feature/description-courte
```

### Fusionner votre PR

Une fois approuvée :

```bash
# Votre PR sera fusionnée via l'interface GitHub
# (généralement fait par un mainteneur)

# Nettoyer localement
git checkout main
git pull upstream main
git branch -d feature/description-courte
```

## Signaler des problèmes

### Avant de signaler

- ✅ Vérifier que le problème n'existe pas déjà
- ✅ Tester avec la dernière version
- ✅ Tester avec une nouvelle branche clean
- ✅ Vérifier la documentation

### Signaler un bug

Titre clair et concis :

```
"Le menu du filtre ne s'affiche pas après mise à jour"
```

**Template** :

```markdown
## Description

Le menu de filtre n'affiche pas les nouvelles options après que le SwingWorker
ajoute dynamiquement des éléments au menu.

## Reproduction

1. Ouvrir le fichier CSV avec 1000+ lignes
2. Clic droit sur colonne → Filter
3. Attendre que les catégories se chargent
4. Les nouvelles options ne s'affichent pas

## Comportement observé

- Menu reste vide/incomplet
- Pas d'erreur dans les logs

## Comportement attendu

- Menu doit afficher les options de filtre après chargement

## Environnement

- OS: Linux
- Java: JDK 21.0.3
- Maven: 3.9.0
- Version Pangol1: main branch

## Logs

[Coller les logs pertinents ici]
```

### Proposer une amélioration

```markdown
## Description

Ajouter le calcul de la corrélation de Pearson entre deux colonnes numériques.

## Motivation

Pour analyser la relation linéaire entre deux variables quantitatives.

## Solution proposée

1. Ajouter `StatisticService.computeCorrelation(table, col1, col2)`
2. Ajouter menu "Corrélation" dans le contexte colonne
3. Afficher dans un dialogue

## Cas d'usage

Données de ventes : corréler le prix avec les ventes pour voir la relation.

## Alternatives

- Utiliser une tool externe comme RStudio
- Implémenter directement dans Excel

## Complexité estimée

Moyenne (une journée de développement)
```

## Ressources

### Liens utiles

- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Git Guide](https://git-scm.com/book)
- [Maven Guide](https://maven.apache.org/guides/)
- [Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
- [JUnit 4 Documentation](https://junit.org/junit4/)

### Documentation du projet

- [README.md](README.md) - Présentation
- [docs/DOCUMENTATION.md](docs/DOCUMENTATION.md) - Documentation complète
- [MEMBERS.md](docs/MEMBERS.md) - Équipe
- [UML Diagrams](uml/) - Diagrammes d'architecture

### Outils recommandés

**Extension VS Code** :

- Extension Pack for Java
- Maven for Java
- Code Spell Checker
- SonarLint (pour la qualité)

**Configuration VS Code** (`.vscode/settings.json`) :

```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-21",
      "path": "/path/to/jdk21",
      "default": true
    }
  ],
  "[java]": {
    "editor.defaultFormatter": "redhat.java",
    "editor.formatOnSave": true,
    "editor.tabSize": 4,
    "editor.insertSpaces": true
  }
}
```

**Merci pour votre contribution à Pangol1 ! 🚀**

Vous trouverez que ce projet suit des standards de code élevés. Votre respect de ces conventions garantit une base de code maintenable, cohérente et professionnelle.
