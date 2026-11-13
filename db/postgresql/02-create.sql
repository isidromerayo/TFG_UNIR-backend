-- Tabla: cursos
DROP TABLE IF EXISTS cursos;
CREATE TABLE cursos (
  id BIGSERIAL PRIMARY KEY,
  descripcion VARCHAR(255) DEFAULT NULL,
  estado CHAR(1) DEFAULT NULL,
  etiquetas VARCHAR(255) DEFAULT NULL,
  fecha_actualizacion DATE DEFAULT NULL,
  fecha_creacion DATE DEFAULT NULL,
  precio DECIMAL(8, 2) DEFAULT NULL,
  titulo VARCHAR(255) DEFAULT NULL,
  valoracion_media DOUBLE PRECISION DEFAULT NULL,
  categoria_id BIGINT DEFAULT NULL,
  instructor_id BIGINT DEFAULT NULL,
  CONSTRAINT fk_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id),
  CONSTRAINT fk_instructor FOREIGN KEY (instructor_id) REFERENCES instructores(id)
);

-- Inserta datos en cursos
INSERT INTO cursos (id, descripcion, estado, etiquetas, fecha_actualizacion, fecha_creacion, precio, titulo, valoracion_media, categoria_id, instructor_id) VALUES
(1, 'lorem ipsum chanante', 'A', NULL, '2023-05-15', '2023-01-01', 9.99, 'Angular', 4.6, 5, 1),
(2, 'Incididunt zagal traeros tol jamón chachachá ', 'A', NULL, '2023-05-15', '2023-01-01', 9.99, 'React', 4.7, 5, 1),
(3, 'Gracias a este curso aprenderás desde a crear tus propias aplicaciones web  desde cero usando Vue.js 3, Strapi, Firebase, etc...\n\nAprenderás a crear aplicaciones web donde el cliente está desacoplado del servidor y a desplegarlos.', 'A', NULL, '2023-06-18', '2023-01-01', 9.99, 'Vue.js', 4.8, 5, 1),
(4, 'Chanante ipsum dolor sit amet, traeros tol jamón forrondo', 'A', NULL, '2023-06-18', '2023-03-01', 9.99, 'Principios de Acústica para el Home Studio', 4.81, 1, 2),
(5, 'Bufonesco elit eres un pirámidee fresquete payacho ', 'A', NULL, '2023-06-18', '2023-03-01', 12.99, 'Home Studio intermedio', 4.5, 1, 2),
(6, 'Regomello elit dolore droja hueles avinagrado cacahué labore', 'A', NULL, '2023-06-18', '2023-03-01', 15.99, 'Masterizacion de Audio: La guía completa de cómo masterizar', 4.6, 1, 2),
(7, 'Corporis voluptates libero labore laudantium quo ab consequuntur molestiae ipsa. Et ab fugit recusandae rerum ullam distinctio at aut voluptatem. Quo est vero vel corrupti.', 'A', 'bluetooth, Cape Verde Escudo, Tasty Rubber Soap', '2023-06-11', '2023-06-01', 20.95, 'grey Small Steel Hat', 0, 9, 3),
(8, 'Soluta aut sit. A mollitia magni incidunt. Distinctio facilis in quis nulla. Reprehenderit et assumenda. Ut aspernatur quia assumenda aliquid omnis.', 'A', '', '2023-06-11', '2023-06-11', 163.62, 'Home Loan Account', 0, 9, 3),
(9, 'Delectus ab et magnam. Perspiciatis nisi distinctio similique cupiditate. Exercitationem sed ut voluptas harum vero ea odio. Eligendi inventore possimus. Et doloribus non omnis expedita minima placeat corrupti.', 'A', '', '2023-06-11', '2023-06-11', 45.00, 'Investment Account', 0, 10, 3),
(10, 'Iusto et iusto culpa ipsam. Voluptatem distinctio fugiat eos debitis necessitatibus adipisci voluptatem. Odio esse iste quas aut placeat ut est. Minus labore saepe eum accusantium ut praesentium voluptas.', 'A', '', '2023-06-11', '2023-06-11', 15.75, 'Checking Account', 0, 10, 3),
(11, 'Et qui fugiat sed voluptatem dolorum ea sunt impedit. Id rerum doloribus rem accusamus odit nihil itaque enim. Beatae esse aspernatur non illo dicta velit eum. Ipsam dolorem iste.', 'A', '', '2023-06-21', '2023-06-21', 178.20, 'Personal Loan Account', 0, 11, 3),
(12, 'No es necesario que tengas conocimientos de fotografía, se parte desde cero y se enseña a cada alumno el manejo de su propia cámara en modo manual (no es necesario que te leas el libro de instrucciones). ', 'A', 'foto', '2023-07-25', '2023-07-25', 60.00, 'Curso de iniciación a la fotografía', 4.88, 2, 4),
(13, 'Está orientada a que aprendas a desenvolverte en la fotografía de paisaje por lo que se irá dando nociones y consejos a cada uno durante toda la jornada sobre filtros y composición.', 'A', 'foto', '2023-07-25', '2023-07-25', 80.00, 'Curso fotografía de paisaje', 4.12, 2, 4),
(14, 'Perferendis qui esse deserunt nemo dolor. Deserunt nihil quisquam numquam. Illo aspernatur officiis autem velit sed rerum sed velit velit.', 'A', 'haptic, Pula, Tasty Granite Car', '2023-07-30', '2023-07-30', 31.50, 'lime Practical Rubber Pants', 0, NULL, NULL),
(15, 'Non dolores debitis quam voluptate sint. Delectus possimus error esse dolorem. Doloremque ab ut commodi ipsa.', 'A', '1080p, Kip, Sleek Plastic Tuna', '2023-07-30', '2023-07-30', 7.69, 'azure Awesome Rubber Chicken', 0, NULL, NULL);
