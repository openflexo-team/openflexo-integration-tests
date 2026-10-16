# DigitalTwinDemo — presenter's run sheet

What to do and what to say, step by step. Roughly 20 minutes.

`OPERATING-PROCEDURE.md` is the how-to (how to run it, what each script asserts);
`specification-demo.md` is the functional source of truth. This file is the narrative.

The whole story hangs on one sentence — keep it in mind throughout:
**`ETI04A` vibrates harder, but `CONV03` is the one to fix.**

---

## Before you walk in

Have three things open:

- `production-line.xml` in an editor;
- `maintenance.xlsx`, sheet `Readings`, **cursor already parked on `A56`**;
- Openflexo on the `ProductionLine` virtual model.

Write `4.9` on a piece of paper next to you. You do not want to be looking for that value live.

The three manipulations apply **in memory and never save**, so the artifacts stay pristine and the
demo is replayable as-is — useful if you give it twice in a row.

---

## 1. Set the scene — 3 min

**Show:** the two files, side by side.

> "Two files, two owners who do not talk to each other. On the left, the automation engineering
> office describes the topology of the line: six equipments, their nominal rates, their vibration
> thresholds. On the right, maintenance keeps its workbook: an asset register, 53 timestamped
> readings, six interventions."

Scroll through the workbook for a second.

> "Look carefully: **not a single formula**. Everything that gets computed will be computed in the
> model, not in Excel. That is a choice — if the computation moved in here, this demo would have no
> subject left."

Then, pointing at the two labels:

> "And notice that the same machine is called `TransferConveyor` on the left and
> `Labeller transfer conveyor` on the right. You cannot join on names. We will join on the code —
> `EQ-CONV-03` on one side, `CONV03` on the other — and that rule is **declared in the model**,
> visible and reviewable."

---

## 2. Manipulation 1 — the transverse question — 6 min

This is the heart of it. Do not rush: you are about to give **three different answers to the same
question**.

**The question:**

> "Maintenance has one slot. Just one. Which equipment do they spend it on?"

### Reading 1 — the workbook alone

Sort the vibration column in Excel.

> "ETI04A, 6.5 mm/s. By far the most vibrating. The obvious answer… and the wrong one."

### Reading 2 — plus the XML thresholds

Show the two thresholds: 7.0 for ETI04A, 4.5 for CONV03.

```
conv.thresholdRatio   ->  0.933
eti.thresholdRatio    ->  0.929
```

> "Measured against their own threshold, both machines sit at 93 %. A tie. At this point no decision
> is possible — and it is built that way on purpose."

**Pause here.** This is the moment the audience realises something is missing.

### Reading 3 — plus the topology

```
eti.isCritical                  ->  false          (ETI04A is doubled by ETI04B)
eti.predecessors                ->  1
conv.isCritical                 ->  true
conv.isBottleneck               ->  true           (nominal rate 950, lowest of the critical ones)
line.getTopPriority()           ->  CONV03
line.getActionablePriorities()  ->  [CONV03]  alone
```

> "ETI04A is doubled: if it stops, ETI04B takes over and the line keeps running. CONV03 is the
> bottleneck nothing bypasses. The topology settles the tie the measurements could not settle."

**The sentence not to fumble:**

> "That priority ranking is written nowhere. Not in the XML, not in the workbook. It exists only at
> the intersection of the two — which is exactly what model federation does."

If you want to drive it home: `conv.temperatureRatio -> 0.95`. CONV03's bearing temperature is also
at 95 % of its threshold. A second, independent quantity that corroborates — without taking part in
the verdict.

---

## 3. Manipulation 2 — the twin is live — 4 min

**Do:** in `maintenance.xlsx`, sheet `Readings`, row 56, type it live:

```
24/07/2026 | CONV03 | Vibration | 4.9 | mm/s | MPR
```

Then re-run the twin.

**Show:**

```
before :  lastVibration 4.2   thresholdRatio 0.933   thresholdStatus WATCH
after  :  lastVibration 4.9   thresholdRatio 1.089   thresholdStatus EXCEEDED
```

> "A technician enters a reading. Nothing else changed. The twin goes from 'under watch' to
> 'threshold exceeded' — because the value crossed a threshold that comes from the *other* file."

**Then the write-back:**

```
conv.writeCriticityToAssetRecord();
->  Assets!H (CONV03) = CRITICAL_BOTTLENECK
```

> "And the twin can write back into the artifact — here column H of the register, left free for
> exactly this."

**The precision that matters:**

> "That column is a **projection sink**, not a source. The twin writes to it and never reads it back.
> Criticity stays derived from the topology — if I read it from there, the whole demonstration would
> collapse."

---

## 4. Manipulation 3 — reconfiguration — 4 min

**Do:** one single line added to `production-line.xml`, after the existing `InternalLink` elements:

```xml
<InternalLink Name="L7" RefPartnerSideA="EQ-REM-02:OUT" RefPartnerSideB="EQ-ETI-04A:IN"/>
```

> "The automation engineer wires a bypass. He touches no measurement and no threshold. He changes
> the topology."

**Show:**

```
material links            :  6  ->  7
ETI04A now has 2 predecessors
conv.isCritical           :  true  ->  false
conv.lastVibration        :  4.2   (unchanged)
conv.isDrifting           :  true  (unchanged)
conv.thresholdStatus      :  WATCH
conv.effectiveCriticity   :  CRITICAL_BOTTLENECK  ->  TO_WATCH
conv.isActionablePriority :  false
```

**The exact wording — this is the best moment of the demo:**

> "Watch what just happened. The vibration has not moved by a tenth. The alert **does not disappear:
> it de-classes.** We move from a line-stop risk to an intervention to be scheduled at the next
> planned stop."

> "That is far more accurate — and far more convincing — than making a red light go away."

---

## 5. Close — 2 min

> "Three readings, three answers. The workbook alone gets it wrong. The workbook plus the thresholds
> hesitates. It takes both models **plus the topology** to decide. And when the line is rewired, the
> decision recomputes on its own."

---

## Arguments to keep in reserve

**If asked "why not just a good XSD schema?"** — this is your best answer. The schema guarantees that
no link dangles. But no schema can guarantee that `EQ-ETI-04B` appears in the Excel asset register,
nor that the `CONV3` code in the `Readings` sheet corresponds to anything. Validation is confined to
one technology; **the space between technologies is precisely the one federation occupies.**

**If someone digs into the data** — two inconsistencies are planted. Mention them in one sentence
without demonstrating them: `EQ-ETI-04B` exists in the topology but not in the register; a reading
row dated 10/07 is coded `CONV3`, one digit short, and references nothing. A real workbook looks
like that.

**If they want business corroboration** — the last intervention on CONV03 dates back to 09/02, the
oldest in the workbook; ETI04A was serviced on 28/06, very recently. The machine being neglected is
indeed the one that is drifting.

**If they ask to see the model** — show `Equipment`: four roles pointing at the real artifact
elements (its `InternalElement`, its register row, its readings, its interventions), and above them
everything is derived. Nothing is stored.

---

## Safety net

The nine `J0`…`J6` scripts under `AutomatedTests/` replay this exact sequence automatically. Run them
the day before as a dress rehearsal, and fall back on them if a live edit goes wrong.
