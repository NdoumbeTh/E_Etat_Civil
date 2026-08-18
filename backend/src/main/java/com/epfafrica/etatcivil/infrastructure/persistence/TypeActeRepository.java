package com.epfafrica.etatcivil.infrastructure.persistence;

import com.epfafrica.etatcivil.domain.model.TypeActe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeActeRepository extends JpaRepository<TypeActe, Long> {
}
