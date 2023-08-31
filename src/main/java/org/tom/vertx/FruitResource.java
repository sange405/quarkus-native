package org.tom.vertx;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {
    private static final Logger log = LoggerFactory.getLogger(FruitResource.class);
    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool mySQLPool;

    @GET
    @Path("/list")
    public Multi<Fruit> findAll() {
        Multi<Fruit> all = Fruit.findAll(mySQLPool);
        return all;
    }

    @GET
    @Path("/info/{id}")
    public Uni<Response> info(@PathParam("id") Long id) {
        log.info("info: id:{}", id);
        return Fruit.findById(mySQLPool, id)
                .onItem()
                .transform(fruit -> fruit != null ? Response.ok(fruit) : Response.status(Response.Status.NOT_FOUND))
                .onItem()
                .transform(Response.ResponseBuilder::build);
    }

    @POST
    @Path("/save")
    public Uni<Response> create(Fruit fruit) {
        return fruit.save(mySQLPool)
                .onItem()
                .transform(id -> URI.create("/fruits/info/" + id))
                .onItem()
                .transform(uri -> Response.created(uri).build());
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return Fruit.delete(mySQLPool, id)
                .onItem()
                .transform(deleted -> deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                .onItem()
                .transform(status -> Response.status(status).build());
    }


    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        Multi<Fruit> all = Fruit.findAll(mySQLPool);
        return "hello world!";
    }



}