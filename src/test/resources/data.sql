INSERT INTO categorias (nombre, descripcion) VALUES
('Música',''),
('Fotografía y vídeo',''),
('Salud y fitness',''),
('Desarrollo',''),
('Informática y software','');

INSERT INTO cursos (titulo,valoracion_media,categoria_id) VALUES
('Angular',4.6,5),
('React',4.7,5),
('Vue.js',4.8,5),
('Principios de Acústica para el Home Studio',4.5,1),
('Home Studio intermedio',4.5,1),
('Masterizacion de Audio: La guía completa de cómo masterizar',4.6,1);

INSERT INTO usuarios (nombre,apellidos,email) VALUES
('María','García Sánchez','maria@localhost'),
('Juan Antonio','Ponferrada Dominguez','juanantonio@localhost'),
('Marta','Toral Alonso','marta@localhost'),
('Pedro','Villa Ledesma','pedro@localhost');

INSERT INTO usuarios (nombre,apellidos,email,estado) VALUES
('Helena','García Sánchez','helena@localhost','A'),
('Carlos','Toreno Sil','carlos@localhost','A'),
('Ines','Boeza Alonso','ines@localhost','A'),
('Isabel','Fresnedo Noceda','isable@localhost','A');

INSERT INTO usuarios_cursos (usuario_id, curso_id) VALUES
(1,4),
(1,5),
(2,1),
(2,2),
(2,3),
(3,1),
(4,4),
(5,4),
(5,5),
(5,6),
(8,6);