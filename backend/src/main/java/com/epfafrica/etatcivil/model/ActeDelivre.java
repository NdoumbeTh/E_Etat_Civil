package com.epfafrica.etatcivil.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "actes_delivres")
@Getter
@Setter
@NoArgsConstructor
public class ActeDelivre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "demande_id", unique = true)
    private DemandeActe demandeActe;

    @Column(nullable = false, unique = true)
    private String numeroUnique; // RG-03 : unique et non réutilisable

    @Column(nullable = false)
    private LocalDateTime dateDelivrance = LocalDateTime.now();

    private String cheminFichierPdf;
}
