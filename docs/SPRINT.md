# 🚀 Suivi des Sprints : Projet Pangol1

## 📅 Sprint 1 : Architecture Core & Gestion de Données

**Objectif :** Mettre en place l'importation, la structure de la table et le moteur de filtrage de base.

| ID   | Tâche Technique                                          |  Lien US  | Complexité | Status  |
| :--- | :------------------------------------------------------- | :-------: | :--------: | :-----: |
| #101 | Implémentation du sélecteur de fichier (Local & URL)     | **US 01** |    3/5     | ✅ FAIT |
| #102 | Développement du moteur de tri par en-tête (Header)      | **US 02** |    3/5     | ✅ FAIT |
| #103 | Système de filtres avancés (Plages, Texte, Top/Bottom N) | **US 02** |    4/5     | ✅ FAIT |
| #104 | Algorithme de détection automatique des types (CSV)      | **US 01** |    3/5     | ✅ FAIT |

**✅ DoD (Definition of Done) :**

- Les données s'affichent correctement dans la grille.
- La recherche textuelle et les filtres numériques mettent à jour la vue instantanément.

---

## 📅 Sprint 2 : Visualisation & Internationalisation

**Objectif :** Développer le rendu graphique et le support multilingue initial.

| ID   | Tâche Technique                                       |  Lien US  | Complexité | Status  |
| :--- | :---------------------------------------------------- | :-------: | :--------: | :-----: |
| #201 | Moteur de rendu vectoriel (Pie, Bar, Columns)         | **US 04** |    5/5     | ✅ FAIT |
| #202 | Système de traduction dynamique (9 langues)           | **US 03** |    3/5     | ✅ FAIT |
| #203 | Module de statistiques descriptives (Summary Window)  | **US 05** |    3/5     | ✅ FAIT |
| #204 | Panneau de personnalisation (Couleurs, Opacité, Axes) | **US 04** |    2/5     | ✅ FAIT |
| #205 | Export de données dans un format choisis              | **US 04** |    3/5     | ✅ FAIT |

**✅ DoD :**

- Basculement instantané entre les 9 langues via le menu Help.
- Le clic droit sur une colonne affiche les agrégations (Moyenne, Somme).

---

## 📅 Sprint 3 : Qualité, Optimisation & Finalisation

**Objectif :** Fiabiliser le code, optimiser les performances et produire la documentation.

| ID   | Tâche Technique                                          |  Lien US  | Complexité | Status  |
| :--- | :------------------------------------------------------- | :-------: | :--------: | :-----: |
| #301 | Mise en place des Tests Unitaires (Calculs & Import)     | **US 07** |    4/5     | ✅ FAIT |
| #302 | Optimisation des boucles de traitement (Refactoring)     | **US 08** |    3/5     | ✅ FAIT |
| #303 | Automatisation du Mode Nuit (18h-6h)                     | **US 06** |    2/5     | ✅ FAIT |
| #304 | Rédaction du Manuel Utilisateur (LaTeX)                  | **US 09** |    3/5     | ✅ FAIT |
| #305 | Gestion des fenêtres détachables (View > Panels)         | **US 07** |    3/5     | ✅ FAIT |
| #306 | Debug et test de l'application en poussant sa technicité | **US 07** |    3/5     | ✅ FAIT |

**✅ DoD :**

- Temps de réponse du logiciel < 500ms sur des fichiers standards.
- Aucune erreur détectée lors des tests de chargement de fichiers corrompus.

---

## 📅 Sprint 4 : Performance, Accessibilité & Excellence Technique

**Objectif :** Transformer le prototype en solution industrielle robuste pour gros volumes.

| ID   | Tâche Technique                                                                 |   Lien US    | Complexité | Status  |
| :--- | :------------------------------------------------------------------------------ | :----------: | :--------: | :-----: |
| #401 | **Streaming Data** : Lecture par blocs pour fichiers > 1Go sans saturation RAM. | **US 01/08** |    5/5     | ✅ FAIT |
| #402 | **Cross-Filtering** : Filtrage interactif bidirectionnel Table ↔ Graphiques.    | **US 02/04** |    4/5     | ✅ FAIT |
| #405 | **Stress Testing CI/CD** : Pipeline de tests automatisés (1M de lignes).        |  **US 07**   |    4/5     | ✅ FAIT |
| #406 | **Documentation API** : Génération auto (Doxygen) pour les développeurs.        |  **US 09**   |    2/5     | ✅ FAIT |
