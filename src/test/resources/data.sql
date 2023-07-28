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
('Home Studio intermedio',4.5,'2023-03-01','2023-03-02',1,2),
('Masterizacion de Audio: La guía completa de cómo masterizar',4.6,'2023-03-01','2023-05-01',1,2);

INSERT INTO usuarios (nombre,apellidos,email,password) VALUES
('María','García Sánchez','maria@localhost','1234'),
('Juan Antonio','Ponferrada Dominguez','juanantonio@localhost','1234'),
('Marta','Toral Alonso','marta@localhost','1234'),
('Pedro','Villa Ledesma','pedro@localhost','1234');

INSERT INTO usuarios (nombre,apellidos,email,estado,password) VALUES
('Helena','García Sánchez','helena@localhost','A','1234'),
('Carlos','Toreno Sil','carlos@localhost','A','1234'),
('Ines','Boeza Alonso','ines@localhost','A','1234'),
('Isabel','Fresnedo Noceda','isable@localhost','A','1234');

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

INSERT INTO contenidos (titulo,documento,orden,curso_id) VALUES
('a título 1','Dolore piticli exercitation horcate bajonaaa artista. To sueltecico bocachoti cobete hueles avinagrado churretoso fresquete et cabeza de viejo cuerpo de joven enjuto mojamuto nui veniam elit ex páharo.',1,1),
('a título 2','Viejuno bonico del tó regomello interneeeer droja one more time gañán ut atiendee elit pepinoninoni chotera. Bufonesco gambitero muchachada cosica cascoporro ojete calor síberet melifluo eveniet, aliqua. ',2,1),
('a título 3','Asobinao, eiusmod pero qué pelazo tontaco eiusmod. Chachachá cartoniano et traeros tol jamón eres un pirámidee. Tontiploster incididunt mangurrián monetes tunante zanguango quis gaticos saepe.',3,1),
('b.1 título 1','Eiusmod asobinao páharo no te digo ná y te lo digo tó ju-já chachachá mangurrián. ',1,2),
('b.2 título 2','Enratonao artista hueles avinagrado eveniet cabeza de viejo cuerpo de joven nisi cobete tollina bajonaaa regomello. Chavalada bufonesco nianoniano sed gambitero.',2,2),
('c.1 título 1','Chotera piticli tempor bizcoché viejuno vivo con tu madre en un castillo forrondosco coconut zagal. Estoy fatal de lo mío ojete moreno tunante bonico del tó fresquete nui freshquisimo elit atiendee one more time enim.',1,4),
('c.2 título 2','To sueltecico ahí va qué chorrazo churretoso es de traca, ayy qué gustico pero qué pelazo labore ex minim monguer Guaper con las rodillas in the guanter, tempor tontaco zanguango. ',2,4),
('c.3 título 3',' Soooy crossoverr mamellas cacahué dolore muchachada enjuto mojamuto te viste de torero melifluo enim asquerosito nuiiiii droja pataliebre consectetur bocachoti.',3,4)
;

INSERT INTO avances (usuario_id,curso_id,tema) VALUES
(1,4,1);

INSERT INTO valoraciones (usuario_id,curso_id,puntuacion,comentario,fecha) VALUES
(1,4,4,'comentario de prueba de 02-05-2023','2023-05-02');
