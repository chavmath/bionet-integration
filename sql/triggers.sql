CREATE TABLE log_cambios_resultados (
    id INT IDENTITY PRIMARY KEY,
    operacion VARCHAR(10),
    paciente_id VARCHAR(50),
    tipo_examen VARCHAR(100),
    fecha DATE
);

GO

CREATE TRIGGER trg_log_cambios
ON resultados_examenes
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO log_cambios_resultados (operacion, paciente_id, tipo_examen, fecha)
    SELECT
        CASE
            WHEN EXISTS(SELECT * FROM inserted EXCEPT SELECT * FROM deleted) THEN 'UPDATE'
            ELSE 'INSERT'
        END,
        paciente_id, tipo_examen, fecha_examen
    FROM inserted;
END
