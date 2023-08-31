package org.tom.vertx;


import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusMain
public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String... args) {
        Quarkus.run(MyApp.class, args);
    }

    public static class MyApp implements QuarkusApplication {
        @Override
        public int run(String... args) throws Exception {
            log.info("Do startup logic here");
            Quarkus.waitForExit();
            return 0;
        }
    }
}
