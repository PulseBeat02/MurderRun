# Basic API
Murder Run has an event bus you can listen to. There are a limited number of events that you are able to listen to.
Get the bus from `EventBusProvider`, and subscribe to an event with your plugin instance, the event class, and a
handler:

```{code-block} java
:linenos:

final ApiEventBus bus = EventBusProvider.getBus();

// Listen to an event
final EventSubscription<GadgetUseEvent> subscription = bus.subscribe(plugin, GadgetUseEvent.class, event -> {
  final Gadget gadget = event.getGadget();
  final GamePlayer player = event.getPlayer();
  event.setCancelled(true); // stops the gadget from being used
});

// Stop listening
subscription.unsubscribe();   // just this subscription
bus.unsubscribe(plugin);      // or everything your plugin subscribed to
```

You can also pass a priority as a fourth argument with `bus.subscribe(plugin, eventClass, handler, priority)`.
Subscriptions with a lower priority are called first. If a handler cancels a cancellable event, the handlers after it
aren't called and Murder Run doesn't perform the action. Subscribing to a parent event such as `MurderRunEvent` or
`GadgetUseEvent` also calls your handler for its sub-events.

All events are in the `me.brandonli.murderrun.api.event.contract` package:

| Event                                   | Called when                               | Cancellable | Methods                                  |
|-----------------------------------------|-------------------------------------------|-------------|------------------------------------------|
| `GameStatusEvent`                       | A game's status changes                   | No          | `getGameStatus()`, `getGame()`           |
| `gadget.GadgetUseEvent`                 | A player uses a gadget                    | Yes         | `getGadget()`, `getPlayer()`             |
| `gadget.TrapActivateEvent`              | A trap is activated (a `GadgetUseEvent`)  | Yes         | `getGadget()`, `getPlayer()`             |
| `ability.AbilityUseEvent`               | A player uses an ability                  | Yes         | `getAbility()`, `getPlayer()`            |
| `arena.ArenaEvent`                      | An arena is created or deleted            | Yes         | `getArena()`, `getModificationType()`    |
| `lobby.LobbyEvent`                      | A lobby is created or deleted             | Yes         | `getLobby()`, `getModificationType()`    |
| `statistic.StatisticsEvent`             | A player statistic is about to change     | Yes         | `getStatisticsType()`, `getChange()`     |
| `event.RandomGameEvent`                 | A random in-game event is triggered       | Yes         | `getEvent()`, `getGame()`                |

Every event also has `getMurderRun()`, which returns the Murder Run plugin instance, and `getEventType()`.
