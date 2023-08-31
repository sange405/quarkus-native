package org.tom.vertx;

import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * fruit表结构初始化
 * startup 表示系统启动后，调用此类
 */
@Startup
@ApplicationScoped
public class FruitInit {
    private static final Logger log = LoggerFactory.getLogger(FruitInit.class);

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool mySQLPool;

    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true")
    boolean schemaCreate;

    @PostConstruct
    void config() {
        log.info("fruit init start .........");
        if (schemaCreate) {
            initdb();
        }
        log.info("fruit init end .........");
    }

    private void initdb() {
        mySQLPool.query("DROP TABLE IF EXISTS fruits")
                .execute()
                .flatMap(r -> mySQLPool.query("CREATE TABLE fruits (id int(11) auto_increment  PRIMARY KEY, name varchar(64) NOT NULL)").execute())
                .flatMap(r -> mySQLPool.query("INSERT INTO fruits (name) VALUES ('Orange')").execute())
                .flatMap(r -> mySQLPool.query("INSERT INTO fruits (name) VALUES ('Pear')").execute())
                .flatMap(r -> mySQLPool.query("INSERT INTO fruits (name) VALUES ('Apple')").execute())
                .await().indefinitely();
    }
}
