# DigitalTwinDemo — operating procedure

Step-by-step guide to run the demo and replay the three live manipulations. The functional
source of truth is `specification-demo.md`; this file is the how-to.

## What was built

- **VirtualModel** `FML/Ligne.fml/Ligne.fml` — federates the CAEX topology (`ligne.xml`, typed
  against `ligne.xsd`) with the maintenance workbook (`exploitation.xlsx`) through two model slots:
  - `XMLModelSlot(metaModel=CAEX)` on `ligne.xml`;
  - `BasicExcelModelSlot()` on `exploitation.xlsx`.
- One federative FlexoConcept `Equipement` (XML `InternalElement` joined, on code, to its `Parc`
  row), plus transverse derivations on the `Ligne` model. **Nothing is stored in the artifacts —
  every criticity value is derived.**
- Join rule `EQ-CONV-03` ⟷ `CONV03` isolated in `deriveCode(...)` (drop `EQ-` prefix and dashes).

> Note on the artifacts: to let the typed XML model slot bind, `ligne.xsd` was given
> `targetNamespace="http://www.dke.de/CAEX"` and `ligne.xml` the matching default namespace with
> `xsi:schemaLocation` (spec section 7). No data was changed. The planted inconsistencies are
> intact: `EQ-ETI-04B` is absent from `Parc`, and the `CONV3` orphan row in `Relevés` references
> nothing.

## Execution loop

Automated FML-script tests (the milestone gate — validation is on business values, not compilation):

```
cd openflexo-integration-tests
./gradlew :integration-tests:test --tests '*DigitalTwinAutomatedTests*'
```

⚠️ `test` runs with `ignoreFailures = true` — read the printed
`Results: … (N tests, … failures)` line, never the exit code. A green run is
`Results: SUCCESS (9 tests, 9 successes, 0 failures, …)`.

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

**Expected result:** prioritized list `[CONV03, ETI04A, DEP01, ENC05, REM02, ETI04B]`; actionable
priorities `[CONV03]` only; `ETI04A` excluded despite the highest raw vibration.

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

- `CONV03` last vibration 4.2 → **4.9**; `tauxSeuil` 0.933 → **1.089**.
- Threshold status **`SURVEILLANCE` → `DEPASSEMENT`** (4.9 > 4.5).
- `criticiteEffective(CONV03)` stays **`CRITIQUE_GOULOT`** (still the critical bottleneck).
- The twin writes the effective criticity back into **`Parc` column `H`** (left free for this):
  `Parc!H(CONV03) = CRITIQUE_GOULOT`. This column is a **projection sink**, never the source.

**Script equivalent:** `ligne.addReleve("CONV03", "4.9", "24/07/2026", "MPR");` then
`ligne.writeBackCriticiteToParc("CONV03");`.

---

## Manipulation 3 — reconfiguration (the alert **de-classes**, it does not disappear)

**Live edit:** open `ligne.xml` and add, after the existing `InternalLink` elements:

```xml
<InternalLink Name="L7" RefPartnerSideA="EQ-REM-02:OUT" RefPartnerSideB="EQ-ETI-04A:IN"/>
```

This bypass creates a path `DEP01 → REM02 → ETI04A → ENC05` that avoids `CONV03`.

**Refresh:** re-run the twin, or run `J6_Manip3_Reconfiguration.fmlscript` (adds L7 in memory).

**Expected result:**

- `estCritique(CONV03)` **true → false** (a path now avoids it).
- Its vibration is unchanged: still `SURVEILLANCE` (0.933 of threshold) and still trending up, so
  `enDerive(CONV03)` stays **true**.
- `criticiteEffective(CONV03)` **`CRITIQUE_GOULOT` → `A_SURVEILLER`**, and `CONV03` leaves the
  actionable priority set.

**Phrase it precisely:** the alert is **not cleared, it is re-scoped** — from a line-stop risk to an
intervention to schedule at the next planned stop. More accurate, and far more convincing, than
making a red light disappear.

**Script equivalent:** `ligne.addBypassL7();`.
