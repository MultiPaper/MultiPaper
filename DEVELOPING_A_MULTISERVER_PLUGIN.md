# Developing a multi-server plugin

Many plugins have data that will need to be kept in sync across multiple
servers. If this data is not kept in sync, Steve could spend all his money on
one server, then go to another server and spend all his money again. This
document will describe the options available to keeping data in sync.

## Option 1: Have only 1 source of truth

Do not keep any copies of the data within your application, whenever you need
to access the data, query it from the database. For example, if Steve wants to
check his balance, the plugin would get his balance directly from the database.

```java
// Multi-server compatible

void execBalanceCommand(Player player) {
  // Run asynchronously so the database operation doesn't block the main thread
  Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    int balance = Database.getBalance(player);
    player.sendMessage("Your balance is " + balance);
  });
}
```

## Option 2: Polling with MySQL (not preferred)

These database operations can be slow, so sometimes they'll need to be kept
in memory to speed up tasks. A common example of this is storing the player's
data when they log into the server.

```java
// Not multi-server compatible

Map<Player, Integer> playerBalances = new HashMap<>();

@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
  int balance = Database.getBalance(event.getPlayer());
  playerBalances.put(event.getPlayer(), balance);
}

void execBalanceCommand(Player player) {
  // We don't need to run asynchronously since the balance is stored in memory
  int balance = Database.getBalance(player);
  player.sendMessage("Your balance is " + balance);
}
```

However, what happens if another server updates the balance on the database?
Since we store the balance in memory, we will not know that it's been updated.
To solve this, we can poll the database every second to ensure we are aware
of any new changes to the database.

```java
// Multi-server compatible

@Override
public void onEnable() {
  // Poll the database every second to update the balances
  Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
    for (Player player : playerBalances.keySet()) {
        int updatedBalance = Database.getBalance(player);
        playerBalances.put(player, updatedBalance);
    }
  }, 20, 20);
}
```

## Options 3: Notifications with PostgreSQL

However, polling is not efficient. It creates a database operation every time
the data is polled, even when there have been no changes to the data. It also
introduces latency as the data is only polled every second, so someone could
take advantage of this second of latency to duplicate money before the balances
are updated. We could increase the frequency of the polling to more than once
every second, but that would just be even more inefficient.

To solve this, we will use a notification model. This is where another client
notifies us whenever data is changed. Unfortunately, MySQL does not support
notifications, so we'll have to use another service such as PostgreSQL.

To set up PostgreSQL with your Bukkit plugin, first add the following to the
plugin.yml to load the PostgreSQL library into Spigot:

```yaml
libraries:
  - com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9
```

Then, add the following dependency to your pom.xml (note that this dependency
is on maven central, so you don't need to add a repository):

```xml
<dependencies>
  <dependency>
    <groupId>com.impossibl.pgjdbc-ng</groupId>
    <artifactId>pgjdbc-ng</artifactId>
    <version>0.8.9</version>
  </dependency>
</dependencies>
```

Finally, set up the notification model in your plugin:

```java
// Multi-server compatible

private PGConnection connection;

@Override
public void onEnable() {
  try {
    // Create the connection
    PGDataSource ds = new PGDataSource();
    ds.setServerName("localhost");
    ds.setDatabaseName("test");
    ds.setUser("postgres");
    ds.setPassword("password");

    connection = (PGConnection) ds.getConnection();

    // Listen for notifications
    connection.addNotificationListener(new PGNotificationListener() {
      public void notification(int processId, String channelName, String payload) {
        getLogger().info("notification for " + channelName + ": " + payload);
        // This is where you'd handle notifications from other servers
      }
    });

    // Listen to the `test` channel
    try (Statement statement = connection.createStatement()) {
      statement.execute("LISTEN test");
    }

    // Send a notification to the `test` channel
    // This will send the notification to every server listening to the `test` channel
    sendNotification("test", "payload goes here");
  } catch (Exception e) {
    throw new RuntimeException(e);
  }
}

public void sendNotification(String channel, String payload) throws SQLException {
  try (Statement statement = connection.createStatement()) {
    statement.execute("NOTIFY " + channel + ", '" + payload + "'");
  }
}
```

## Options 4: Subscriptions with Firestore (not covered)

The final option is a subscription-based model. This is where the database will
send you data as it changes. Firestore is one such service that provides this
model, but unfortunately it can't be self-hosted and thus will not be covered
here.