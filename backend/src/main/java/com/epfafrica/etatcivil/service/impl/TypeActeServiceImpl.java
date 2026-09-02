package com.epfafrica.etatcivil.service.impl;

import com.epfafrica.etatcivil.dto.TypeActeDTO;
import com.epfafrica.etatcivil.mapper.TypeActeMapper;
import com.epfafrica.etatcivil.repository.TypeActeRepository;
import com.epfafrica.etatcivil.service.TypeActeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeActeServiceImpl implements TypeActeService {

    private final TypeActeRepository typeActeRepository;
    private final TypeActeMapper typeActeMapper;

    public TypeActeServiceImpl(TypeActeRepository typeActeRepository, TypeActeMapper typeActeMapper) {
        this.typeActeRepository = typeActeRepository;
        this.typeActeMapper = typeActeMapper;
    }

    @Override
    public List<TypeActeDTO> lister() {
        return typeActeRepository.findAll().stream()
                .map(typeActeMapper::toDTO)
                .toList();
    }
}
