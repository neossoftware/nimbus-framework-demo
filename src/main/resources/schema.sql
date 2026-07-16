-- Ejecutado por H2 en cada conexión (ver db.url en app.properties: INIT=RUNSCRIPT FROM
-- 'classpath:schema.sql'), ya que DriverManagerDataSource no usa pool de conexiones.
-- Por eso debe ser idempotente: CREATE TABLE IF NOT EXISTS + MERGE (upsert) en vez de
-- CREATE TABLE / INSERT a secas.

CREATE TABLE IF NOT EXISTS cursos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    duracion_horas INT NOT NULL,
    nivel VARCHAR(20) NOT NULL
);

-- Mismos 4 cursos semilla que CursoServiceImpl (en memoria), para comparar ambos caminos.
MERGE INTO cursos (id, nombre, descripcion, duracion_horas, nivel) KEY(id) VALUES
  (1, 'Java Básico', 'Fundamentos del lenguaje Java: sintaxis, POO, colecciones', 40, 'BASICO'),
  (2, 'Spring Boot', 'Desarrollo de APIs REST con Spring Boot', 60, 'INTERMEDIO'),
  (3, 'Arquitectura de Microservicios', 'Diseño y despliegue de microservicios', 80, 'AVANZADO'),
  (4, 'SQL para Desarrolladores', 'Consultas, joins e índices', 30, 'BASICO');

-- El MERGE con ids explícitos NO ajusta la secuencia de AUTO_INCREMENT — sin esto, el
-- próximo INSERT (sin id explícito, vía guardar()) intentaría usar id=1 de nuevo y
-- chocaría con la fila semilla. Recalcula el próximo valor cada vez que el script
-- corre (idempotente: siempre a partir del MAX(id) real en ese momento, nunca un
-- valor fijo que pisaría filas ya insertadas por una conexión anterior).
ALTER TABLE cursos ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM cursos);
