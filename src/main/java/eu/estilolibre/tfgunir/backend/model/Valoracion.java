package eu.estilolibre.tfgunir.backend.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "valoraciones")
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario estudiante;
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;
    private int puntuacion;
    @Column(columnDefinition = "TEXT")
    private String comentario;
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = new Date();
        }
    }

}
