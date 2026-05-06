# Architecture

## Bounded contexts

The domain is split into six bounded contexts. Each has its own model, language, and (eventually) its own database tables.

### Core contexts

These are the reason the application exists. Most engineering effort goes here.

**Scheduling** — the heart of the app. Owns the `Celebration` aggregate (with subtypes Mass, Baptism, Wedding, Funeral, Confirmation, FirstCommunion). Knows when, where, who. Enforces all booking invariants.

**Records** — the canonical sacramental books required by canon law (baptism book, marriage book, etc.). Append-only by design. Created in response to events fired by Scheduling.

**Participants** — people involved in celebrations: faithful, ministers, priests, godparents, witnesses. Owns the `Person` aggregate.

### Supporting contexts

Necessary to operate the business, but not differentiating.

**Locations** — physical spaces (main church, side chapels, cemetery, parish hall) and capacity / availability info.

**Public calendar** — read-only projections of upcoming celebrations for the parish website. CQRS-style read model.

### Generic contexts

Things that could be replaced by an off-the-shelf service.

**IAM** — authentication, authorization, roles (admin, secretary, priest, viewer).

**Notifications** — email and SMS reminders, for both staff and the faithful.

## Integration patterns

| From → To | Pattern | Why |
|---|---|---|
| Scheduling → Records | Upstream/Downstream | Scheduling is the source of truth; Records reacts. |
| Scheduling → Participants | Customer/Supplier | Scheduling negotiates the data it needs from Participants. |
| Scheduling → Notifications | Async events | Notifications listens for "celebration confirmed" events. |
| Scheduling → Public calendar | CQRS read model | Calendar is a denormalized projection, eventually consistent. |
| Anything → IAM | Conformist | Use IAM's user model as-is, no anti-corruption layer. |

## Layered structure inside each context

```
domain          ← pure Java, no Spring, no JPA
application     ← use cases, transaction boundaries
infrastructure  ← JPA adapters, external integrations
api             ← REST controllers, DTOs
```

The domain has no imports from any of the other layers. This means domain logic can be unit-tested without Spring, MySQL, or HTTP — just plain JUnit.
