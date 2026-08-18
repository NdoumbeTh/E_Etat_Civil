package com.epfafrica.etatcivil.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pieces_justificatives")
@Getter
@Setter
@NoArgsConstructor
public class PieceJustificative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "demande_id")
    private DemandeActe demandeActe;

    @Column(nullable = false)
    private String nomFichier;

    @Column(nullable = false)
    private String url; // chemin de stockage local ou objet
}
