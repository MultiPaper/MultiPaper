# Developing a multi-thread plugin

When developing a plugin for a multi-threaded server, you must be careful of
multiple things:

## 1. Updating data

Be careful of multiple threads updating data at the same time. This can cause
a race-condition between threads, meaning the data will not be updated
correctly.

For example, given the command:

```java
final HashMap<UUID, Integer> playerMoney = new HashMap<>();

// Called via /givemoney PureGero 100
public void giveMoneyCommand(UUID player, int amount) {
    int currentMoney = playerMoney.get(player);
    int newMoney = currentMoney + amount;
    playerMoney.put(player, newMoney);
}
```

Commands can be called from multiple threads at the same time. If two threads
try to update the same player's money at the same time, the following will
happen:

1. Thread 1 reads the current money as 100.
2. Thread 2 reads the current money as 100.
3. Thread 1 adds 100 to the current money, making it 200.
4. Thread 2 adds 100 to the current money, making it 200.
5. Thread 1 writes the new money as 200.
6. Thread 2 writes the new money as 200.
7. The player's money is now 200, not 300. Money has been lost.

To solve this, you can use a synchronized block around the code, which will
ensure the code is only called on one thread at a time.

```java
final HashMap<UUID, Integer> playerMoney = new HashMap<>();

// Called via /givemoney PureGero 100
public void giveMoneyCommand(UUID player, int amount) {
    synchronized (playerMoney) {
        // Only one thread will run this code at a time while in a
        // synchronized block for a given object
        int currentMoney = playerMoney.get(player);
        int newMoney = currentMoney + amount;
        playerMoney.put(player, newMoney);
    }
}
```

You can take this a step further by using a ConcurrentHashMap. A HashMap is not
thread-safe, meaning data can be lost if two threads update any data in it at
the same time. However, a ConcurrentHashMap is thread-safe.

In addition, ConcurrentHashMap lets you lock a certain value while updating it,
meaning you don't have to lock the entire HashMap, but can instead lock one key.

```java
final ConcurrentHashMap<UUID, Integer> playerMoney = new ConcurrentHashMap<>();

// Called via /givemoney PureGero 100
public void giveMoneyCommand(UUID player, int amount) {
    playerMoney.compute(player, (key, currentValue) -> {
        // This locks the 'player' key in the map, meaning only one thread can
        // update it at a time
        if (currentValue == null) currentValue = 0; // Values default to null
        
        return currentValue + amount;
    });
}
```

## 2. Reading data

Be careful of one thread reading data while it is being updated by another
thread. This can cause the thread to read incorrect data.

For example:

```java
List<UUID> players = new ArrayList<>();

for (UUID player : players) {
    // If another thread removes a player from the list, this will throw an
    // IndexOutOfBoundsException
}

for (int i = 0; i < players.size(); i++) {
    // If another thread removes a player to the list, this may skip a player
    // as the indecies will shift during the iteration.
        
    UUID player = players.get(i);
    // `player` may be null, or an IndexOutOfBoundsException may be thrown if
    // a player was removed from the list by another thread
}
```

## 3. Chunk threads

Code must be executed on the chunk's thread. You can no longer rely on a main
thread.

For example, for a command the updates a block:

```java
// A player at 0,0,0 wants to update a far-away block at 1000,0,0
// This block will certainly be on a different thread, so we must use the
// block's thread instead.
public void updateBlockCommand(Player player, Location blockLocation) {
    Bukkit.getRegionScheduler().run(plugin, blockLocation, task -> {
        // This code block will be run on the chunk at blockLocation's thread
        blockLocation.getBlock().setType(Material.DIAMOND_BLOCK); 
    });
    
}
```

Another example, for a command that updates an entity:

```java
// A player at 0,0,0 wants to update a far-away entity at 1000,0,0
// This entity will certainly be on a different thread, so we must use the
// entity's thread instead.
public void updateEntityCommand(Player player, Entity entity) {
    entity.getScheduler().run(plugin, task -> {
        // This code block will be run on the entity's chunk's thread, even if
        // the entity moves to another chunk!
        Bukkit.broadcastMessage("Hello, world!");
    }, null);
}
```

Note that these two examples use Paper's Scheduler API, so you must reference
the paper api rather than the spigot api in your dependencies.