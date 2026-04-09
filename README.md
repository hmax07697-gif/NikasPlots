# NikasPlots

A lightweight plot management plugin for Paper servers that automates timed building sessions. Perfect for speed-builds or trial periods where players have a fixed window to build before their plot is locked.

## Features

  - **Timed Sessions** – Automatically grants 30 minutes of build time upon claiming
  - **Auto-Protection** – Instantly generates 100x100 WorldGuard regions per plot
  - **Automatic Locking** – Switches plots to read-only mode the second the timer expires
  - **High Performance** – Built on FAWE to handle region operations without lag
  - **Persistent Data** – All plot owners and timers persist through server restarts via YAML

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/plot create` | Claims a 100x100 plot and starts the 30m timer | `plots.create` |
| `/plot tp [player]` | Teleport to your plot or another player's plot | `plots.tp` |
| `/plot reset <player>` | Resets a player's build timer to 30 minutes | `plots.reset` |
| `/plot lock <player>` | Manually lock a plot from further building | `plots.lock` |
| `/plot unlock <player>` | Manually unlock a plot for building | `plots.unlock` |

## Permissions

  - `plots.create` – Allow claiming a plot (default: true)
  - `plots.tp` – Allow teleporting to plots (default: true)
  - `plots.reset` – Allow resetting timers (default: op)
  - `plots.lock` – Allow manual locking (default: op)
  - `plots.unlock` – Allow manual unlocking (default: op)
  - `plots.bypass` – Allows building in locked plots (default: op)

## Installation

1.  Ensure **WorldGuard** and **FAWE** are installed on your server.
2.  Drop `NikasPlots.jar` into your `plugins/` folder.
3.  Set the `__global__` flag in your plot world to `build deny`.
4.  Configure your world name in `plugins/NikasPlots/config.yml`:

<!-- end list -->

```yaml
# The name of the world where plots will be generated
plots-world: "plots_world"
```

5.  Restart your server.

## Usage

1.  Enter the designated plots world.
2.  Run `/plot create` to receive a 100x100 area.
3.  Build until the 30-minute timer expires.
4.  Once locked, the plot becomes read-only unless an admin runs `/plot unlock` or `/plot reset`.

## License

MIT — see [LICENSE](https://www.google.com/search?q=LICENSE) for details.
