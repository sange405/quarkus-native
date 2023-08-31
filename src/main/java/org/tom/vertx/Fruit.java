package org.tom.vertx;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Fruit {

    private Logger log = LoggerFactory.getLogger(Fruit.class);

    public Long id;

    public String name;

    public Fruit() {
    }

    public Fruit(String name) {
        this.name = name;
    }

    public Fruit(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 查询所有
     */
    public static Multi<Fruit> findAll(MySQLPool client) {
        Multi<Row> rowMulti = client.query("SELECT id, name FROM fruits ORDER BY name ASC")
                .execute()
                .onItem()
                .transformToMulti(set -> Multi.createFrom().iterable(set));
        Multi<Fruit> transform = rowMulti.onItem()
                .transform(Fruit::from);
        return transform;
    }

    /**
     * id查询
     */
    public static Uni<Fruit> findById(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT id, name FROM fruits WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem()
                .transform(RowSet::iterator)
                .onItem().
                transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    /**
     * 添加
     */
    public Uni<Long> save(MySQLPool client) {
        RowSet<Row> rowSet = client.preparedQuery("INSERT INTO fruits (name) VALUES (?)")
                .execute(Tuple.of(name))
                .await()
                .indefinitely();
        RowSet<Row> rowRowSet = client.preparedQuery("select @@IDENTITY as id").execute().await()
                .indefinitely();
        RowIterator<Row> iterator = rowRowSet.iterator();

        Long id = null;
        while (iterator.hasNext()){
            id = iterator.next().getLong("id");
        }
        log.info("current saved id:{}", id);
        return Uni.createFrom().item(id);
    }

    /**
     * 刪除
     */
    public static Uni<Boolean> delete(MySQLPool client, Long id) {
        return client.preparedQuery("DELETE FROM fruits WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem()
                .transform(rowSet -> rowSet.rowCount() == 1);
    }

    /**
     * 事务-添加两天数据
     */
    public static Uni<Void> insertTwoFruits(MySQLPool client, Fruit fruit1, Fruit fruit2) {
        return client.withTransaction(conn -> {
            Uni<RowSet<Row>> insertOne = conn.preparedQuery("INSERT INTO fruits (name) VALUES (?) RETURNING id")
                    .execute(Tuple.of(fruit1.name));
            Uni<RowSet<Row>> insertTwo = conn.preparedQuery("INSERT INTO fruits (name) VALUES (?) RETURNING id")
                    .execute(Tuple.of(fruit2.name));

            return Uni.combine().all().unis(insertOne, insertTwo)
                    // Ignore the results (the two ids)
                    .discardItems();
        });
    }

    /**
     * 批量更新
     */
    public static Uni<Integer> batchUpdate(MySQLPool client) {
        PreparedQuery<RowSet<Row>> preparedQuery = client.preparedQuery("UPDATE fruits SET name = $1 WHERE id = $2");

        Uni<RowSet<Row>> rowSet = preparedQuery.executeBatch(
                Arrays.asList(
                        Tuple.of("Orange", 1),
                        Tuple.of("Pear", 2),
                        Tuple.of("Apple", 3)
                )
        );

        Uni<Integer> totalAffected = rowSet.onItem().transform(res -> {
            int total = 0;
            do {
                total += res.rowCount();
            } while ((res = res.next()) != null);
            return total;
        });
        return totalAffected;
    }

    /**
     * 批量-添加&返回添加值
     */
    public static Multi<Row> batchSaveAndReturn(MySQLPool client) {
        PreparedQuery<RowSet<Row>> preparedQuery = client.preparedQuery("INSERT INTO fruits (name) VALUES ($1) RETURNING *");

        Uni<RowSet<Row>> rowSet = preparedQuery.executeBatch(Arrays.asList(
                Tuple.of("Orange"),
                Tuple.of("Pear"),
                Tuple.of("Apple")));

        // Generate a Multi of RowSet items
        Multi<RowSet<Row>> rowSets = rowSet.onItem().transformToMulti(res -> {
            return Multi.createFrom().generator(() -> res, (rs, emitter) -> {
                RowSet<Row> next = null;
                if (rs != null) {
                    emitter.emit(rs);
                    next = rs.next();
                }
                if (next == null) {
                    emitter.complete();
                }
                return next;
            });
        });

        // Transform each RowSet into Multi of Row items and Concatenate
        Multi<Row> rows = rowSets.onItem()
                .transformToMultiAndConcatenate(Multi.createFrom()::iterable);
        return rows;
    }

    /**
     * 数据转换
     */
    private static Fruit from(Row row) {
        return new Fruit(row.getLong("id"), row.getString("name"));
    }

    @Override
    public String toString() {
        return "Fruit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
