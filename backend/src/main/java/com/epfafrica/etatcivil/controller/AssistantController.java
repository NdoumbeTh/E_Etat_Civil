package com.epfafrica.etatcivil.controller;

import com.epfafrica.etatcivil.dto.AssistantRequest;
import com.epfafrica.etatcivil.dto.AssistantResponse;
import com.epfafrica.etatcivil.service.AssistantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping
    public AssistantResponse poserQuestion(@Valid @RequestBody AssistantRequest request) {
        String reponse = assistantService.repondre(request.message());
        return new AssistantResponse(reponse);
    }
}