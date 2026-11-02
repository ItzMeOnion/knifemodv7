# Knife Cases (Fabric 1.21.8)

Client-side CS-style knife cosmetics for Minecraft Java 1.21.8.

- **K**: open the case menu. Five cases, unlimited opening, no cost.
- **G**: draw the equipped knife into your empty hand, or put it away.
- **H**: same as G for now. The inspect move is the next step.

All three keys can be rebound under Options → Controls → Knife Cases.

Rarities: Common 60%, Uncommon 25%, Rare 10%, Epic 4%, Legendary 1%.

## Build

The **Build mod** GitHub Actions workflow builds the jar and uploads it as an artifact (Actions tab → latest run → Artifacts).

Locally, with Java 21 and Gradle 8.14.3 installed:

    gradle build

The jar appears in `build/libs/`.

## Adding knives

Edit `knifeBases` and `knifeFinishes` in `build.gradle`. Every base is combined with every finish, and the item models, item definitions, lang entries and registry are generated on build.

## Notes

Knives are client-side cosmetics only. They are not sent to the server and other players won't see them.
