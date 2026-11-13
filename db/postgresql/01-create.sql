-- Tabla: categorias
DROP TABLE IF EXISTS categorias;
CREATE TABLE categorias (
  id BIGSERIAL PRIMARY KEY,
  descripcion VARCHAR(255) DEFAULT NULL,
  nombre VARCHAR(255) DEFAULT NULL
);

-- Inserta datos en categorias
INSERT INTO categorias (id, descripcion, nombre) VALUES
(1, 'Información sobre los generos musicales', 'Música'),
(2, 'Técnicas que forman parte del arte visual', 'Fotografía y vídeo'),
(3, 'Estado generalizado de bienestar y salud física logrado no solo a partir del desarrollo de una vida sana', 'Salud y fitness'),
(4, '', 'Desarrollo'),
(5, '', 'Informática y software'),
(6, 'Atque libero quos. Officiis commodi dignissimos magni excepturi expedita quisquam consequatur. Ut neque sint nihil fugit. Unde enim eum.', 'consequatur commodi (UPDATED)'),
(7, 'Dolores molestiae nobis voluptatum quod ad. Rem libero delectus veritatis accusamus est numquam laboriosam deleniti. Autem magni nesciunt corporis natus. A perspiciatis accusantium est fugiat pariatur labore dicta dolore. Perspiciatis sunt eius deserunt.', 'Rustic Concrete Chicken'),
(8, 'Est nostrum animi nam sunt. Laborum vitae et omnis nihil vel. Omnis aut quam neque in ea. Asperiores velit dolorum commodi quis magni quo enim voluptatibus sed. Et dolores autem odit velit a et voluptate.', 'est aliquam'),
(9, 'Molestiae error sint aut. Porro doloremque atque aut porro aut et illum autem. Nulla et quasi officiis hic reiciendis ipsam velit rerum.', 'illo quia (UPDATED)'),
(10, 'Sed ut sed voluptatem atque odit similique. Quas adipisci fuga et. Nostrum optio et dolores molestiae esse culpa ad. Voluptas rerum qui sed hic. Exercitationem non molestiae optio distinctio. Nobis quia voluptatem voluptatem qui distinctio quaerat.', 'ut (UPDATED)'),
(11, 'Id voluptatem amet sint et eum. Qui et est voluptas eum est similique. Maiores amet aspernatur velit in. Harum nihil error harum asperiores. Commodi quia consequatur voluptas maxime eligendi. At iure eligendi laudantium nesciunt at deleniti unde corrupti.', 'sapiente (UPDATED)'),
(12, 'Perspiciatis et perspiciatis aut et a quam iste neque ducimus. Qui molestias quisquam suscipit magni. Voluptatem ullam pariatur commodi est quis quia fugiat. Delectus rerum porro soluta.', 'dicta sit');

-- Tabla: instructores
DROP TABLE IF EXISTS instructores;
CREATE TABLE instructores (
  id BIGSERIAL PRIMARY KEY,
  apellidos VARCHAR(255) DEFAULT NULL,
  descripcion VARCHAR(255) DEFAULT NULL,
  fecha_alta DATE DEFAULT NULL,
  nombre VARCHAR(255) DEFAULT NULL
);

-- Inserta datos en instructores
INSERT INTO instructores (id, apellidos, descripcion, fecha_alta, nombre) VALUES
(1, 'Merayo', 'I.T.Informática de Gestión Chanante ipsum dolor sit amet, interneeeer enim tontiploster hueles avinagrado bizcoché. Cabeza de viejo cuerpo de joven enratonao ut cosica ju-já chiquititantantan cascoporro regomello tunante.', '2023-01-01', 'Isidro'),
(2, 'Hernandez', 'Conocido por la producción de discos de música independiente de bandas bien conocidas como Los Planetas, Sr. Chinarro o Mercromina.', '2022-02-02', 'Carlos'),
(3, 'de Amor Bizarro', 'Una vez que fichan por el sello Mushroom Pillow, graban su primer disco, Triángulo de Amor Bizarro (2007), con el técnico Carlos Hernández', '2022-02-02', 'Triángulo'),
(4, 'Sánchez Sánchez', 'FOTÓGRAFO PROFESIONAL ESPECIALIZADO EN NATURALEZA Y RETRATO', '2023-07-25', 'Paula');

-- Tabla: usuarios
DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
  id BIGSERIAL PRIMARY KEY,
  apellidos VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  estado CHAR(1) DEFAULT 'P',
  nombre VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  CONSTRAINT unique_email UNIQUE (email)
);

-- Inserta datos en usuarios
INSERT INTO usuarios (id, apellidos, email, estado, nombre, password) VALUES
(1, 'Villafranca Sánchez', 'maria@localhost', 'P', 'María', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(2, 'Ponferrada Dominguez', 'juanantonio@localhost', 'P', 'Juan Antonio', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(3, 'Toral Alonso', 'marta@localhost', 'P', 'Marta', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(4, 'Villa Ledesma', 'pedro@localhost', 'P', 'Pedro', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(5, 'Fabero Sánchez', 'helena@localhost', 'A', 'Helena', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(6, 'Toreno Sil', 'carlos@localhost', 'A', 'Carlos', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(7, 'Boeza Alonso', 'ines@localhost', 'A', 'Ines', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(8, 'Fresnedo Noceda', 'isable@localhost', 'A', 'Isabel', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(9, 'Canedo Castro', 'c@example.com', 'A', 'Carla', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(11, 'Diaz Carracedelo', 'd@example.com', 'P', 'Diego', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(12, 'Cedro Igüeña', 'c@demo.com', 'P', 'Clara', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(14, 'Martinez Marcos', 'm@example.com', 'P', 'Marta', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(15, 'Frami', 'Kathryne1@example.com', 'P', 'Krista', '$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.'),
(16, 'Harber', 'Alva_Streich@example.net', 'P', 'Jevon', '$2b$10$KJxpdj39.GSIUlEfPig5peIotMt484c8b8ls1O.rRecudIuT/o2Bi');
