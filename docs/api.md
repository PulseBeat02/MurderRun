# Basic API
Murder Run as an event bus you can listen to. There are a limited number of events that you are able to
listen to.

```{code-block} java
:linenos:

final APIEventBus bus = EventBusProvider.getBus(); // gets the event bus
bus.subscribe(...);
bus.unsubscribe(...);
bus.fire(...);
```
