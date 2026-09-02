package com.epfafrica.etatcivil.repository;

import com.epfafrica.etatcivil.model.DemandeActe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeActeRepository extends JpaRepository<DemandeActe, Long> {
    List<DemandeActe> findByCitoyenId(Long citoyenId);
}
