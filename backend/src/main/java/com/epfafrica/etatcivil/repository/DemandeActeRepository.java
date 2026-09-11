package com.epfafrica.etatcivil.repository;

import com.epfafrica.etatcivil.model.DemandeActe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeActeRepository extends JpaRepository<DemandeActe, Long> {

    Page<DemandeActe> findByCitoyenId(Long citoyenId, Pageable pageable);
}