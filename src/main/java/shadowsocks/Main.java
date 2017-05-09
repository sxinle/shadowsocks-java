package shadowsocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import shadowsocks.util.Config;
import shadowsocks.crypto.CryptoFactory;

public class Main {

    public static Logger log = LogManager.getLogger(Main.class.getName());

    public static final String VERSION = "0.6.2";

    public static void main(String argv[]) {
        log.info("Shadowsocks " + VERSION);
        Config.getConfigFromArgv(argv);
        Config.getConfigFromFile();
        //make sure this method could work.
        try {
            CryptoFactory.create(Config.get().getMethod(), Config.get().getPassword());
        } catch (Exception e) {
            log.fatal("Error crypto method", e);
            return;
        }
        Config.get().printConfig();
        new Shadowsocks(Config.get().isServerMode()).boot();
    }
}
