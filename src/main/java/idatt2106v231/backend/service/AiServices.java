package idatt2106v231.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.theokanning.openai.service.OpenAiService.*;

@Service
public class AiServices {

    private static final Logger _logger =
            LoggerFactory.getLogger(UserServices.class);

    /**
     * Gets a chat completion using OpenAI GPT-3
     * @param content the content of the query
     * @return the answer produced by the AI
     */
    public String getChatCompletion(String content) {
        try {

            String token = getOpenAiApiKey();

            ObjectMapper mapper = defaultObjectMapper();

            Duration timeout = Duration.ofSeconds(30);


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
                    .temperature(0.01)
                    .build();


            return String.valueOf(service.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent());
        } catch (IllegalArgumentException e) {
            _logger.error("Failed to generate chat completion", e);
            return null;
        }
    }

    /**
     * Gets the OpenAi API key
     * @return the key
     */
    public String getOpenAiApiKey() {
        try {
            Dotenv dotenv = Dotenv.configure().load();
            String token = dotenv.get("OPENAI_TOKEN");
            if (token == null) {
                _logger.error("Token is missing. " +
                        "Make sure a valid OpenAI API key is stored in a .env file in the root of the project");
                return null;
            } else {
                System.out.println(token);
                return token;
            }
        } catch (Exception e) {
            _logger.error("Failed to get token: " + e.getMessage());
            return null;
        }
    }
}
