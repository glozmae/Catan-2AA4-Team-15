package Player;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Optional OpenAI-backed advisor for the computer player.
 * The game remains rule-based when no API key is configured or a request fails.
 */
public final class OpenAiMoveAdvisor implements MoveAdvisor {
    private static final URI DEFAULT_ENDPOINT = URI.create("https://api.openai.com/v1/responses");
    private static final String DEFAULT_MODEL = "gpt-5.6-luna";
    private static final Pattern MOVE_PATTERN = Pattern.compile("(?i)MOVE_ID\\s*:\\s*(\\d+)");
    private static final Pattern REASON_PATTERN = Pattern.compile("(?i)REASON\\s*:\\s*(.+)");

    private final String apiKey;
    private final String model;
    private final URI endpoint;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Creates an advisor that uses the standard OpenAI Responses API endpoint.
     *
     * @param apiKey OpenAI API key
     * @param model model id to call
     */
    public OpenAiMoveAdvisor(String apiKey, String model) {
        this(apiKey, model, DEFAULT_ENDPOINT, HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build());
    }

    OpenAiMoveAdvisor(String apiKey, String model, URI endpoint, HttpClient httpClient) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("An OpenAI API key is required");
        }
        this.apiKey = apiKey;
        this.model = model == null || model.isBlank() ? DEFAULT_MODEL : model;
        this.endpoint = endpoint;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Creates an advisor from environment variables. If no key is present,
     * returns the disabled advisor so the original AI remains unchanged.
     *
     * @return configured OpenAI advisor or the disabled advisor
     */
    public static MoveAdvisor fromEnvironment() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return MoveAdvisor.DISABLED;
        }
        return new OpenAiMoveAdvisor(apiKey, System.getenv("OPENAI_MODEL"));
    }

    @Override
    public Optional<MoveAdvice> advise(String gameState, List<MoveOption> legalMoves) {
        if (legalMoves == null || legalMoves.isEmpty()) {
            return Optional.empty();
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(endpoint)
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(createRequestBody(gameState, legalMoves)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return Optional.empty();
            }

            return parseAdvice(extractOutputText(response.body()), legalMoves.size());
        } catch (IOException | InterruptedException | RuntimeException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return Optional.empty();
        }
    }

    private String createRequestBody(String gameState, List<MoveOption> legalMoves) throws IOException {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", model);
        body.put("input", createPrompt(gameState, legalMoves));
        body.put("max_output_tokens", 80);
        body.put("store", false);
        body.putObject("reasoning").put("effort", "none");
        return objectMapper.writeValueAsString(body);
    }

    private String createPrompt(String gameState, List<MoveOption> legalMoves) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are choosing one move for a Catan computer player. ")
                .append("Every listed move is already legal. Choose only one listed MOVE_ID.\n")
                .append("Player state: ").append(gameState).append("\nLegal moves:\n");

        for (MoveOption move : legalMoves) {
            prompt.append("MOVE_ID ").append(move.id())
                    .append(" | rule score ").append(move.score())
                    .append(" | ").append(move.description()).append("\n");
        }

        prompt.append("Return exactly two lines:\nMOVE_ID: <number>\nREASON: <one short sentence>");
        return prompt.toString();
    }

    private String extractOutputText(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode output = root.path("output");
        if (!output.isArray()) {
            return "";
        }

        for (JsonNode item : output) {
            JsonNode content = item.path("content");
            if (!content.isArray()) {
                continue;
            }
            for (JsonNode part : content) {
                if ("output_text".equals(part.path("type").asText()) && part.has("text")) {
                    return part.path("text").asText();
                }
            }
        }
        return "";
    }

    static Optional<MoveAdvice> parseAdvice(String outputText, int moveCount) {
        if (outputText == null || outputText.isBlank() || moveCount <= 0) {
            return Optional.empty();
        }

        Matcher moveMatcher = MOVE_PATTERN.matcher(outputText);
        if (!moveMatcher.find()) {
            return Optional.empty();
        }

        int moveId;
        try {
            moveId = Integer.parseInt(moveMatcher.group(1));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }

        if (moveId < 0 || moveId >= moveCount) {
            return Optional.empty();
        }

        Matcher reasonMatcher = REASON_PATTERN.matcher(outputText);
        String reason = reasonMatcher.find() ? reasonMatcher.group(1).trim() : "Selected by the LLM advisor.";
        return Optional.of(new MoveAdvice(moveId, reason));
    }
}
