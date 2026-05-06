# 📋 Backlog Produit : Projet Pangol1

## 🏗️ Groupe 1 : Gestion des Données & Core Architecture

### **US 01** | Importation Multi-Sources

**En tant qu'** analyste de données,  
**je veux** charger un fichier CSV local ou importer des données via une URL,  
**afin de** visualiser instantanément les données dans l'application.

- **Critères d'acceptation :**
  - Support du chargement local et distant.
  - La barre d'état confirme le statut "Ready" après l'import.

### **US 02** | Filtrage Dynamique et Logique

**En tant qu'** utilisateur,  
**je veux** appliquer des filtres complexes (AND, OR, NOT) et des tris sur les colonnes,  
**afin d'** isoler les données pertinentes pour mon analyse.

- **Critères d'acceptation :**
  - Clic droit sur l'en-tête pour le tri (Asc/Desc).
  - Accès au menu "Filters" pour les plages numériques et textuelles.

## 🌍 Groupe 2 : Internationalisation (i18n)

### **US 03** | Support Multilingue Intégré

**En tant qu'** utilisateur international,  
**je veux** pouvoir changer la langue de l'interface à tout moment via le menu `Help > Languages`.

- **Langues supportées :** Français 🇫🇷, Anglais 🇬🇧, Espagnol 🇪🇸, Arabe 🇸🇦, Chinois 🇨🇳, Portugais 🇵🇹, Russe 🇷🇺, Japonais 🇯🇵, Indonésien 🇮🇩 ( 10 plus parlé dans le monde ).

## 📊 Groupe 3 : Visualisation & Statistiques

### **US 04** | Rendu Graphique Vectoriel

**En tant que** responsable d'équipe,  
**je veux** générer des graphiques de type Camembert (Pie), Barres (Bar) ou Colonnes (Columns),  
**afin de** produire des rapports visuels clairs.

### **US 05** | Analyse Statistique (Summary)

**En tant qu'** utilisateur,  
**je veux** voir un résumé statistique d'une colonne (Type, Null Rate, Moyenne, Somme),  
**afin de** comprendre rapidement la structure de mes données.

## ⚙️ Groupe 4 : Qualité & Performance (Agile, Tests, Optimisation)

### **US 06** | Méthodologie Agile & Suivi de Projet

**En tant que** développeur,  
**je veux** travailler par itérations (Sprints) et découper mes tâches,  
**afin de** livrer des fonctionnalités testées et fonctionnelles à chaque fin de cycle.

- **Critères d'acceptation :**
  - Utilisation d'un fichier `SPRINT.md` mis à jour quotidiennement.
  - Définition claire des "DoD" (Definition of Done) pour chaque tâche.

### **US 07** | Fiabilité par les Tests

**En tant que** responsable qualité,  
**je veux** implémenter des tests unitaires et d'intégration,  
**afin de** garantir qu'aucune régression n'apparaît lors du développement.

- **Critères d'acceptation :**
  - Validation des algorithmes de calcul statistique (Moyenne, Somme).
  - Test de l'importation de fichiers malformés (gestion des erreurs).

### **US 08** | Optimisation du Code et Refactoring

**En tant que** développeur,  
**je veux** optimiser le moteur de traitement et nettoyer le code,  
**afin de** garantir que l'application reste fluide même avec des milliers de lignes.

- **Critères d'acceptation :**
  - Suppression du code mort et des commentaires de debug.
  - Optimisation des boucles de traitement des données vectorielles.
  - Temps de chargement inférieur à 500ms pour les fichiers standard.

## 📖 Groupe 5 : Documentation Finale

### **US 09** | Manuel Utilisateur en LaTeX

**En tant qu'** utilisateur final,  
**je veux** consulter un manuel PDF avec un sommaire cliquable.  
**afin de** maîtriser l'outil en toute autonomie.
