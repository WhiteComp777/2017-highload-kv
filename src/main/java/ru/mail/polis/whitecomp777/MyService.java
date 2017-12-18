package ru.mail.polis.whitecomp777;

import org.jetbrains.annotations.NotNull;
import ru.mail.polis.KVService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by root on 09/10/2017.
 */
public class MyService implements KVService {

    private static final String PREFIX = "id=";

    @NotNull
    private final HttpServer server;
    @NotNull
    private final MyDAO dao;

    @NotNull
    private static String extractId(@NotNull final String query){

        if(!query.startsWith(PREFIX)){
            throw new IllegalArgumentException("Wrong query");
        }
        return query.substring(PREFIX.length());
    }

    public MyService(int port, @NotNull final MyDAO dao) throws IOException{
        this.server = HttpServer.create(
                new InetSocketAddress(port), 0
        );
        this.dao = dao;


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
                    String id="";
                    try {
                        id = this.extractId(httpExchange.getRequestURI().getQuery());
                    }
                    catch (IllegalArgumentException e){
                        httpExchange.sendResponseHeaders(404, 0);
                        httpExchange.close();
                    }
                    if(id.length()==0){
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
                                final int contentLengh = Integer.valueOf(httpExchange.getRequestHeaders().getFirst("Content-Length"));

                                final byte[] putValue = new byte[contentLengh];
                                if(contentLengh > 0 && httpExchange.getRequestBody().read(putValue) != contentLengh){
                                    throw new IOException("Cant read at once");
                                }
                                dao.upsert(id, putValue);
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
