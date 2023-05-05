package idatt2106v231.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import idatt2106v231.backend.model.OpenAiKey;
import idatt2106v231.backend.repository.OpenAiKeyRepository;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.theokanning.openai.service.OpenAiService.*;

/**
 * Class to manage Ai.
 */
@Service
public class AiServices {

    private final OpenAiKeyRepository openAiKeyRepo;

    /**
     * Constructor which sets the Open AI key repository.
     */
    @Autowired
    public AiServices(OpenAiKeyRepository openAiKeyRepo) {
        this.openAiKeyRepo = openAiKeyRepo;
    }

    /**
     * Gets a chat completion using OpenAI GPT-3.
     *
     * @param content the content of the query
     * @return the answer produced by the AI
     */
    public String getChatCompletion(String content) {
        try {

            String token = getOpenAiApiKey();
            if (token.startsWith("ERROR :")) throw new Exception(token);

            ObjectMapper mapper = defaultObjectMapper();

            Duration timeout = Duration.ofSeconds(300);


            OkHttpClient client = defaultClient(token, timeout)
                    .newBuilder()
                    .build();

            Retrofit retrofit = defaultRetrofit(client, mapper);
            OpenAiApi api = retrofit.create(OpenAiApi.class);
            OpenAiService service = new OpenAiService(api);

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("user", content));


            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .messages(messages)
                    .model("gpt-3.5-turbo")
                    .temperature(0.0)
                    .build();


            return String.valueOf(service.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent());
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Gets the OpenAi API key.
     * This must either be stored in the table 'open_ai_key' in the database,
     * or in a .env file in the root of the project folder as OPENAI_TOKEN=your_token.
     *
     * @return the key
     */
    public String getOpenAiApiKey() {
        try {
            String token = null;
            Optional<OpenAiKey> openAiKey = openAiKeyRepo.findFirstByOrderByIdDesc();
            if (openAiKey.isPresent()) token = openAiKey.get().getApiKey();

            if (token == null) {
                Dotenv dotenv = Dotenv.configure().load();
                token = dotenv.get("OPENAI_TOKEN");

                if (token == null) {
                    return "Token is missing. " +
                            "Make sure a valid OpenAI API key is stored in the database " +
                            "or in a .env file in the root of the project";
                }
            }
            return token;
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}