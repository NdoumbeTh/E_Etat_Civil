package com.epfafrica.etatcivil.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "types_actes")
@Getter
@Setter
@NoArgsConstructor
public class TypeActe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String libelle; // ex: NAISSANCE, MARIAGE, DECES

    @Column(nullable = false)
    private String piecesRequises; // liste simplifiée, ex: CSV ou JSON
}
