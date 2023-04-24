package idatt2106v231.backend.service;

import idatt2106v231.backend.BackendApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class)
@Disabled("These tests require a private key and should not run in the pipeline")
public class AiServicesTest {

    @Autowired
    AiServices aiServices;

    @Test
    @DisplayName("OpenAI chat completion gives expected result")
    public void openAiChatCompletionGivesExpectedResult() {
        assertEquals("Hello, World!", aiServices.getChatCompletion("Say 'Hello, World!'"));
    }

    @Test
    @DisplayName("OpenAI chat completion gives different result")
    public void openAiChatCompletionGivesUnexpectedResult() {
        assertNotEquals("Hello, World!", aiServices.getChatCompletion("Say 'World, Hello!'"));
    }
}