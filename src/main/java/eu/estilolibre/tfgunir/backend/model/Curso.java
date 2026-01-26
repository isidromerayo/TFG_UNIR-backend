package eu.estilolibre.tfgunir.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 */
@Data
@Entity
@Table(name = "cursos")
@lombok.EqualsAndHashCode(exclude = {"alumnos", "contenidos", "avances"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String titulo;
    private String descripcion;
    @Column(name = "estado", length = 1)
    private String estado = "A";
    @Column(name = "valoracion_media")
    private double valoracionMedia;
    @Column(precision = 8, scale = 2)
    private BigDecimal precio;
    private String etiquetas;
    @Column(name="fecha_creacion")
    private LocalDate fechaCreacion;
    @Column(name="fecha_actualizacion")
    private LocalDate fechaActualizacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    private Categoria categoria;
    @ManyToMany(mappedBy = "misCursosComprados")
    private Set<Usuario> alumnos;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", referencedColumnName = "id")
    private Instructor instructor;
    @OneToMany(
        mappedBy = "curso",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<Contenido> contenidos = new HashSet<>();
    @OneToMany(mappedBy = "curso")
    Set<Avance> avances;

    @PrePersist
    protected void onCreate() {
        fechaActualizacion = fechaCreacion = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDate.now();
    }
}
