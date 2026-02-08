package eu.estilolibre.tfgunir.backend.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
@lombok.EqualsAndHashCode(exclude = {"misCursosComprados", "avances"})
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private String apellidos;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "estado", length = 1)
    private String estado;
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "usuarios_cursos", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "curso_id"))
    private Set<Curso> misCursosComprados = new HashSet<>();
    @OneToMany(mappedBy = "estudiante")
    Set<Avance> avances;

    /**
     * 
     * @param curso
     */
    public void addCurso(Curso curso) {
        misCursosComprados.add(curso);
        curso.getAlumnos().add(this);
    }

    @PrePersist
    void prePersist() {
        if (this.estado == null)
            this.estado = "P";
    }
}
