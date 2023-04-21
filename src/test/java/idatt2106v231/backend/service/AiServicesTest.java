package idatt2106v231.backend.service;

import idatt2106v231.backend.BackendApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class)
public class AiServicesTest {

    @Autowired
    AiServices aiServices;

    @Test
    @DisplayName("OpenAI chat completion works")
    public void openAiChatCompletionWorks() {
        assertEquals("Hello, World!", aiServices.getChatCompletion("Write 'Hello, World!'"));
    }
}
