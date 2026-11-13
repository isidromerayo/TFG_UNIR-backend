-- Ajusta las secuencias tras insertar IDs explícitos en el seed.
--
-- Los INSERT de 01/02/03 incluyen la columna id, por lo que PostgreSQL no
-- avanza las secuencias asociadas a BIGSERIAL. Sin este ajuste, la primera
-- fila creada por la aplicación reutiliza un id existente y falla con
-- "duplicate key value violates unique constraint".
--
-- Se ejecuta automáticamente en el primer arranque de la imagen de PostgreSQL
-- (el entrypoint ejecuta los .sql de /docker-entrypoint-initdb.d en orden).

DO $$
DECLARE
  tabla text;
  secuencia text;
  max_id bigint;
BEGIN
  FOREACH tabla IN ARRAY ARRAY[
    'categorias', 'instructores', 'usuarios', 'cursos',
    'contenidos', 'avances', 'valoraciones'
  ]
  LOOP
    secuencia := pg_get_serial_sequence(tabla, 'id');
    IF secuencia IS NULL THEN
      RAISE NOTICE '% no tiene secuencia para id; se omite', tabla;
      CONTINUE;
    END IF;

    EXECUTE format('SELECT COALESCE(MAX(id), 0) FROM %I', tabla) INTO max_id;
    PERFORM setval(secuencia, GREATEST(max_id, 1), max_id > 0);
    RAISE NOTICE '%: secuencia ajustada a max_id=%', tabla, max_id;
  END LOOP;
END $$;
