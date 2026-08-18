package com.epfafrica.etatcivil.domain.model;

import com.epfafrica.etatcivil.domain.enums.StatutDemande;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "demandes_actes")
@Getter
@Setter
@NoArgsConstructor
public class DemandeActe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "citoyen_id")
    private Utilisateur citoyen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_acte_id")
    private TypeActe typeActe;

    @Column(columnDefinition = "TEXT")
    private String infosDemandeur; // JSON: nom, date/lieu de naissance, parents, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemande statut = StatutDemande.DEPOSEE;

    private String motifRejet; // obligatoire si statut = REJETEE (RG-05)

    @Column(nullable = false)
    private LocalDateTime dateDepot = LocalDateTime.now();

    @OneToMany(mappedBy = "demandeActe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PieceJustificative> pieces = new ArrayList<>();

    @OneToOne(mappedBy = "demandeActe", cascade = CascadeType.ALL)
    private ActeDelivre acteDelivre; // non-null seulement si statut = VALIDEE (RG-03, RG-06)

    // -- Comportement métier minimal, à enrichir en application/service --

    public void valider() {
        this.statut = StatutDemande.VALIDEE;
    }

    public void rejeter(String motif) {
        if (motif == null || motif.isBlank()) {
            throw new IllegalArgumentException("Le motif de rejet est obligatoire (RG-05).");
        }
        this.statut = StatutDemande.REJETEE;
        this.motifRejet = motif;
    }
}
