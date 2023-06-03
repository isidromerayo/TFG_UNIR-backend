package eu.estilolibre.tfgunir.backend.model;

import java.util.ArrayList;
import java.util.List;

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
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    @Column(name = "estado", length = 1, columnDefinition = "char(1) default 'P'")
    private String estado;
    @ManyToMany(cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    @JoinTable(name="usuarios_cursos",
    joinColumns = @JoinColumn(name="usuario_id"),
    inverseJoinColumns = @JoinColumn(name="curso_id"))
    private List<Curso> misCursos = new ArrayList<Curso>();

    /**
     * 
     * @param curso
     */
    public void addCurso(Curso curso) {
        misCursos.add(curso);
        curso.getAlumnos().add(this);
    }
}
