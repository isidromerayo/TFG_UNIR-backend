package eu.estilolibre.tfgunir.backend.infrastructure.persistence;

import eu.estilolibre.tfgunir.backend.application.port.out.ContenidoPort;
import eu.estilolibre.tfgunir.backend.domain.modelo.Contenido;
import eu.estilolibre.tfgunir.backend.infrastructure.persistence.entity.ContenidoEntity;
import eu.estilolibre.tfgunir.backend.infrastructure.persistence.mapper.ContenidoMapper;
import eu.estilolibre.tfgunir.backend.infrastructure.persistence.repository.ContenidoJpaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA para el puerto de contenidos.
 * 
 * <p>Este adapter implementa el puerto de aplicación {@link ContenidoPort}
 * usando Spring Data JPA. Convierte entre modelos de dominio y entidades JPA.</p>
 * 
 * <p>Este es un adapter de salida (driven adapter) que permite a la capa
 * de aplicación acceder a la base de datos sin depender de JPA o Spring.</p>
 */
@Component
@RequiredArgsConstructor
public class ContenidoJpaAdapter implements ContenidoPort {

    private final ContenidoJpaRepository jpaRepository;
    private final ContenidoMapper mapper;

    @Override
    public Optional<Contenido> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Contenido> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Contenido> findByCursoId(Long cursoId) {
        return jpaRepository.findByCursoId(cursoId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Contenido save(Contenido contenido) {
        ContenidoEntity entity = mapper.toEntity(contenido);
        ContenidoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
