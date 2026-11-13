-- Tabla: contenidos
DROP TABLE IF EXISTS contenidos;
CREATE TABLE contenidos (
  id BIGSERIAL PRIMARY KEY,
  documento TEXT DEFAULT NULL,
  orden INTEGER NOT NULL,
  titulo VARCHAR(255) DEFAULT NULL,
  curso_id BIGINT DEFAULT NULL,
  CONSTRAINT fk_curso_contenidos FOREIGN KEY (curso_id) REFERENCES cursos(id)
);

-- Inserta datos en contenidos
INSERT INTO contenidos (id, documento, orden, titulo, curso_id) VALUES
(1, 'Dolore piticli exercitation horcate bajonaaa artista. To sueltecico bocachoti cobete hueles avinagrado churretoso fresquete et cabeza de viejo cuerpo de joven enjuto mojamuto nui veniam elit ex páharo.', 1, 'a título 1', 1),
(2, 'Viejuno bonico del tó regomello interneeeer droja one more time gañán ut atiendee elit pepinoninoni chotera. Bufonesco gambitero muchachada cosica cascoporro ojete calor síberet melifluo eveniet, aliqua. ', 2, 'a título 2', 1),
(3, 'Asobinao, eiusmod pero qué pelazo tontaco eiusmod. Chachachá cartoniano et traeros tol jamón eres un pirámidee. Tontiploster incididunt mangurrián monetes tunante zanguango quis gaticos saepe.', 3, 'a título 3', 1),
(4, 'Eiusmod asobinao páharo no te digo ná y te lo digo tó ju-já chachachá mangurrián. ', 1, 'b.1 título 1', 2),
(5, 'Enratonao artista hueles avinagrado eveniet cabeza de viejo cuerpo de joven nisi cobete tollina bajonaaa regomello. Chavalada bufonesco nianoniano sed gambitero.', 2, 'b.2 título 2', 2),
(6, 'dummy 3', 1, '1 título 3', 3),
(7, 'Chotera piticli tempor bizcoché viejuno vivo con tu madre en un castillo forrondosco coconut zagal. Estoy fatal de lo mío ojete moreno tunante bonico del tó fresquete nui freshquisimo elit atiendee one more time enim.', 1, 'c.1 título 1', 4),
(8, 'To sueltecico ahí va qué chorrazo churretoso es de traca, ayy qué gustico pero qué pelazo labore ex minim monguer Guaper con las rodillas in the guanter, tempor tontaco zanguango. ', 2, 'c.2 título 2', 4),
(9, ' Soooy crossoverr mamellas cacahué dolore muchachada enjuto mojamuto te viste de torero melifluo enim asquerosito nuiiiii droja pataliebre consectetur bocachoti.', 3, 'c.3 título 3', 4),
(10, 'dummy 5', 1, '1 título 5', 5),
(11, 'dummy 5', 1, '1 título 5', 6);

-- Tabla: avances
DROP TABLE IF EXISTS avances;
CREATE TABLE avances (
  id BIGSERIAL PRIMARY KEY,
  tema INTEGER NOT NULL,
  curso_id BIGINT DEFAULT NULL,
  usuario_id BIGINT DEFAULT NULL,
  CONSTRAINT fk_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
  CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Inserta datos en avances
INSERT INTO avances (id, tema, curso_id, usuario_id) VALUES
(1, 1, 4, 1),
(2, 2, 1, 2);

-- Tabla: usuarios_cursos
DROP TABLE IF EXISTS usuarios_cursos;
CREATE TABLE usuarios_cursos (
  usuario_id BIGINT NOT NULL,
  curso_id BIGINT NOT NULL,
  PRIMARY KEY (usuario_id, curso_id),
  CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  CONSTRAINT fk_curso_usuarios FOREIGN KEY (curso_id) REFERENCES cursos(id)
);

-- Inserta datos en usuarios_cursos
INSERT INTO usuarios_cursos (usuario_id, curso_id) VALUES
(1, 4), (1, 5), (2, 1), (2, 2), (2, 3), (3, 1), (3, 2), (3, 3), (4, 4), (5, 4), (5, 5), (5, 6), (8, 6), (9, 1), (9, 2), (9, 3), (9, 4);

-- Tabla: valoraciones
DROP TABLE IF EXISTS valoraciones;
CREATE TABLE valoraciones (
  id BIGSERIAL PRIMARY KEY,
  comentario TEXT DEFAULT NULL,
  fecha DATE DEFAULT NULL,
  puntuacion INTEGER NOT NULL,
  curso_id BIGINT DEFAULT NULL,
  usuario_id BIGINT DEFAULT NULL,
  CONSTRAINT fk_curso_valoraciones FOREIGN KEY (curso_id) REFERENCES cursos(id),
  CONSTRAINT fk_usuario_valoraciones FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Inserta datos en valoraciones
INSERT INTO valoraciones (id, comentario, fecha, puntuacion, curso_id, usuario_id) VALUES
(1, 'comentario de prueba de 02-05-2023', '2023-05-02', 4, 4, 1),
(3, 'Molestias voluptatem eos suscipit iure dolorem modi repellendus qui. Maxime temporibus explicabo. Necessitatibus natus eius consequatur qui nesciunt ab eius labore. Natus dolor aperiam adipisci culpa quia.', '2023-06-11', 5, 5, 1),
(6, 'Eaque doloribus molestiae quam. Et occaecati quis repudiandae aut non aperiam expedita. Quo delectus adipisci facilis doloribus aut exercitationem vitae dignissimos. Velit eius dolore.', '2023-06-11', 5, 1, 2),
(7, 'Tempora doloribus voluptas voluptate nihil. Repellendus repellendus dicta consequatur vitae et quis nihil quo. Cum sapiente rem ut odit molestiae tempora voluptates.', '2023-06-11', 5, 1, 2),
(8, 'Chanante ipsum dolor sit amet, es de traca nisi eveniet elit elit, saepe muchachada monetes. Aliqua mangurrián churretoso et veniam aliqua adipisicing ut, ex elit horcate ea. Ahí va qué chorrazo ut et monetes adipisicing. Et ullamco ut melifluo cascoporro ex interneeeer viejuno minim incididunt nostrud ut labore. Enim ut ut eiusmod bizcoché ad gambitero bufonesco ju-já saepe. Ad minim labore et veniam adipisicing ex quis. Eiusmod ullamco ut dolore one more time elit nisi ut ex enim freshquisimo nisi.', '2023-07-28', 4, 2, 3),
(9, 'Mangurrián elit aliqua fresquete tempor zanguango tempor piticli quis nostrud eiusmod. Chiquititantantan enim tempor saepe. Magna, ut elit et to sueltecico. Veniam aliqua chachachá. Ea tontaco, ad eveniet gaticos, ut eiusmod saepe. Magna ex aliqua ojete moreno elit exercitation exercitation minim nianoniano asquerosito chotera elit ut. Pepino en la mano aliqua ahí va qué chorrazo exercitation minim, ullamco.', '2023-07-22', 3, 4, 4),
(10, 'Chanante ipsum dolor sit amet, nisi regomello ut ad. Minim, ut, ut cascoporro tollina ex artista. Traeros tol jamón hueles avinagrado minim síberet asquerosito labore te viste de torero melifluo eiusmod.', '2023-07-01', 3, 3, 3);
