package puregero.multipaper.server.bootstrap;

import lombok.extern.slf4j.Slf4j;
import puregero.multipaper.server.CommandLineInput;
import puregero.multipaper.server.MultiPaperServer;
import puregero.multipaper.server.proxy.ProxyServer;

@Slf4j
public class MultiPaperStandalone {

    public static void main(String[] args) {
        MultiPaperServer.DAEMON = false;

        String address = null;
        int port = MultiPaperServer.DEFAULT_PORT;

        if (args.length > 0) {
            if (args[0].contains(":")) {
                address = args[0].substring(0, args[0].indexOf(':'));
                args[0] = args[0].substring(args[0].indexOf(':') + 1);
            }
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error("Usage: java -jar MultiPaperServer.jar <[address:]port> [proxy port]");

                System.exit(1);
            }
        }

        if (args.length > 1) {
            try {
                ProxyServer.openServer(Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                log.error("Usage: java -jar MultiPaperServer.jar <[address:]port> [proxy port]");

                System.exit(1);
            }
        }

        new MultiPaperServer(address, port);

        new CommandLineInput().run();
    }
}
