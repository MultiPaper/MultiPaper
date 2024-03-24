package puregero.multipaper.server;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class CommandLineInput extends Thread {

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.equalsIgnoreCase("shutdown")) {
                log.info("Shutting down servers...");
                ServerConnection.shutdownAndWait();
                System.exit(0);
            } else {
                log.info("Unknown command, use 'shutdown' to shutdown all servers or ctrl+c to stop just this master server");
            }
        }
    }
}
