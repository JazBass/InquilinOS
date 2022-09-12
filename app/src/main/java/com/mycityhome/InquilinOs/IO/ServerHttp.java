package com.mycityhome.InquilinOs.IO;

import android.util.Log;

import com.mycityhome.InquilinOs.UI.MainActivity;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class ServerHttp {

    private final String TAG = "ServerHttp";
    HttpServer mHttpServer;
    MainActivity activity;
    BLEConnection bleConnection;

    public ServerHttp(MainActivity activity) {
        this.activity = activity;
        bleConnection = new BLEConnection(activity);
    }

    public String streamToString(InputStream inputStream) {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void startServer(int port) throws IOException {
        mHttpServer = HttpServer.create(new InetSocketAddress(port), 0);
        if (mHttpServer != null) {
            mHttpServer.setExecutor(Executors.newCachedThreadPool());
            mHttpServer.createContext("/lock/get", handlerAsk);
            mHttpServer.createContext("/lock/open", handlerUniversal);
            mHttpServer.start();
            Log.i(TAG, "startServer: " + mHttpServer.getAddress().toString());
        }
    }

    private final HttpHandler handlerUniversal = httpExchange -> {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            String response =  httpExchange.getRequestURI().toString();
            String [] parts = response.split("=");
            String code = parts[1];
            Log.i(TAG, ": "+code);
            bleConnection.sendBleRequest(code, httpExchange);
        }
    };
    /*------------------------------Aqui pedimos rndNumer y Bateria--------------------------------*/
    private final HttpHandler handlerAsk = httpExchange -> {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            String code = "5530";
            bleConnection.sendBleRequest(code, httpExchange);
        }
    };

    public void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream out = httpExchange.getResponseBody();
        out.write(response.getBytes());
        out.close();
    }



}
