package com.epfafrica.etatcivil.dto;

import jakarta.validation.constraints.NotNull;

public record TraitementRequest(
        @NotNull Decision decision,
        String motif // obligatoire si decision = REJETEE, vérifié dans le service (RG-05)
) {
    public enum Decision { VALIDEE, REJETEE }
}
