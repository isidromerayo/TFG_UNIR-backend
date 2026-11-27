package eu.estilolibre.tfgunir.backend.infrastructure.persistence;

import eu.estilolibre.tfgunir.backend.application.port.out.CategoriaPort;
import eu.estilolibre.tfgunir.backend.domain.modelo.Categoria;
import eu.estilolibre.tfgunir.backend.infrastructure.persistence.entity.CategoriaEntity;
import eu.estilolibre.tfgunir.backend.infrastructure.persistence.mapper.CategoriaMapper;
import eu.estilolibre.tfgunir.backend.infrastructure.persistence.repository.CategoriaJpaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA para el puerto de categorías.
 * 
 * <p>Este adapter implementa el puerto de aplicación {@link CategoriaPort}
 * usando Spring Data JPA. Convierte entre modelos de dominio y entidades JPA.</p>
 * 
 * <p>Este es un adapter de salida (driven adapter) que permite a la capa
 * de aplicación acceder a la base de datos sin depender de JPA o Spring.</p>
 */
@Component
@RequiredArgsConstructor
public class CategoriaJpaAdapter implements CategoriaPort {

    private final CategoriaJpaRepository jpaRepository;
    private final CategoriaMapper mapper;

    @Override
    public Optional<Categoria> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Categoria> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Categoria save(Categoria categoria) {
        CategoriaEntity entity = mapper.toEntity(categoria);
        CategoriaEntity saved = jpaRepository.save(entity);
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
