# openflexo-integration-tests — Fixed defects

Defects of openflexo-integration-tests that are fixed, with what was verified, the cause and the fix. Identifiers are never reused.

---

## multi-process-challenge

### ITEST-D-1 — A stored Acme instance loses `initialTaskType` and the name lookup of its first task type  ·  `DONE`

**GitHub:** none yet.

**Symptom.** After loading `AcmeModel.fml.rt`, `initialTaskType` of the process type came back null and `getTaskType("Begin")` and
`getTaskType("Requirement analysis")` found nothing, while the counts of task types and gateways were right. `T6_AcmeFixture` pinned
those nulls as "not root-caused".

**Cause (verified 2026-11-20 by execution).** The `language` role of the stored `CodingTaskType` was written as a
`FlexoEnumValueActorReference` on `AcmeMetaModel.fml#ProgrammingLanguage#COBOL`, but `ProgrammingLanguage` is a concept, not an
enumeration, so the load failed with a `ClassCastException` on that reference and the instances stored after it were not wired.

**Fix.** The language is stored as an instance of `ProgrammingLanguage` (`COBOL`) referenced by a `ModelObjectActorReference`. T6 now
asserts the language is found and that `initialTaskType` and both lookups resolve (the pins are inverted).
