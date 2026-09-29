# PlaceholderAPI Support
Murder Run has support for [PlaceholderAPI](https://modrinth.com/plugin/placeholderapi) (PAPI) placeholders. The
placeholders are registered automatically when PlaceholderAPI is installed, so you don't need to download an expansion
with `/papi ecloud`. Here are the placeholders supported by Murder Run.

| Placeholder                          | Returns                                                                     |
|--------------------------------------|-----------------------------------------------------------------------------|
| `%murderrun_fastest_win_killer%`     | The player's fastest win as a killer, in seconds (`N/A` if they haven't won yet) |
| `%murderrun_fastest_win_survivor%`   | The player's fastest win as a survivor, in seconds (`N/A` if they haven't won yet) |
| `%murderrun_total_kills%`            | The total kills for the player                                              |
| `%murderrun_total_deaths%`           | The total deaths for the player                                             |
| `%murderrun_total_wins%`             | The total wins for the player                                               |
| `%murderrun_total_losses%`           | The total losses for the player                                             |
| `%murderrun_total_games%`            | The total games played by the player                                        |
| `%murderrun_win_loss_ratio%`         | The win-loss ratio for the player                                           |

You can test a placeholder in-game with `/papi parse me %murderrun_total_wins%`.
