package com.example.unswpolicieschatgpt.chatgptapi;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;

public class ChatGPTClient {



    private final OpenAiService openAiService;

    public ChatGPTClient(String apiKey) {
        openAiService = new OpenAiService(apiKey);
    }

    public String getChatGPTResponse(String prompt) throws ExecutionException, InterruptedException {
        //Set parameter for the GPT-3 API request
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("text-davinci-003")
                .echo(false)
                .maxTokens(500)
                .temperature(0.0)
                .build();

        //Get the generated response text
        String generatedText = openAiService.createCompletion(completionRequest).getChoices().get(0).getText();
        //openAiService.createCompletion(completionRequest).getChoices().forEach(System.out::println);
        return generatedText;

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
