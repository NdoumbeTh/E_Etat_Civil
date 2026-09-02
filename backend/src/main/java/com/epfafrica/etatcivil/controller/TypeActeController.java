package com.epfafrica.etatcivil.controller;

import com.epfafrica.etatcivil.dto.TypeActeDTO;
import com.epfafrica.etatcivil.service.TypeActeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/types-actes")
public class TypeActeController {

    private final TypeActeService typeActeService;

    public TypeActeController(TypeActeService typeActeService) {
        this.typeActeService = typeActeService;
    }

    @GetMapping
    public List<TypeActeDTO> lister() {
        return typeActeService.lister();
    }
}
