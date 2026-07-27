# Générer la démo Openflexo dans Claude Code

Deux parties : un **descriptif technique** (ce qu'il faut produire, indépendant de la
syntaxe) et le **prompt** à coller dans Claude Code, écrit pour éviter l'invention de
syntaxe FML.

---

# Partie A — Descriptif technique

## A.1 Objectif

Produire un projet Openflexo qui fédère deux artefacts hétérogènes en un jumeau numérique
d'une ligne de conditionnement, et qui dérive une hiérarchie de priorités de maintenance
qu'aucun artefact ne porte seul. Cible : une démo de 20 minutes, trois manipulations en
direct.

## A.2 Entrées (déjà construites, ne pas régénérer)

- `ligne.xml` — topologie de la ligne, sous-ensemble CAEX/AutomationML, 6 équipements,
  6 liaisons, cadences et seuils nominaux.
- `ligne.xsd` — schéma du précédent, référencé par `xsi:noNamespaceSchemaLocation`,
  contraintes `key`/`keyref` sur les liaisons.
- `exploitation.xlsx` — 3 feuilles (`Parc`, `Relevés`, `Interventions`), tenu par la
  maintenance, aucune formule.

La correspondance de jointure et la logique de dérivation sont spécifiées dans
`specification-demo.md` (fourni). C'est la source de vérité fonctionnelle.

## A.3 Sortie attendue

Un projet Openflexo exécutable, contenant au minimum un ViewPoint et un VirtualModel qui
fédère les deux ressources, avec les concepts, rôles, propriétés dérivées et actions
décrits ci-dessous. Livré avec un mode opératoire pas-à-pas pour les trois manipulations.

## A.4 Architecture cible

Deux model slots dans le VirtualModel :

- un model slot **XML/XSD** monté sur `ligne.xml` (métamodèle : `ligne.xsd`) ;
- un model slot **Excel** monté sur `exploitation.xlsx`.

Trois FlexoConcepts :

- **`Equipement`** — le concept fédératif. Rôles : un pointeur vers l'`InternalElement`
  XML, un pointeur vers la ligne `Parc` correspondante, et l'accès à ses relevés. La
  jointure se fait sur le code, avec la règle de transformation `EQ-DEP-01` ↔ `DEP01`
  (retrait du préfixe `EQ-` et des tirets) — voir §2 de la spec.
- **`Liaison`** — issu du seul XML (`InternalLink`).
- **`Ligne`** — racine, porte les propriétés dérivées transverses.

Propriétés dérivées (calculées, présentes dans aucun artefact) — logique complète en §3
de la spec :

- `estCritique` : aucun chemin entrée→sortie n'évite l'équipement (parcours de graphe sur
  les `InternalLink`) ;
- `estGoulot` : cadence nominale minimale parmi les critiques ;
- `tauxSeuil` : dernière vibration (Excel) ÷ `SeuilVibration` (XML) ;
- `enDérive` : pente positive sur 4 relevés **et** `tauxSeuil` ≥ 0,8 ;
- `criticitéEffective` : `estCritique` ∧ `enDérive`, aggravée si `estGoulot`.

Une action d'écriture en retour : écrire `criticitéEffective` dans la colonne `H` de la
feuille `Parc` (laissée libre à cet effet).

## A.5 Contraintes et pièges

- **Jointure sur code, pas sur libellé** : les libellés diffèrent volontairement des deux
  côtés. La règle de transformation de code doit être explicite et révisable.
- **Propriétés CAEX génériques** : côté XML, les propriétés sont des couples
  `<Attribute Name Value/>`, donc l'accès ressemble à `attributes[name='SeuilVibration'].value`.
  Décider avant de coder si on garde cette forme (fidèle) ou si on promeut les 4 propriétés
  en attributs XML natifs (plus lisible en projection). Ce choix change toutes les
  expressions — voir §7 de la spec.
- **En-tête décalé de la feuille `Parc`** (titre en lignes 1–2, en-têtes en ligne 4) : le
  model slot Excel doit démarrer à la bonne ligne, ou bien on supprime les deux premières
  lignes.
- **Incohérences plantées** volontairement (`EQ-ETI-04B` absent du parc ; code `CONV3`
  orphelin au 10/07) : ne pas les « réparer », elles servent la démo.
- **Aucune criticité stockée** dans les artefacts : elle est toujours dérivée de la
  topologie, sinon la démo perd son sens.

## A.6 Les trois manipulations à outiller

1. Question transverse → liste priorisée, `CONV03` en tête (§5 de la spec).
2. Ajout d'un relevé `4,9 mm/s` sur `CONV03` dans Excel → l'alerte monte ; puis écriture
   en retour de `criticitéEffective` dans `Parc!H`.
3. Ajout du by-pass `L7` (`EQ-REM-02:OUT → EQ-ETI-04A:IN`) dans le XML → `CONV03` cesse
   d'être critique, l'alerte se déclasse (elle ne disparaît pas).

## A.7 Où porte la validation

La syntaxe FML n'est pas le risque ici : l'agent sait écrire, valider et exécuter du FML.
Le risque résiduel est fonctionnel — que le modèle s'exécute mais calcule faux. La
validation porte donc sur les **valeurs métier attendues**, pas sur la compilation : à
chaque jalon, on compare le calcul obtenu aux valeurs connues de la spec (§3, §5) avant de
continuer. Un préalable subsiste, indépendant du FML : fixer la boucle d'exécution dans
l'environnement de travail (comment on lance et on inspecte un projet).

## A.8 Découpage incrémental (jalons vérifiables)

- **J0** — boucle de validation établie ; un projet Openflexo vide s'ouvre/s'exécute.
- **J1** — les deux model slots montent les ressources ; on lit une valeur de chaque côté.
- **J2** — concept `Equipement` fédéré, jointure sur code fonctionnelle sur les 5
  équipements appariables.
- **J3** — propriétés topologiques (`estCritique`, `estGoulot`) correctes.
- **J4** — propriétés transverses (`tauxSeuil`, `enDérive`, `criticitéEffective`).
- **J5** — manipulation 1 (liste priorisée) ; `CONV03` sort en tête, `ETI04A` non.
- **J6** — manipulations 2 et 3 outillées et rejouables.

Chaque jalon est validé sur des valeurs attendues connues (la spec les donne) avant de
passer au suivant.

---

# Partie B — Le prompt

> À coller dans Claude Code, à la racine d'un dossier contenant les quatre fichiers
> (`ligne.xml`, `ligne.xsd`, `exploitation.xlsx`, `specification-demo.md`). Adapter le
> premier paragraphe à votre environnement réel (version d'Openflexo, présence de l'IDE,
> projet existant éventuel).

```text
Contexte. Je prépare une démonstration d'Openflexo (fédération de modèles) appliquée aux
jumeaux numériques. Nous allons travailler dans le projet openflexo-integration-tests, 
et plus particulièrement dans le resource center integration-tests-rc, dans 
src/main/resources/DigitalTwinDemo. Les artefacts sont déjà dans ce dossier : ligne.xml (topologie d'une
ligne de conditionnement, sous-ensemble CAEX/AutomationML), ligne.xsd (son schéma),
exploitation.xlsx (données de maintenance, 3 feuilles), claude-code-brief.md et specification-demo.md qui est la
SOURCE DE VÉRITÉ FONCTIONNELLE — lis-le en entier avant toute chose. Le but est un projet
Openflexo qui fédère le XML et l'Excel en un jumeau numérique et dérive une hiérarchie de
priorités de maintenance.

Étape 1 — boucle d'exécution. Établis d'abord comment on lance et inspecte un projet dans
cet environnement (exécution FML en ligne de commande, FML-rt, ou l'outil graphique de mon
côté). Propose la boucle la plus légère et confirme-la avec moi si c'est ambigu, pour qu'on
puisse vérifier chaque jalon au fur et à mesure plutôt qu'à la fin.

Étape 2 — architecture, à me faire valider AVANT de coder. Sur la base de la spec §3, §5, §7,
propose : les deux model slots (XML/XSD sur ligne.xml + ligne.xsd ; Excel sur
exploitation.xlsx) ; les trois FlexoConcepts (Equipement fédératif, Liaison, Ligne) ; la
jointure sur code avec la règle EQ-DEP-01 ↔ DEP01. Et tranche avec moi la question du §7 :
garder les Attribute CAEX génériques (fidèle, verbeux) ou promouvoir les 4 propriétés en
attributs XML natifs (lisible en projection). Ce choix change toutes les expressions de
dérivation — recommande une option, mais laisse-moi décider.

Étape 3 — construction incrémentale, validée sur les VALEURS MÉTIER. Suis les jalons J1→J6
de la spec §A.8, un par un. À chaque jalon, calcule les valeurs obtenues et compare-les aux
valeurs attendues de la spec, qui sont le vrai test — pas la compilation :
- estCritique VRAI pour CONV03, FAUX pour ETI04A (car doublée par ETI04B) ;
- CONV03 goulot (cadence nominale 950, la plus basse des critiques) ;
- CONV03 et ETI04A tous deux à ~93 % de leur seuil de vibration (l'égalité est voulue) ;
- liste priorisée : CONV03 en tête, ETI04A pas dans les priorités malgré sa vibration élevée.
Ne passe au jalon suivant qu'une fois le précédent vérifié sur ces valeurs. Si un chiffre ne
tombe pas juste, signale-le au lieu de l'absorber.

Étape 4 — les trois manipulations. Outille-les pour qu'elles soient rejouables en direct
(spec §A.6) et écris un mode opératoire pas-à-pas : quel fichier on édite, quelle valeur on
saisit, quoi rafraîchir, quel résultat attendre. Pour la manipulation 3, formule bien que
l'alerte se DÉCLASSE (elle ne disparaît pas). Génère-moi également les scripts fml-script 
associés (pour pouvoir automatiser la démo)

Contraintes transverses. Ne régénère pas les artefacts. Ne « répare » pas les incohérences
plantées (EQ-ETI-04B absent du parc, code CONV3 orphelin). Ne stocke jamais la criticité dans
un artefact : elle est toujours dérivée. Travaille par petits incréments testés, et privilégie
un modèle lisible en projection sur un modèle malin.
```

---

## Note d'usage

Le prompt fait confiance à la maîtrise FML de l'agent et concentre l'effort de vérification
sur les valeurs métier, seul endroit où un modèle qui « compile » peut encore se tromper.

Deux points où votre expertise tranchera plus vite que Claude Code : le choix Attribute
générique vs attribut natif (étape 2), et la boucle d'exécution (étape 1) — si vous savez
déjà comment vous lancez vos projets, donnez-le d'emblée pour lui éviter d'explorer.
