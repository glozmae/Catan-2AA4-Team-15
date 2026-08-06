package Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

/** Tests the Claude advisor without making paid API requests. */
public class TestClaudeMoveAdvisor {

    @Test
    void parsesValidMoveAndReason() {
        Optional<MoveAdvice> advice = ClaudeMoveAdvisor.parseAdvice(
                "MOVE_ID: 1\nREASON: This move improves the road network.", 3);

        assertTrue(advice.isPresent());
        assertEquals(1, advice.get().moveId());
        assertEquals("This move improves the road network.", advice.get().reason());
    }

    @Test
    void rejectsInvalidResponses() {
        assertTrue(ClaudeMoveAdvisor.parseAdvice("MOVE_ID: 7\nREASON: Build here.", 2).isEmpty());
        assertTrue(ClaudeMoveAdvisor.parseAdvice("Build a road.", 2).isEmpty());
        assertTrue(ClaudeMoveAdvisor.parseAdvice("", 2).isEmpty());
    }

    @Test
    void usesDefaultReasonWhenResponseOmitsIt() {
        Optional<MoveAdvice> advice = ClaudeMoveAdvisor.parseAdvice("MOVE_ID: 0", 1);

        assertTrue(advice.isPresent());
        assertEquals("Selected by Claude.", advice.get().reason());
    }

    @Test
    void usesOfficialSdkAndReadsSelectedMove() throws Exception {
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> apiKey = new AtomicReference<>();
        HttpServer server = startServer(200, successResponse(), requestBody, apiKey);

        AnthropicClient client = testClient(server);
        try {
            ClaudeMoveAdvisor advisor = new ClaudeMoveAdvisor(client, "claude-haiku-4-5");
            Optional<MoveAdvice> advice = advisor.advise(
                    "victory points=2, brick=1",
                    List.of(
                            new MoveOption(0, 1.0, "Build road [1,2]"),
                            new MoveOption(1, 2.0, "Build settlement at node 3")));

            assertTrue(advice.isPresent());
            assertEquals(1, advice.get().moveId());
            assertEquals("test-key", apiKey.get());
            assertTrue(requestBody.get().contains("claude-haiku-4-5"));
            assertTrue(requestBody.get().contains("Build settlement at node 3"));
        } finally {
            client.close();
            server.stop(0);
        }
    }

    @Test
    void fallsBackWhenClaudeReturnsAnError() throws Exception {
        HttpServer server = startServer(500, "service unavailable", null, null);

        AnthropicClient client = testClient(server);
        try {
            ClaudeMoveAdvisor advisor = new ClaudeMoveAdvisor(client, "claude-haiku-4-5");
            Optional<MoveAdvice> advice = advisor.advise(
                    "victory points=0",
                    List.of(new MoveOption(0, 1.0, "Build road [1,2]")));

            assertTrue(advice.isEmpty());
        } finally {
            client.close();
            server.stop(0);
        }
    }

    @Test
    void handlesEmptyMovesAndInvalidConfiguration() {
        ClaudeMoveAdvisor advisor = new ClaudeMoveAdvisor("test-key", "claude-haiku-4-5");

        assertTrue(advisor.advise("state", List.of()).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> new ClaudeMoveAdvisor("", "claude-haiku-4-5"));
    }

    private AnthropicClient testClient(HttpServer server) {
        return AnthropicOkHttpClient.builder()
                .apiKey("test-key")
                .baseUrl("http://localhost:" + server.getAddress().getPort())
                .timeout(Duration.ofSeconds(2))
                .maxRetries(0)
                .build();
    }

    private HttpServer startServer(int status, String responseBody,
            AtomicReference<String> requestBody, AtomicReference<String> apiKey) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/v1/messages", exchange -> {
            if (requestBody != null) {
                requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            }
            if (apiKey != null) {
                apiKey.set(exchange.getRequestHeaders().getFirst("x-api-key"));
            }

            byte[] response = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        return server;
    }

    private String successResponse() {
        return "{\"id\":\"msg_test\",\"type\":\"message\",\"role\":\"assistant\","
                + "\"model\":\"claude-haiku-4-5-20251001\","
                + "\"content\":[{\"type\":\"text\","
                + "\"text\":\"MOVE_ID: 1\\nREASON: Save resources for a city.\"}],"
                + "\"stop_reason\":\"end_turn\",\"stop_sequence\":null,"
                + "\"usage\":{\"input_tokens\":10,\"output_tokens\":10}}";
    }
}
