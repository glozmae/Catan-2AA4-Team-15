package Player;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.ContentBlock;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;

/**
 * Optional Claude advisor built with Anthropic's official Java SDK.
 * The original rule-based AI is used whenever Claude is not configured or fails.
 */
public final class ClaudeMoveAdvisor implements MoveAdvisor {
    private static final String DEFAULT_MODEL = "claude-haiku-4-5";
    private static final Pattern MOVE_PATTERN = Pattern.compile("(?i)MOVE_ID\\s*:\\s*(\\d+)");
    private static final Pattern REASON_PATTERN = Pattern.compile("(?i)REASON\\s*:\\s*(.+)");

    private final AnthropicClient client;
    private final String model;

    /**
     * Creates a Claude advisor using an Anthropic API key.
     *
     * @param apiKey Anthropic API key
     * @param model Claude model id, or blank to use Claude Haiku 4.5
     */
    public ClaudeMoveAdvisor(String apiKey, String model) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("An Anthropic API key is required");
        }
        this.client = AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .timeout(Duration.ofSeconds(10))
                .maxRetries(0)
                .build();
        this.model = model == null || model.isBlank() ? DEFAULT_MODEL : model;
    }

    ClaudeMoveAdvisor(AnthropicClient client, String model) {
        this.client = client;
        this.model = model == null || model.isBlank() ? DEFAULT_MODEL : model;
    }

    /**
     * Creates the optional advisor from environment variables.
     *
     * @return Claude advisor, or the disabled advisor when no key is set
     */
    public static MoveAdvisor fromEnvironment() {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return MoveAdvisor.DISABLED;
        }
        return new ClaudeMoveAdvisor(apiKey, System.getenv("ANTHROPIC_MODEL"));
    }

    @Override
    public Optional<MoveAdvice> advise(String gameState, List<MoveOption> legalMoves) {
        if (legalMoves == null || legalMoves.isEmpty()) {
            return Optional.empty();
        }

        try {
            MessageCreateParams params = MessageCreateParams.builder()
                    .model(model)
                    .maxTokens(80)
                    .addUserMessage(createPrompt(gameState, legalMoves))
                    .build();
            Message response = client.messages().create(params);
            return parseAdvice(extractText(response), legalMoves.size());
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

    private String createPrompt(String gameState, List<MoveOption> legalMoves) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Choose one legal move for a Catan computer player. ")
                .append("Choose only a listed MOVE_ID.\n")
                .append("Player state: ").append(gameState).append("\nLegal moves:\n");

        for (MoveOption move : legalMoves) {
            prompt.append("MOVE_ID ").append(move.id())
                    .append(" | rule score ").append(move.score())
                    .append(" | ").append(move.description()).append("\n");
        }

        prompt.append("Return exactly two lines:\nMOVE_ID: <number>\nREASON: <one short sentence>");
        return prompt.toString();
    }

    private String extractText(Message response) {
        return response.content().stream()
                .filter(ContentBlock::isText)
                .map(block -> block.asText().text())
                .collect(Collectors.joining("\n"));
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
        String reason = reasonMatcher.find() ? reasonMatcher.group(1).trim() : "Selected by Claude.";
        return Optional.of(new MoveAdvice(moveId, reason));
    }
}
