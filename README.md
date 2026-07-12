<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Suivi Hydratation

Application Android de suivi de la consommation d'eau quotidienne, développée avec Jetpack Compose.

## Fonctionnalités

- Enregistrement de la quantité d'eau bue (en ml)
- Objectif journalier configurable
- Visualisation de la progression via un cercle animé
- Historique des entrées du jour
- Persistance des données avec Room (base de données locale)

## Installation

```bash
git clone https://github.com/bley-abel/Hydratation_Tracker
```

Ouvrir le dossier dans Android Studio, laisser le sync Gradle se terminer, puis lancer l'app sur un émulateur ou un appareil physique.

## Structure du projet

```
app/src/main/java/com/example/
├── MainActivity.kt
├── data/
│   ├── WaterDatabase.kt
│   ├── WaterLog.kt
│   ├── WaterLogDao.kt
│   └── WaterRepository.kt
└── ui/
    ├── WaterTrackerScreen.kt
    ├── WaterViewModel.kt
    └── theme/
```

## Origine

Projet généré via [Google AI Studio](https://aistudio.google.com) dans le cadre d'un exercice d'utilisation d'Android Studio.
