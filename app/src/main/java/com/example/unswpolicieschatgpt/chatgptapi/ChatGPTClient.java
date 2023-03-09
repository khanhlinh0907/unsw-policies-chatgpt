package com.example.unswpolicieschatgpt.chatgptapi;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatGPTClient {

    private final OpenAiService openAiService;

    public ChatGPTClient(String apiKey) {
        openAiService = new OpenAiService(apiKey);
    }

    public void sendPrompt(String prompt) throws ExecutionException, InterruptedException {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("ada")
                .echo(true)
                .build();

        openAiService.createCompletion(completionRequest).getChoices().forEach(System.out::println);

    }


}
