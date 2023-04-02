package com.example.unswpolicieschatgpt.chatgptapi;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;

import java.util.Collections;
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


    public List<Embedding> createEmbeddings(List<String> paragraphs) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                .model("text-embedding-ada-002")
                .input(paragraphs)
                .build();

        List<Embedding> embeddings = openAiService.createEmbeddings(embeddingRequest).getData();

        return embeddings;

    }


    public Embedding embedQuery(String query) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                .model("text-embedding-ada-002")
                .input(Collections.singletonList(query))
                .build();

        List<Embedding> embeddedQuery = openAiService.createEmbeddings(embeddingRequest).getData();

        return embeddedQuery.get(0);
    }

}
