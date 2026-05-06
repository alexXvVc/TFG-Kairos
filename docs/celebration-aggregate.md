# The Celebration aggregate

The core of the application is the `Celebration` aggregate. Most of the backend complexity lives here, which is what allows the frontend to stay simple — exactly what the project requires.

## Aggregate root

`Celebration` is an **abstract** class. You never instantiate it directly; you instantiate one of its subtypes. This is the only way to load or save a celebration — the JPA repository returns `Celebration`, but at runtime it'll be a `Mass`, `Baptism`, `Wedding`, etc.

Shared fields (in the base class):

- `id` — UUID
- `scheduledAt` — date and time
- `locationId` — reference to the Locations context, by ID
- `presidingPriestId` — reference to the Participants context, by ID
- `status` — DRAFT, CONFIRMED, COMPLETED, CANCELLED
- `notes` — free-form

Shared lifecycle methods:

- `confirm()` — transitions DRAFT → CONFIRMED, calls subtype validation
- `cancel(reason)` — transitions to CANCELLED
- `reschedule(newDateTime)` — only allowed before COMPLETED/CANCELLED

## Subtypes and their invariants

Each subtype enforces what makes it valid, in its **constructor** and in `validateForConfirmation()`.

| Subtype | Required at creation | Required to confirm |
|---|---|---|
| Mass | nothing extra | nothing extra |
| Baptism | child, ≥1 godparent | (extensible) |
| Wedding | 2 different spouses, ≥2 witnesses | documentation complete |
| Funeral | deceased person | (extensible) |
| Confirmation | ≥1 candidate | bishop assigned |
| FirstCommunion | ≥1 candidate | catechism completed |

## Why this matters for the user

The user-facing form for "create wedding" sends:

```json
{
  "scheduledAt": "2026-09-15T11:00:00",
  "locationId": "...",
  "presidingPriestId": "...",
  "spouseAId": "...",
  "spouseBId": "...",
  "witnessIds": ["...", "..."]
}
```

The backend rejects bad data with clear messages (in Spanish, English, whatever locale). The 65-year-old admin doesn't see Java stack traces — they see "A wedding requires at least two witnesses." That message lives in the domain, not in twenty different controllers.

## Why subtypes instead of one big class with a `type` field

A "celebration with optional everything" would need `if (type == WEDDING) checkSpouses()` scattered across services. With subtypes, the type *is* the validation. Adding a new celebration type means one new class — no existing code changes.

## Persistence

JPA `@Inheritance(strategy = JOINED)` is the recommended mapping:

- one base table `celebration` with shared columns
- one child table per subtype: `celebration_mass`, `celebration_wedding`...
- joined automatically when you query

This keeps your MySQL schema clean and lets you query across types easily ("all confirmed celebrations next week, regardless of type"). Annotations are added in the next phase, currently the domain classes are pure POJOs.

## Cross-aggregate references

The `Celebration` only stores **IDs** of other aggregates (priests, locations, witnesses). It never holds a `Person` object inside it. This rule keeps:

- transactions small (you don't accidentally load and save a Person when saving a Wedding)
- the boundaries clear (Participants is in charge of Person; Scheduling is in charge of Celebration)
- the JSON payloads predictable
