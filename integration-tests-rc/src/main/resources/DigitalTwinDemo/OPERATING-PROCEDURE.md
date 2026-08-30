# DigitalTwinDemo — operating procedure

Step-by-step guide to run the demo and replay the three live manipulations. The functional
source of truth is `specification-demo.md`; this file is the how-to.

## What was built

- **VirtualModel** `FML/Ligne.fml/Ligne.fml` — federates the CAEX topology (`ligne.xml`, typed
  against `ligne.xsd`) with the maintenance workbook (`exploitation.xlsx`) through two model slots:
  - `XMLModelSlot(metaModel=CAEX)` on `ligne.xml`;
  - `FMLExcelModelSlot()` on `exploitation.xlsx`, reflected against the **`Exploitation`** contract
    VirtualModel (`FML/Exploitation.fml`), which types each significant row as a `Releve`,
    a `FicheParc` or an `Intervention`. The workbook is never navigated as rows and columns.

**The structure is materialized, the values are derived.** Two FlexoConcepts are instantiated and
attached to the artifact elements they federate:

| Concept | Roles (what it points at) |
|---|---|
| `Equipement` | `xmlElement` → its `InternalElement` (XML) · `fiche` → its row in `Parc` (cardinality 1, empty for `ETI04B`) · `releves` → its readings · `interventions` → its maintenance operations |
| `Liaison` | `xmlLink` → its `InternalLink` (XML) · `source` / `cible` → the two `Equipement` it connects |

`synchronize()` fills those roles with the FML `match` mechanism, so it is idempotent and re-runnable.
Everything above them — `cadenceNominale`, `seuilVibration`, `tauxSeuil`, `enDerive`, `successeurs`,
`estCritique`, `estGoulot`, `criticiteEffective`, `rangPriorite` — is an expression (`values`) or a
`get()` property. **Nothing is stored: no criticity is ever written into an artifact.** After a live
manipulation, `synchronize()` re-attaches the structure and every derived value follows on its own.

Join rule `EQ-CONV-03` ⟷ `CONV03` isolated in `codeDepuisXmlId(...)` (drop `EQ-` prefix and dashes),
and reused by `Liaison`, through `codeDepuisInterface(...)`, to resolve interface ids such as
`EQ-CONV-03:OUT`.

> Note on the artifacts: to let the typed XML model slot bind, `ligne.xsd` was given
> `targetNamespace="http://www.dke.de/CAEX"` and `ligne.xml` the matching default namespace with
> `xsi:schemaLocation` (spec section 7 documents the no-namespace variant, which predates this).
> No data was changed. The planted inconsistencies are intact: `EQ-ETI-04B` is absent from `Parc`,
> and the `CONV3` orphan row in `Relevés` references nothing.

## Execution loop

⚠️ **Prerequisite — a technology-adapter fix is required.** Materialized roles pointing at the
reflected Excel instances need `XLSObjectActorReference` to be declared on `FMLExcelModelSlot`
(`openflexo-xlsx`); the same gap was fixed on `FMLXMLModelSlot` (`openflexo-xml`). Until those two
projects are published, run the tests through a composite build against local sources, otherwise 7
of the 9 scripts fail:

```
cd openflexo-integration-tests
./gradlew --include-build ../openflexo-xlsx --include-build ../openflexo-xml \
    :integration-tests:test --tests '*DigitalTwin*'
```

(Or run everything from the `openflexo-dev` composite, which includes both.)

⚠️ `test` runs with `ignoreFailures = true` — read the printed
`Results: … (N tests, … failures)` line, never the exit code. A green run is
`Results: SUCCESS (11 tests, 11 successes, 0 failures, …)` — the 9 FML-scripts plus the two
`DigitalTwinValidationTest` cases.

Scripts live in `AutomatedTests/`:

| Script | Milestone / manipulation |
|---|---|
| `J0_LoopAlive` … `J5_Priorities` | build-up, each asserting the spec's business values |
| `J6_Manip1_Question` | Manipulation 1 — the prioritized question |
| `J6_Manip2_Ecriture` | Manipulation 2 — liveness + write-back to `Parc!H` |
| `J6_Manip3_Reconfiguration` | Manipulation 3 — the L7 bypass, alert de-classing |

The manipulation scripts apply their change **in memory and never save**, so the artifacts stay
pristine and the scripts are fully replayable. For a live demo you edit the files by hand as below.

---

## Manipulation 1 — the transverse question

**Script:** `J6_Manip1_Question.fmlscript`. Nothing to edit.

Ask: *which equipment should maintenance address first?* The three readings disagree:

1. **Excel alone, raw vibration** → `ETI04A` is highest (6.5 vs 4.2 mm/s) — **wrong answer**.
2. **Excel + XML thresholds** → `CONV03` at 93.3 % and `ETI04A` at 92.9 % — a **tie**, no decision.
3. **Excel + thresholds + topology** → `ETI04A` is doubled by `ETI04B`; `CONV03` is the bottleneck
   nothing bypasses → **`CONV03`, unambiguously.**

**Expected result:** `ligne.getCodesPrioritaires()` puts `CONV03` first; `ligne.getPrioritesActionnables()`
holds `CONV03` alone; `ETI04A` is excluded (`estPrioriteActionnable == false`) despite the highest raw
vibration of the whole workbook.

---

## Manipulation 2 — liveness and write-back

**Live edit:** open `exploitation.xlsx`, sheet **`Relevés`**, cursor already on **`A56`**, and add
one row:

```
24/07/2026 | CONV03 | Vibration | 4,9 | mm/s | MPR
```

**Refresh:** re-run the twin (re-instantiate `Ligne`, or run `J6_Manip2_Ecriture.fmlscript`, which
appends the same reading in memory).

**Expected result:**

- `CONV03` last vibration 4.2 → **4.9** (its `releves` role goes from 14 to 15); `tauxSeuil` 0.933 → **1.089**.
- Threshold status **`SURVEILLANCE` → `DEPASSEMENT`** (4.9 > 4.5).
- `conv.criticiteEffective` stays **`CRITIQUE_GOULOT`** (still the critical bottleneck).
- The twin writes the effective criticity back into **`Parc` column `H`** (left free for this):
  `Parc!H(CONV03) = CRITIQUE_GOULOT`. This column is a **projection sink**, never the source.

**Script equivalent** — both manipulations are carried by the equipment they concern:

```
conv = ligne.getEquipementParCode("CONV03");
conv.ajouterReleveVibration(4.9, "MPR");
conv.ecrireCriticiteDansFiche();
```

---

## Manipulation 3 — reconfiguration (the alert **de-classes**, it does not disappear)

**Live edit:** open `ligne.xml` and add, after the existing `InternalLink` elements:

```xml
<InternalLink Name="L7" RefPartnerSideA="EQ-REM-02:OUT" RefPartnerSideB="EQ-ETI-04A:IN"/>
```

This bypass creates a path `DEP01 → REM02 → ETI04A → ENC05` that avoids `CONV03`.

**Refresh:** re-run the twin, or run `J6_Manip3_Reconfiguration.fmlscript` (adds L7 in memory).

**Expected result:**

- one more `Liaison` (6 → 7), and `ETI04A` now has two predecessors;
- `conv.estCritique` **true → false** (a path now avoids it).
- Its vibration is unchanged: still `SURVEILLANCE` (0.933 of threshold) and still trending up, so
  `conv.enDerive` stays **true**.
- `conv.criticiteEffective` **`CRITIQUE_GOULOT` → `A_SURVEILLER`**, and `CONV03` leaves the
  actionable priority set.

**Phrase it precisely:** the alert is **not cleared, it is re-scoped** — from a line-stop risk to an
intervention to schedule at the next planned stop. More accurate, and far more convincing, than
making a red light disappear.

**Script equivalent:** `ligne.addBypassL7();`.
