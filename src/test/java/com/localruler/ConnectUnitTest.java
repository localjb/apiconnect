package com.localruler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class ConnectUnitTest {

    private MockWebServer server;
    private Connect connect;

    @BeforeEach
    void initialize() throws IOException{
        server = new MockWebServer();
        server.start(8099);
        this.connect = new Connect();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void testGet() throws Exception{
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"success\"}"));

        connect.setRequestMethod(Connect.HttpMethod.GET);
        connect.addHeader(Connect.RequestHeader.CONTENT_TYPE.getType(),"application/json");
        connect.addHeader("GetHeader1", "This is get header 1");
        connect.addHeader("testGetHeader", "The Get Header Test");
        HttpURLConnection connection = (HttpURLConnection)connect.doRequest("http://localhost:8099/status");
        int responseCode = connection.getResponseCode();

        RecordedRequest recordedRequest = server.takeRequest(5, TimeUnit.SECONDS);
        String header1 = recordedRequest.getHeaders().get("GetHeader1");
        assertEquals("This is get header 1", header1);
        assertEquals("GET", recordedRequest.getMethod());

        String responseBody = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("{\"status\":\"success\"}", responseBody);

        assertEquals(200, responseCode, "Expected a 200 OK response");
        connection.disconnect();
    }

    @Test
    public void testPost() throws Exception{
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"success\"}"));

        connect.setRequestMethod(Connect.HttpMethod.POST);
        connect.addHeader(Connect.RequestHeader.CONTENT_TYPE.getType(), "application/json");
        connect.addHeader("PostHeader2", "This is the Post");
        connect.addHeader("X-API-Key", "abcdefg123");

        HttpURLConnection connection = (HttpURLConnection)connect.doRequest("http://localhost:8099/status");
        int responseCode = connection.getResponseCode();

        RecordedRequest recordedRequest = server.takeRequest(5, TimeUnit.SECONDS);
        String header1 = recordedRequest.getHeaders().get("PostHeader2");
        assertEquals("This is the Post", header1);
        assertEquals("abcdefg123", recordedRequest.getHeader("X-API-Key"));
        assertEquals("POST", recordedRequest.getMethod());

        String responseBody = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("{\"status\":\"success\"}", responseBody);

        assertEquals(200, responseCode, "Expected a 200 OK response");
        connection.disconnect();

    }

    @Test
    public void testException() throws Exception{
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"success\"}"));

        connect.setRequestMethod(Connect.HttpMethod.POST);
        connect.addHeader(Connect.RequestHeader.CONTENT_TYPE.getType(), "application/json");
        connect.addHeader("PostHeader2", "This is the Post");
        connect.addHeader("X-API-Key", "abcdefg123");
        //malformed url will cause an Exception
        HttpURLConnection connection = (HttpURLConnection)connect.doRequest("http//://localhost:8099/status");

        assertEquals(null, connection);
    }

    @Test
    public void testHeaders() throws Exception{
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"success\"}"));

        connect.setRequestMethod(Connect.HttpMethod.POST);
        connect.addHeader(Connect.RequestHeader.CONTENT_TYPE.getType(), "application/json");
        connect.addHeader("PostHeader2", "This is the Post");
        connect.addHeader("X-API-Key", "abcdefg123");
        connect.removeHeader("PostHeader2");

        HttpURLConnection connection = (HttpURLConnection)connect.doRequest("http://localhost:8099/status");
        int responseCode = connection.getResponseCode();

        RecordedRequest recordedRequest = server.takeRequest(5, TimeUnit.SECONDS);
        String header1 = recordedRequest.getHeaders().get("PostHeader2");
        assertEquals(null, header1);
        assertEquals("abcdefg123", recordedRequest.getHeader("X-API-Key"));
        assertEquals("POST", recordedRequest.getMethod());

        String responseBody = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("{\"status\":\"success\"}", responseBody);

        assertEquals(200, responseCode, "Expected a 200 OK response");
        connection.disconnect();

    }

    @Test
    public void testParameters() throws Exception{
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"success\"}"));

        connect.setRequestMethod(Connect.HttpMethod.GET);
        connect.addHeader(Connect.RequestHeader.CONTENT_TYPE.getType(),"application/json");
        connect.addHeader("GetHeader1", "This is get header 1");
        connect.addHeader("testGetHeader", "The Get Header Test");
        connect.addParameter("param1", "This is parameter 1 & 1");
        connect.addParameter("param2", "Parameter 2");
        connect.addParameter("param3", "Parameter 3");
        connect.removeParameter("param2");
        HttpURLConnection connection = (HttpURLConnection)connect.doRequest("http://localhost:8099/status");
        int responseCode = connection.getResponseCode();

        RecordedRequest recordedRequest = server.takeRequest(5, TimeUnit.SECONDS);
        String header1 = recordedRequest.getHeaders().get("GetHeader1");
        assertEquals("This is get header 1", header1);
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/status?param1=This+is+parameter+1+%26+1&param3=Parameter+3", recordedRequest.getPath());


        String responseBody = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("{\"status\":\"success\"}", responseBody);

        assertEquals(200, responseCode, "Expected a 200 OK response");
        connection.disconnect();
    }
}
