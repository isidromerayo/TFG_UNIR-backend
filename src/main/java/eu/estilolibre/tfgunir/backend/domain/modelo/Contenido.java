package eu.estilolibre.tfgunir.backend.domain.modelo;

import java.util.Objects;

/**
 * Modelo de dominio que representa un contenido de curso.
 * 
 * <p>Este es un POJO puro que contiene la lógica de negocio relacionada
 * con contenidos. No depende de JPA, Spring u otros frameworks.</p>
 * 
 * <p>El título y el orden son obligatorios. El documento es opcional.</p>
 */
public class Contenido {

    private Long id;
    private String titulo;
    private String documento;
    private int orden;

    /**
     * Crea un nuevo contenido con todos los campos.
     *
     * @param id        identificador único (puede ser null para nuevos contenidos)
     * @param titulo    título del contenido (obligatorio, no nulo, no vacío)
     * @param documento contenido detallado (opcional)
     * @param orden     posición en el orden del curso (obligatorio, debe ser > 0)
     * @throws IllegalArgumentException si el título es nulo, vacío o el orden no es positivo
     */
    public Contenido(Long id, String titulo, String documento, int orden) {
        this.id = id;
        setTitulo(titulo);
        this.documento = documento;
        setOrden(orden);
    }

    /**
     * Crea un nuevo contenido sin identificador.
     *
     * @param titulo    título del contenido (obligatorio, no nulo, no vacío)
     * @param documento contenido detallado (opcional)
     * @param orden     posición en el orden del curso (obligatorio, debe ser > 0)
     * @throws IllegalArgumentException si el título es nulo, vacío o el orden no es positivo
     */
    public Contenido(String titulo, String documento, int orden) {
        this(null, titulo, documento, orden);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título del contenido.
     *
     * @param titulo título del contenido (obligatorio, no nulo, no vacío)
     * @throws IllegalArgumentException si el título es nulo, vacío o solo espacios
     */
    public void setTitulo(String titulo) {
        if (titulo == null) {
            throw new IllegalArgumentException("El título del contenido no puede ser nulo");
        }
        if (titulo.isBlank()) {
            throw new IllegalArgumentException("El título del contenido no puede ser vacío o solo espacios");
        }
        this.titulo = titulo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public int getOrden() {
        return orden;
    }

    /**
     * Establece el orden del contenido.
     *
     * @param orden posición en el orden del curso (debe ser > 0)
     * @throws IllegalArgumentException si el orden no es positivo
     */
    public void setOrden(int orden) {
        if (orden <= 0) {
            throw new IllegalArgumentException("El orden del contenido debe ser un número positivo");
        }
        this.orden = orden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contenido contenido = (Contenido) o;
        return Objects.equals(id, contenido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Contenido{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", documento='" + (documento != null ? documento.substring(0, Math.min(documento.length(), 50)) + "..." : "null") + '\'' +
                ", orden=" + orden +
                '}';
    }
}
