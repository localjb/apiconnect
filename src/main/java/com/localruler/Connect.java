package com.localruler;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Connect {

    private HttpURLConnection httpURLConnection;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private HttpMethod httpMethod;

    public Connect(){
        this.parameters = new HashMap<String, String>();
        this.headers = new HashMap<String, String>();
    }

    public void setHttpURLConnection(URL url) throws IOException {
        this.httpURLConnection = (HttpURLConnection) url.openConnection();
    }

    public HttpURLConnection getHttpURLConnection(){
        return this.httpURLConnection;
    }

    public void setRequestMethod(HttpMethod httpMethod){
        this.httpMethod = httpMethod;
    }

    public void addHeader(String headerName, String headerValue){
        this.headers.put(headerName, headerValue);
    }

    public void addParameter(String name, String value){
        this.parameters.put(name, value);
    }

    /*
    * Will return null if there is an Exception thrown
     */
    public URLConnection doRequest(String url){
        try {
            switch (this.httpMethod) {
                case GET:
                    buildGetRequest(url);
                    break;
                case POST:
                    buildPostRequest(url);
                    break;
                default:
            }
        }catch(Exception e){
            //handle exception gracefully
        }
        return this.httpURLConnection;
    }

    private void buildPostRequest(String urlStr) throws Exception{
        URL url = new URL(urlStr);
        this.setHttpURLConnection(url);
        this.httpURLConnection.setRequestMethod(HttpMethod.POST.name());
        this.headers.forEach((k,v) -> {
            this.httpURLConnection.setRequestProperty(k,v);
        });
    }

    private void buildGetRequest(String urlStr) throws Exception{
        StringJoiner queryParameters = new StringJoiner("&");
        this.parameters.forEach((k, v) -> {
            queryParameters.add(URLEncoder.encode(k, StandardCharsets.UTF_8) + "=" +
                    URLEncoder.encode(v, StandardCharsets.UTF_8));
        });
        URL url = new URL(urlStr + "?" + queryParameters.toString());
        this.setHttpURLConnection(url);
        this.httpURLConnection.setRequestMethod(HttpMethod.GET.name());
        this.headers.forEach((k,v) -> {
            this.httpURLConnection.setRequestProperty(k,v);
        });
    }


    public enum HttpMethod{
        GET, POST, PUT, DELETE;
    }

    public enum RequestHeader{
        ACCEPT("Accept"),
        USER_AGENT("User-Agent"),
        AUTHORIZATION("Authorization"),
        CONTENT_TYPE("Content-Type"),
        COOKIE("Cookie"),
        ACCEPT_LANGUAGE("Accept-Language"),
        ACCEPT_ENCODING("Accept-Encoding"),
        REFERER("Referer"),
        IF_MODIFIED_SINCE("If-Modified-Since");

        private final String type;

        private RequestHeader(String type) {
            this.type = type;
        }

        // Public getter method to access the value
        public String getType() {
            return this.type;
        }

    }
}
