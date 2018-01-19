package ru.mail.polis.whitecomp777;

import org.jetbrains.annotations.NotNull;
import ru.mail.polis.KVService;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Set;

/**
 * Created by root on 09/10/2017.
 */
public class MyService implements KVService {

    private static final String PREFIX = "id=";

    @NotNull
    private final HttpServer server;
    @NotNull
    private final MyDAO dao;

    @NotNull private final Set<String> topology;

    @NotNull
    private static String extractId(@NotNull final String query){

        final int PREFIX_LENGTH = PREFIX.length();
        if(!query.startsWith(PREFIX)){
            throw new IllegalArgumentException("Wrong query");
        }
        return query.substring(PREFIX_LENGTH);
    }

    public MyService(int port, @NotNull final MyDAO dao, @NotNull Set<String> topology) throws IOException{
        this.server = HttpServer.create(
                new InetSocketAddress(port), 0
        );
        this.dao = dao;
        this.topology = topology;


        this.server.createContext("/",
                httpExchange -> {
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                }
        );


        this.server.createContext("/v0/status",
                httpExchange -> {
                    final String response = "ONLINE";
                    httpExchange.sendResponseHeaders(200, response.length());
                    httpExchange.getResponseBody().write(response.getBytes());
                    httpExchange.close();
                }
        );

        this.server.createContext("/v0/entity",
                httpExchange -> {
                    String id = "";
                    try {
                        id = this.extractId(httpExchange.getRequestURI().getQuery());
                    }
                    catch (IllegalArgumentException e){
                        httpExchange.sendResponseHeaders(404, 0);
                        httpExchange.close();
                    }
                    if(id.length() == 0){
                        httpExchange.sendResponseHeaders(400, 0);
                        httpExchange.close();
                    }
                    else{
                        switch (httpExchange.getRequestMethod()){
                            case "GET":
                                final byte[] getValue;
                                try{
                                    getValue = dao.get(id);
                                }
                                catch (IOException e){
                                    httpExchange.sendResponseHeaders(404, 0);
                                    break;
                                }

                                httpExchange.sendResponseHeaders(200, getValue.length);
                                httpExchange.getResponseBody().write(getValue);
                                break;
                            case "DELETE":
                                dao.delete(id);
                                httpExchange.sendResponseHeaders(202, 0);
                                break;

                            case "PUT":
                                InputStream in = httpExchange.getRequestBody();
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                byte[] buf = new byte[2048];
                                int read = 0;
                                while ((read = in.read(buf)) != -1) {
                                    out.write(buf, 0, read);
                                }

                                dao.upsert(id, out.toByteArray());
                                httpExchange.sendResponseHeaders(201, 0);
                                break;

                            default:
                                httpExchange.sendResponseHeaders(405, 0);

                        }
                        httpExchange.close();
                    }
                }
        );




    }

    @Override
    public void start() {
        this.server.start();
    }

    @Override
    public void stop() {
        this.server.stop(0);
    }
}
