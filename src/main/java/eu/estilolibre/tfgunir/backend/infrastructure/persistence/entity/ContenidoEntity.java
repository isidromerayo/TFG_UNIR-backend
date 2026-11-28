package eu.estilolibre.tfgunir.backend.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad JPA que representa un contenido en la base de datos.
 * 
 * <p>Esta entidad maneja la persistencia de contenidos y sus relaciones
 * con otras entidades. Contiene anotaciones JPA y campos técnicos
 * necesarios para la persistencia.</p>
 * 
 * <p>No contiene lógica de negocio - esa responsabilidad pertenece al
 * modelo de dominio {@link eu.estilolibre.tfgunir.backend.domain.modelo.Contenido}.</p>
 * 
 * <p>Nota: Temporalmente referencia al modelo antiguo Curso hasta que
 * la migración de Curso esté completa.</p>
 */
@Data
@Entity
@Table(name = "contenidos")
public class ContenidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String documento;
    
    private int orden;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id")
    private eu.estilolibre.tfgunir.backend.model.Curso curso;
}
