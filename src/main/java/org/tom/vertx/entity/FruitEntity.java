package org.tom.vertx.entity;

import io.vertx.mutiny.sqlclient.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FruitEntity {

    private Logger log = LoggerFactory.getLogger(FruitEntity.class);

    public Long id;

    public String name;

    public FruitEntity() {
    }

    public FruitEntity(String name) {
        this.name = name;
    }

    public FruitEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }


    /**
     * 数据转换
     */
    private static FruitEntity from(Row row) {
        return new FruitEntity(row.getLong("id"), row.getString("name"));
    }

    @Override
    public String toString() {
        return "FruitEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
