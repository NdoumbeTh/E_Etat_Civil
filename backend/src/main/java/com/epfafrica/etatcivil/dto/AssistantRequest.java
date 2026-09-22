package com.epfafrica.etatcivil.dto;

import jakarta.validation.constraints.NotBlank;

public record AssistantRequest(@NotBlank String message) {
}