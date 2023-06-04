package eu.estilolibre.tfgunir.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "avances")
public class Avance {
   

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario estudiante;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    private int tema;
    
}
