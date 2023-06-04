INSERT INTO categorias (nombre, descripcion) VALUES
('Música',''),
('Fotografía y vídeo',''),
('Salud y fitness',''),
('Desarrollo',''),
('Informática y software','');

INSERT INTO instructores (nombre,apellidos,descripcion,fecha_alta) VALUES
('Isidro','Merayo','I.T.Informática de Gestión Chanante ipsum dolor sit amet, interneeeer enim tontiploster hueles avinagrado bizcoché. Cabeza de viejo cuerpo de joven enratonao ut cosica ju-já chiquititantantan cascoporro regomello tunante.','2023-01-01'),
('Carlos','Hernandez','Conocido por la producción de discos de música independiente de bandas bien conocidas como Los Planetas, Sr. Chinarro o Mercromina.','2022-02-02'),
('Triángulo','de Amor Bizarro','Una vez que fichan por el sello Mushroom Pillow, graban su primer disco, Triángulo de Amor Bizarro (2007), con el técnico Carlos Hernández','2022-02-02');


INSERT INTO cursos (titulo,valoracion_media,fecha_creacion,fecha_actualizacion,categoria_id,instructor_id) VALUES
('Angular',4.6,'2023-01-01','2023-02-01',5,1),
('React',4.7,'2023-01-01','2023-02-02',5,1),
('Vue.js',4.8,'2023-01-01','2023-02-03',5,1),
('Principios de Acústica para el Home Studio',4.5,'2023-03-01','2023-03-01',1,2),
('Home Studio intermedio',4.5,'2023-03-01','2023-03-01',1,2),
('Masterizacion de Audio: La guía completa de cómo masterizar',4.6,'2023-03-01','2023-03-01',1,2);

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