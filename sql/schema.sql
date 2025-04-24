CREATE TABLE resultados_examenes (
    id INT IDENTITY PRIMARY KEY,
    laboratorio_id INT NOT NULL,
    paciente_id VARCHAR(50) NOT NULL,
    tipo_examen VARCHAR(100) NOT NULL,
    resultado VARCHAR(255),
    fecha_examen DATE,
    CONSTRAINT UQ_examen_paciente_fecha UNIQUE (paciente_id, tipo_examen, fecha_examen)
);
