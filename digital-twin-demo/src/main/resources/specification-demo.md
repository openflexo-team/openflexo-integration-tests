# Openflexo demo — digital twin of a packaging line

Specification note accompanying `production-line.xml` and `maintenance.xlsx`.

---

## 1. The two artifacts

**`production-line.xml`** — 69 lines, a subset of AutomationML (`InstanceHierarchy` / `InternalElement` /
`ExternalInterface` / `InternalLink`). Owner: the automation engineering office. It holds six
equipments, their nominal rates, their vibration and temperature thresholds, and six material
links.

**`production-line.xsd`** — its schema, referenced from the XML through `xsi:schemaLocation`. Named global
`complexType`s, plus `key` / `keyref` constraints that guarantee the referential integrity of the
links. See §7.

**`maintenance.xlsx`** — three sheets. Owner: the maintenance department.

| Sheet | Header row | Content |
|---|---|---|
| `Assets` | 4 (title on 1–2) | 5 inventoried equipments |
| `Readings` | 1 | 53 timestamped readings, 05/06 to 17/07/2026 |
| `Interventions` | 1 | 6 dated interventions |

The header offset of the `Assets` sheet is deliberate: it is what a real workbook looks like. It is
trivial to absorb in the Excel model slot — delete the first two rows if you would rather not talk
about it.

**No formula in the workbook.** That is a design choice, not an oversight: every derived computation
belongs to the virtual model. If one of them migrated into Excel, the demo's argument would weaken.

---

## 2. Correspondence between the two artifacts

Join key: the equipment code, spelled differently on each side.

| `production-line.xml` (`InternalElement/@ID`) | `maintenance.xlsx` (`Code` column) |
|---|---|
| `EQ-DEP-01` | `DEP01` |
| `EQ-REM-02` | `REM02` |
| `EQ-CONV-03` | `CONV03` |
| `EQ-ETI-04A` | `ETI04A` |
| `EQ-ETI-04B` | *(missing)* |
| `EQ-ENC-05` | `ENC05` |

The rule is regular: drop the `EQ-` prefix and the dashes. It is deliberately simple — the teaching
point is that it exists and is **declared**, not that it is hard.

The labels, on the other hand, do not coincide at all (`TransferConveyor` on the XML side,
`Labeller transfer conveyor` on the Excel side): that is what shows you cannot join on names.

---

## 3. Virtual model concepts and derivation rules

Three concepts, only one of which is federative.

**`Equipment`** — federates an `InternalElement` of the XML, one row of `Assets`, all of its rows in
`Readings` and in `Interventions`.

**`MaterialLink`** — comes from the XML alone (`InternalLink`).

**`ProductionLine`** — the root, which carries the transverse derived properties.

Derived properties, computed on the fly, present in no artifact:

| Property | Rule | Source |
|---|---|---|
| `isCritical` | no path from entry to exit avoids it | XML alone |
| `isBottleneck` | minimal nominal rate among the critical equipments | XML alone |
| `thresholdRatio` | last vibration ÷ `VibrationThreshold` | XML **and** Excel |
| `isDrifting` | rising trend over the last 4 readings **and** `thresholdRatio` ≥ 0.8 | XML **and** Excel |
| `effectiveCriticity` | `isCritical` **and** `isDrifting`, aggravated when `isBottleneck` | both |

An important design point: **criticity is never declared, it is derived from the topology.** There is
no `CriticalStation` attribute in the XML — otherwise the demo would boil down to reading an
attribute.

---

## 4. What is planted in the data

This is the heart of the demonstration. Each equipment plays a precise role.

| Equipment | Topology (XML) | Measurements (Excel) | Role in the demo |
|---|---|---|---|
| `DEP01` | critical | 4.1 mm/s against a 6.0 threshold → 68 % | background noise |
| `REM02` | critical | 2.6 mm/s against a 5.0 threshold → 52 % | critical but healthy |
| `CONV03` | critical **and bottleneck** (950 u/h) | 2.9 → 4.2 mm/s, threshold 4.5 → **93 %** | **the answer** |
| `ETI04A` | **not critical** (doubled by `04B`) | 5.8 → 6.5 mm/s, threshold 7.0 → **93 %** | the decoy |
| `ETI04B` | not critical | **absent from the asset register** | the inconsistency |
| `ENC05` | critical | 3.3 mm/s against a 5.5 threshold → 60 % | background noise |

The three possible readings do not give the same answer, and that is the whole argument:

- **Excel alone, raw values**: `ETI04A` vibrates by far the most (6.5 against 4.2) → wrong answer.
- **Excel + XML thresholds**: `CONV03` and `ETI04A` both sit at 93 % of their threshold → a tie, no
  decision possible.
- **Excel + thresholds + topology**: `ETI04A` is doubled, `CONV03` is the bottleneck nothing bypasses
  → `CONV03`, unambiguously.

The 93 % tie is deliberate: it forbids deciding on the measurements alone and makes the topology
strictly necessary.

Two discreet corroborations, should someone dig: the last intervention on `CONV03` dates back to
09/02, the oldest in the workbook; `ETI04A` was serviced on 28/06, very recently.

**Planted inconsistencies**, to be mentioned in one sentence without demonstrating them:
`EQ-ETI-04B` exists in the XML and is not in the asset register; the `Readings` sheet contains a row
dated 10/07 coded `CONV3` (a missing digit) that references nothing.

---

## 5. The two live manipulations

**Manipulation 2 — liveness and write-back.** Append at the end of the `Readings` sheet:

```
24/07/2026 | CONV03 | Vibration | 4.9 | mm/s | MPR
```

`4.9 > 4.5`: `CONV03` goes from "under watch" to "threshold exceeded". Prepare the workbook with the
cursor already on `A56` and the value written down next to you.

Then the twin writes the effective criticity into the `Assets` sheet — column `H`, left free for this
purpose.

**Manipulation 3 — reconfiguration.** Add a single line to `production-line.xml`, after the existing
`InternalLink` elements:

```xml
<InternalLink Name="L7" RefPartnerSideA="EQ-REM-02:OUT" RefPartnerSideB="EQ-ETI-04A:IN"/>
```

This bypass creates a path `DEP01 → REM02 → ETI04A → ENC05` that avoids `CONV03`. The latter ceases
to be critical, while its vibration stays above threshold.

State the result precisely: **the alert does not disappear, it de-classes.** We move from a
line-stop risk to an intervention to be scheduled at the next planned stop. That is more accurate,
and far more convincing, than making a red light go away.

---

## 6. Points of attention when moving to FML

- Links share their interfaces: `EQ-CONV-03:OUT` appears in both `L3` and `L4`. Topological
  navigation must therefore go through the `InternalLink`s, and must not assume one interface per
  link.
- `isCritical` requires a graph walk (is there an entry→exit path that avoids `e`?). On six nodes, a
  naive enumeration of paths is more than enough.
- The dates are real Excel dates, not text.
- The units are identical on both sides (`mm/s`, `°C`): no conversion to handle, that was a needless
  complication for a twenty-minute demo.

---

## 7. The `production-line.xsd` schema

Six named global `complexType`s — `CAEXFileType`, `InstanceHierarchyType`, `InternalElementType`,
`AttributeType`, `ExternalInterfaceType`, `InternalLinkType` — rather than anonymous inline types, so
that deriving concepts from the schema yields usable names.

**The schema declares a namespace.** An earlier version relied on `xsi:noNamespaceSchemaLocation`,
which avoids prefixes in every navigation expression. That had to be abandoned: the typed XML model
slot recognises the metamodel from the **namespace of the root element**, not from
`xsi:noNamespaceSchemaLocation`; without a namespace the file loads as an untyped document and the
slot silently does not bind. The XSD therefore carries
`targetNamespace="http://www.dke.de/CAEX"` and the XML the matching `xmlns` with
`xsi:schemaLocation`. This also matches real CAEX, which declares one. No data was changed.

**Identifiers are `xs:string`, not `xs:ID`.** A constraint we had to accept: `EQ-DEP-01:OUT` contains
a colon and is therefore not a valid `NCName`. Integrity is instead enforced by two `xs:key`
(uniqueness of equipments, uniqueness of interfaces) and two `xs:keyref` on the link ends.

This is verified: a `RefPartnerSideB` pointing at a non-existent interface fails validation, and the
`L7` bypass of manipulation 3 remains valid.

**An argument to keep in reserve.** The schema guarantees the internal consistency of the XML — no
link can dangle. But no schema can guarantee that `EQ-ETI-04B` appears in the Excel asset register,
nor that the `CONV3` code in the `Readings` sheet corresponds to anything. Validation is confined to
one technology; that is precisely the space federation occupies. If someone asks "why not just use a
good schema?", this is the answer.

**Consequence for FML.** In CAEX, properties are generic `<Attribute Name="…" Value="…"/>` pairs. A
metamodel derived from the schema therefore gives you an `Attribute` concept, and navigation looks
like `attributes->select(name = 'VibrationThreshold').value` rather than a typed `vibrationThreshold`
property. That is faithful to AutomationML but verbose in projection. The alternative — promoting the
four properties to XML attributes of `InternalElement` — gives a far more readable model at the cost
of fidelity to the standard. To be arbitrated before writing the FML, because it changes every
derivation expression.

*Settled:* fidelity was kept. `Equipment` carries a single generic `attributeValue(name)` lookup, and
the four CAEX properties are promoted on top of it as named `values` properties (`nominalRate`,
`vibrationThreshold`, `bearingTemperatureThreshold`, `type`) — faithful to AutomationML on one side,
readable in projection on the other.
