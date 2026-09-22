package com.epfafrica.etatcivil.service.impl;

import com.epfafrica.etatcivil.service.AssistantService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AssistantServiceImpl implements AssistantService {

    private static final String PROMPT_SYSTEME = """
            Tu es l'assistant virtuel de e-ÉtatCivil, une plateforme sénégalaise de demandes
            d'actes d'état civil (naissance, mariage, décès). Tu aides les citoyens à comprendre :
            - quelles pièces justificatives sont nécessaires pour chaque type d'acte,
            - comment se déroule le traitement d'une demande (dépôt, instruction, décision),
            - les délais habituels et le fonctionnement général du service.
            Réponds toujours en français, de façon claire et concise (quelques phrases maximum).
            Si une question sort du cadre de l'état civil, précise poliment que tu ne peux
            aider que sur les démarches d'état civil.
            """;

    private final ChatClient chatClient;

    public AssistantServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(PROMPT_SYSTEME)
                .build();
    }

    @Override
    public String repondre(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}