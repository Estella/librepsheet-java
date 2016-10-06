package com.aaronbedra.librepsheet.example;

import com.repsheet.librepsheet.Actor;
import com.repsheet.librepsheet.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class Main {
    static void repsheet(Connection connection, Logger logger, Request request, Response response) {
        Actor actor = connection.lookup(Actor.Type.IP, request.ip());

        switch (actor.getStatus()) {
            case WHITELISTED:
                logger.info(request.ip() + " has been whitelisted. Passing request");
                break;
            case BLACKLISTED:
                logger.info(request.ip() + " has been blacklisted. Denying request");
                halt(403, "<h1>Forbidden</h1>");
            case MARKED:
                logger.info(request.ip() + " has been marked.");
                request.attribute("X-Repsheet", true);
                break;
            default:
                break;
        }
    }

    static String index(Request request, Response response) {
        if (request.attributes().contains("X-Repsheet")) {
            if (request.attribute("X-Repsheet")) {
                return "Something Different";
            }
        }
        return "Hello World";
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        Connection connection = new Connection("localhost", 6379);
        before((request, response) -> repsheet(connection, logger, request, response));
        get("/", Main::index);
    }
}
