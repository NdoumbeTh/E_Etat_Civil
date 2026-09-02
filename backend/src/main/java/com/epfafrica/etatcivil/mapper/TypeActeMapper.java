package com.epfafrica.etatcivil.mapper;

import com.epfafrica.etatcivil.dto.TypeActeDTO;
import com.epfafrica.etatcivil.model.TypeActe;
import org.springframework.stereotype.Component;

@Component
public class TypeActeMapper {

    public TypeActeDTO toDTO(TypeActe typeActe) {
        return new TypeActeDTO(typeActe.getId(), typeActe.getLibelle(), typeActe.getPiecesRequises());
    }
}
