package eu.estilolibre.tfgunir.backend.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Data
@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String titulo;
    private String descripcion;
    @Column(name = "estado", length = 1)
    private String estado = "A";
    @Column(name = "valoracion_media", length = 1)
    private double valoracionMedia;
    @Column(precision = 8, scale = 2)
    private BigDecimal precio;
    private String etiquetas;
    @Column(name="fecha_creacion")
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;
    @Column(name="fecha_actualizacion")
    @Temporal(TemporalType.DATE)
    private Date fechaActualizacion;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    private Categoria categoria;
    @ManyToMany(mappedBy = "misCursosComprados")
    private Set<Usuario> alumnos;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", referencedColumnName = "id")
    private Instructor instructor;
    @OneToMany(
        mappedBy = "curso",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Contenido> contenidos = new ArrayList<Contenido>();
    @OneToMany(mappedBy = "curso")
    Set<Avance> avances;

    @PrePersist
    protected void onCreate() {
        fechaActualizacion = fechaCreacion = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = new Date();
    }
}
