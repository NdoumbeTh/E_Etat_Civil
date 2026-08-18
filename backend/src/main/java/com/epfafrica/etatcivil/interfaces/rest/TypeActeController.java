package com.epfafrica.etatcivil.interfaces.rest;

import com.epfafrica.etatcivil.infrastructure.persistence.TypeActeRepository;
import com.epfafrica.etatcivil.interfaces.dto.TypeActeDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/types-actes")
public class TypeActeController {

    private final TypeActeRepository typeActeRepository;

    public TypeActeController(TypeActeRepository typeActeRepository) {
        this.typeActeRepository = typeActeRepository;
    }

    @GetMapping
    public List<TypeActeDTO> lister() {
        return typeActeRepository.findAll().stream()
                .map(t -> new TypeActeDTO(t.getId(), t.getLibelle(), t.getPiecesRequises()))
                .toList();
    }
}