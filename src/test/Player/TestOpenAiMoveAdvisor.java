package Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

/** Tests parsing and validation of LLM move responses without making live API calls. */
public class TestOpenAiMoveAdvisor {

    @Test
    void parsesValidMoveAndReason() {
        Optional<MoveAdvice> advice = OpenAiMoveAdvisor.parseAdvice(
                "MOVE_ID: 1\nREASON: This move improves the road network.", 3);

        assertTrue(advice.isPresent());
        assertEquals(1, advice.get().moveId());
        assertEquals("This move improves the road network.", advice.get().reason());
    }

    @Test
    void rejectsMoveThatWasNotOffered() {
        Optional<MoveAdvice> advice = OpenAiMoveAdvisor.parseAdvice(
                "MOVE_ID: 7\nREASON: Build here.", 2);

        assertTrue(advice.isEmpty());
    }

    @Test
    void rejectsMalformedResponse() {
        assertTrue(OpenAiMoveAdvisor.parseAdvice("Build a road.", 2).isEmpty());
        assertTrue(OpenAiMoveAdvisor.parseAdvice("", 2).isEmpty());
    }

    @Test
    void usesDefaultReasonWhenResponseOmitsIt() {
        Optional<MoveAdvice> advice = OpenAiMoveAdvisor.parseAdvice("MOVE_ID: 0", 1);

        assertTrue(advice.isPresent());
        assertEquals("Selected by the LLM advisor.", advice.get().reason());
    }

    @Test
    void callsResponsesApiAndReadsSelectedMove() throws Exception {
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> authorization = new AtomicReference<>();
        HttpServer server = startServer(200,
                "{\"output\":[{\"content\":[{\"type\":\"output_text\","
                        + "\"text\":\"MOVE_ID: 1\\nREASON: Save resources for a city.\"}]}]}",
                requestBody, authorization);

        try {
            OpenAiMoveAdvisor advisor = new OpenAiMoveAdvisor(
                    "test-key", "test-model", endpoint(server), HttpClient.newHttpClient());

            Optional<MoveAdvice> advice = advisor.advise(
                    "victory points=2, brick=1",
                    List.of(
                            new MoveOption(0, 1.0, "Build road [1,2]"),
                            new MoveOption(1, 2.0, "Build settlement at node 3")));

            assertTrue(advice.isPresent());
            assertEquals(1, advice.get().moveId());
            assertEquals("Bearer test-key", authorization.get());
            assertTrue(requestBody.get().contains("test-model"));
            assertTrue(requestBody.get().contains("Build settlement at node 3"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void fallsBackWhenApiReturnsAnError() throws Exception {
        HttpServer server = startServer(500, "service unavailable", null, null);

        try {
            OpenAiMoveAdvisor advisor = new OpenAiMoveAdvisor(
                    "test-key", "test-model", endpoint(server), HttpClient.newHttpClient());

            Optional<MoveAdvice> advice = advisor.advise(
                    "victory points=0",
                    List.of(new MoveOption(0, 1.0, "Build road [1,2]")));

            assertTrue(advice.isEmpty());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void handlesEmptyMovesAndInvalidConfiguration() {
        OpenAiMoveAdvisor advisor = new OpenAiMoveAdvisor("test-key", "test-model");

        assertTrue(advisor.advise("state", List.of()).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> new OpenAiMoveAdvisor("", "test-model"));
    }

    private HttpServer startServer(int status, String responseBody,
            AtomicReference<String> requestBody, AtomicReference<String> authorization) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/v1/responses", exchange -> {
            if (requestBody != null) {
                requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            }
            if (authorization != null) {
                authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            }

            byte[] response = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        return server;
    }

    private URI endpoint(HttpServer server) {
        return URI.create("http://localhost:" + server.getAddress().getPort() + "/v1/responses");
    }
}
