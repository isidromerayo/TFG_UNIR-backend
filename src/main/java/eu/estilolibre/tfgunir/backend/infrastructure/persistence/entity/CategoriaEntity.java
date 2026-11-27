package eu.estilolibre.tfgunir.backend.infrastructure.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad JPA que representa una categoría en la base de datos.
 * 
 * <p>Esta entidad maneja la persistencia de categorías y sus relaciones
 * con otras entidades. Contiene anotaciones JPA y campos técnicos
 * necesarios para la persistencia.</p>
 * 
 * <p>No contiene lógica de negocio - esa responsabilidad pertenece al
 * modelo de dominio {@link eu.estilolibre.tfgunir.backend.domain.modelo.Categoria}.</p>
 */
@Data
@Entity
@Table(name = "categorias")
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String descripcion;
    
    @OneToMany(mappedBy = "categoria")
    private List<eu.estilolibre.tfgunir.backend.model.Curso> cursos = new ArrayList<>();
}
