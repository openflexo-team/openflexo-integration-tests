# Démo Openflexo — jumeau numérique d'une ligne de conditionnement

Note de spécification accompagnant `ligne.xml` et `exploitation.xlsx`.

---

## 1. Les deux artefacts

**`ligne.xml`** — 69 lignes, sous-ensemble d'AutomationML (`InstanceHierarchy` /
`InternalElement` / `ExternalInterface` / `InternalLink`). Propriétaire : bureau d'études
automatisme. Contient six équipements, leurs cadences nominales, leurs seuils de vibration
et de température, et six liaisons matière.

**`ligne.xsd`** — le schéma du précédent, référencé depuis le XML par
`xsi:noNamespaceSchemaLocation`. Sans espace de noms, avec des `complexType` globaux nommés
et des contraintes `key` / `keyref` qui garantissent l'intégrité référentielle des liaisons.
Voir §7.

**`exploitation.xlsx`** — trois feuilles. Propriétaire : service maintenance.

| Feuille | En-têtes en ligne | Contenu |
|---|---|---|
| `Parc` | 4 (titre en 1–2) | 5 équipements inventoriés |
| `Relevés` | 1 | 53 relevés horodatés du 05/06 au 17/07/2026 |
| `Interventions` | 1 | 6 interventions datées |

Le décalage d'en-tête de la feuille `Parc` est volontaire : c'est ce qu'on trouve dans un
vrai classeur. Il est trivial à absorber dans le model slot Excel — supprimez les deux
premières lignes si vous préférez ne pas en parler.

**Aucune formule dans le classeur.** C'est un choix de conception, pas un oubli : tout le
calcul dérivé appartient au modèle virtuel. Si un calcul migrait dans Excel, l'argument de
la démo s'affaiblirait.

---

## 2. Correspondance entre les deux artefacts

Clé de jointure : le code équipement, écrit différemment de part et d'autre.

| `ligne.xml` (`InternalElement/@ID`) | `exploitation.xlsx` (colonne `Code`) |
|---|---|
| `EQ-DEP-01` | `DEP01` |
| `EQ-REM-02` | `REM02` |
| `EQ-CONV-03` | `CONV03` |
| `EQ-ETI-04A` | `ETI04A` |
| `EQ-ETI-04B` | *(absent)* |
| `EQ-ENC-05` | `ENC05` |

La règle est régulière : retirer le préfixe `EQ-` et les tirets. Elle est volontairement
simple — l'intérêt pédagogique est qu'elle existe et soit **déclarée**, pas qu'elle soit
difficile.

Les libellés, eux, ne coïncident pas du tout (`ConvoyeurTransfert` côté XML,
`Convoyeur transfert étiqueteuses` côté Excel) : c'est ce qui montre qu'on ne peut pas
joindre sur les noms.

---

## 3. Concepts du modèle virtuel et règles de dérivation

Trois concepts, dont un seul est fédératif.

**`Équipement`** — fédère un `InternalElement` du XML, une ligne de `Parc`, l'ensemble de
ses lignes dans `Relevés` et dans `Interventions`.

**`Liaison`** — provient du seul XML (`InternalLink`).

**`Ligne`** — la racine, qui porte les propriétés dérivées.

Propriétés dérivées, calculées à la volée, présentes dans aucun artefact :

| Propriété | Règle | Source |
|---|---|---|
| `estCritique` | aucun chemin de l'entrée à la sortie ne l'évite | XML seul |
| `estGoulot` | cadence nominale minimale parmi les équipements critiques | XML seul |
| `tauxSeuil` | dernière vibration ÷ `SeuilVibration` | XML **et** Excel |
| `enDérive` | pente positive sur les 4 derniers relevés **et** `tauxSeuil` ≥ 0,8 | XML **et** Excel |
| `criticitéEffective` | `estCritique` **et** `enDérive`, aggravée si `estGoulot` | les deux |

Point de conception important : **la criticité n'est jamais déclarée, elle est dérivée de
la topologie.** Aucun attribut `PosteCritique` dans le XML — sans quoi la démo se réduirait
à une lecture d'attribut.

---

## 4. Ce qui est planté dans les données

C'est le cœur de la démonstration. Chaque équipement joue un rôle précis.

| Équipement | Topologie (XML) | Mesures (Excel) | Rôle dans la démo |
|---|---|---|---|
| `DEP01` | critique | 4,1 mm/s pour un seuil de 6,0 → 68 % | bruit de fond |
| `REM02` | critique | 2,6 mm/s pour un seuil de 5,0 → 52 % | critique mais sain |
| `CONV03` | critique **et goulot** (950 u/h) | 2,9 → 4,2 mm/s, seuil 4,5 → **93 %** | **la réponse** |
| `ETI04A` | **non critique** (doublée par `04B`) | 5,8 → 6,5 mm/s, seuil 7,0 → **93 %** | le leurre |
| `ETI04B` | non critique | **absente du parc** | l'incohérence |
| `ENC05` | critique | 3,3 mm/s pour un seuil de 5,5 → 60 % | bruit de fond |

Les trois lectures possibles ne donnent pas la même réponse, et c'est tout l'argument :

- **Excel seul, en valeur brute** : `ETI04A` est de loin la plus vibrante (6,5 contre 4,2)
  → mauvaise réponse.
- **Excel + seuils du XML** : `CONV03` et `ETI04A` sont toutes deux à 93 % de leur seuil
  → ex æquo, aucune décision possible.
- **Excel + seuils + topologie** : `ETI04A` est doublée, `CONV03` est le goulot que rien
  ne contourne → `CONV03`, sans ambiguïté.

L'égalité à 93 % est délibérée : elle interdit de trancher sur les seules mesures et rend
la topologie strictement nécessaire.

Deux corroborations discrètes, si quelqu'un creuse : la dernière intervention sur `CONV03`
remonte au 09/02, la plus ancienne du classeur ; `ETI04A` a été reprise le 28/06, très
récemment.

**Incohérences plantées**, à mentionner en une phrase sans les démontrer :
`EQ-ETI-04B` existe dans le XML et n'est pas dans le parc ; la feuille `Relevés` contient
une ligne au 10/07 codée `CONV3` (chiffre manquant) qui ne référence rien.

---

## 5. Les deux manipulations en direct

**Manipulation 2 — vivacité et écriture en retour.** Ajouter en fin de feuille `Relevés` :

```
24/07/2026 | CONV03 | Vibration | 4,9 | mm/s | MPR
```

`4,9 > 4,5` : `CONV03` passe de « sous surveillance » à « dépassement de seuil ». Préparez
le classeur avec le curseur déjà en `A56` et la valeur notée à côté de vous.

Puis le jumeau écrit la criticité effective dans la feuille `Parc` — colonne `H`, laissée
libre à cet effet.

**Manipulation 3 — reconfiguration.** Ajouter une seule ligne dans `ligne.xml`, à la suite
des `InternalLink` :

```xml
<InternalLink Name="L7" RefPartnerSideA="EQ-REM-02:OUT" RefPartnerSideB="EQ-ETI-04A:IN"/>
```

Ce by-pass crée un chemin `DEP01 → REM02 → ETI04A → ENC05` qui évite `CONV03`. Celle-ci
cesse d'être critique, alors que sa vibration reste au-dessus du seuil.

Formulez le résultat avec précision : **l'alerte ne disparaît pas, elle se déclasse.** On
passe d'un risque d'arrêt de ligne à une intervention à programmer au prochain arrêt
planifié. C'est plus juste, et bien plus convaincant, que de faire disparaître un voyant
rouge.

---

## 6. Points d'attention pour le passage au FML

- Les liaisons partagent leurs interfaces : `EQ-CONV-03:OUT` apparaît dans `L3` et `L4`.
  La navigation topologique doit donc passer par les `InternalLink`, pas supposer une
  interface par liaison.
- `estCritique` demande un parcours de graphe (existe-t-il un chemin entrée→sortie qui
  évite `e` ?). Sur six nœuds, une énumération naïve des chemins suffit largement.
- Les dates sont de vraies dates Excel, pas du texte.
- Les unités sont identiques des deux côtés (`mm/s`, `°C`) : pas de conversion à gérer,
  c'était une complication inutile pour vingt minutes.


---

## 7. Le schéma `ligne.xsd`

Cinq `complexType` globaux nommés — `CAEXFileType`, `InstanceHierarchyType`,
`InternalElementType`, `AttributeType`, `ExternalInterfaceType`, `InternalLinkType` — plutôt
que des types anonymes en ligne, pour que la dérivation de concepts depuis le schéma donne
des noms exploitables.

**Pas d'espace de noms.** `xsi:noNamespaceSchemaLocation` suffit et évite les préfixes dans
toutes les expressions de navigation. Le vrai CAEX en déclare un — si votre chaîne d'outils
le préfère, il suffit d'ajouter `targetNamespace` au schéma et de basculer sur
`xsi:schemaLocation`.

**Les identifiants sont des `xs:string`, pas des `xs:ID`.** Contrainte subie :
`EQ-DEP-01:OUT` contient un deux-points et n'est donc pas un `NCName` valide. L'intégrité
est assurée à la place par deux `xs:key` (unicité des équipements, unicité des interfaces)
et deux `xs:keyref` sur les extrémités des liaisons.

C'est vérifié : un `RefPartnerSideB` pointant vers une interface inexistante fait échouer la
validation, et le by-pass `L7` de la manipulation 3 reste valide.

**Un argument à garder en réserve.** Le schéma garantit la cohérence interne du XML — aucune
liaison ne peut pendre dans le vide. Mais aucun schéma ne peut garantir que `EQ-ETI-04B`
figure dans le parc Excel, ni que le code `CONV3` de la feuille `Relevés` corresponde à
quelque chose. La validation est confinée à une technologie ; c'est précisément l'espace que
la fédération occupe. Si quelqu'un demande « pourquoi ne pas se contenter d'un bon schéma ? »,
c'est la réponse.

**Conséquence pour le FML.** En CAEX, les propriétés sont des couples génériques
`<Attribute Name="…" Value="…"/>`. Un métamodèle dérivé du schéma vous donnera donc un
concept `Attribute`, et la navigation ressemblera à `attributes->select(name =
'SeuilVibration').value` plutôt qu'à une propriété typée `seuilVibration`. C'est fidèle à
AutomationML mais verbeux en projection. L'alternative — promouvoir les quatre propriétés en
attributs XML de `InternalElement` — donne un modèle bien plus lisible au prix de la
fidélité au standard. À arbitrer avant d'écrire le FML, parce que cela change toutes les
expressions de dérivation.