package eu.estilolibre.tfgunir.backend.domain.modelo;

import java.util.Objects;

/**
 * Modelo de dominio que representa una categoría de cursos.
 * 
 * <p>Este es un POJO puro que contiene la lógica de negocio relacionada
 * con categorías. No depende de JPA, Spring u otros frameworks.</p>
 * 
 * <p>El nombre es obligatorio y debe ser no nulo, no vacío y no solo espacios.
 * La descripción es opcional.</p>
 */
public class Categoria {

    private Long id;
    private String nombre;
    private String descripcion;

    /**
     * Crea una nueva categoría con todos los campos.
     *
     * @param id          identificador único (puede ser null para nuevas categorías)
     * @param nombre      nombre de la categoría (obligatorio, no nulo, no vacío)
     * @param descripcion descripción de la categoría (opcional)
     * @throws IllegalArgumentException si el nombre es nulo, vacío o solo espacios
     */
    public Categoria(Long id, String nombre, String descripcion) {
        this.id = id;
        setNombre(nombre);
        this.descripcion = descripcion;
    }

    /**
     * Crea una nueva categoría sin identificador.
     *
     * @param nombre      nombre de la categoría (obligatorio, no nulo, no vacío)
     * @param descripcion descripción de la categoría (opcional)
     * @throws IllegalArgumentException si el nombre es nulo, vacío o solo espacios
     */
    public Categoria(String nombre, String descripcion) {
        this(null, nombre, descripcion);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la categoría.
     *
     * @param nombre nombre de la categoría (obligatorio, no nulo, no vacío)
     * @throws IllegalArgumentException si el nombre es nulo, vacío o solo espacios
     */
    public void setNombre(String nombre) {
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre de la categoría no puede ser nulo");
        }
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede ser vacío o solo espacios");
        }
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
