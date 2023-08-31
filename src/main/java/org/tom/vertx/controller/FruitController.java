package org.tom.vertx.controller;

import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tom.vertx.Fruit;
import org.tom.vertx.entity.FruitEntity;
import org.tom.vertx.service.FruitService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * api 文档位置： http://localhost:8080/q/swagger-ui
 */
@Path("/mybatis/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitController {
    private static final Logger log = LoggerFactory.getLogger(FruitController.class);
    @Inject
    FruitService fruitService;

    @GET
    @Path("/info/{id}")
    public Uni<Response> info(@PathParam("id") Long id) {
        FruitEntity result = fruitService.getById(id);
        log.info("search id:{} result:{}", id, result);
        if (result != null) {
            return Uni.createFrom().item(Response.ok(result).build());
        } else {
            return Uni.createFrom().item(Response.ok().status(Response.Status.NOT_FOUND).build());
        }
    }

    @POST
    @Path("/create")
    public Uni<Response> create(Fruit fruit) {
        Integer result = fruitService.create(fruit.id, fruit.name);
        log.info("add:{} result:{}", fruit, result);
        return this.info(fruit.id);
    }

    @PUT
    @Path("/update")
    public Uni<Response> update(Fruit fruit) {
        Integer result = fruitService.update(fruit.id, fruit.name);
        log.info("update{} result:{}", fruit, result);
        return this.info(fruit.id);
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        Integer result = fruitService.remove(id);
        log.info("delete id:{} result:{}", id, result);
        return this.info(id);
    }

    /**
     * 尝试用了 mybatis xml的mapper读数据，读取不到数据
     *
     */
    @GET
    @Path("list")
    public Uni<Response> list() {
        List<FruitEntity> result = fruitService.listAll();
        log.info("list: {} result:{}", "all table", result);
        if (result != null) {
            return Uni.createFrom().item(Response.ok(result).build());
        } else {
            return Uni.createFrom().item(Response.ok().status(Response.Status.NOT_FOUND).build());
        }
    }
}
