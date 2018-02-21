CREATE DATABASE poller;
GO
USE poller;
GO

CREATE TABLE Respondiente (
  id_pk INT NOT NULL IDENTITY ,
  firstName VARCHAR(45) NOT NULL ,
  secondName VARCHAR(255) NOT NULL ,
  email VARCHAR(45) NOT NULL UNIQUE ,
  password VARCHAR(45) NOT NULL ,
  PRIMARY KEY (id_pk)
);
GO

CREATE TABLE Encuesta (
  id_pk VARCHAR(8) NOT NULL UNIQUE ,
  titulo VARCHAR(45) NOT NULL ,
  activa BIT NOT NULL ,
  propietario_fk INT NOT NULL ,
  periodo VARCHAR(7) NOT NULL ,
  PRIMARY KEY (id_pk) ,
  FOREIGN KEY (propietario_fk) REFERENCES Respondiente(id_pk)
);
GO

CREATE TABLE Aplicacion (
  id_pk INT NOT NULL IDENTITY ,
  periodo VARCHAR(7) NOT NULL ,
  fecha DATETIME NOT NULL ,
  respondiente_fk INT NOT NULL ,
  encuesta_fk VARCHAR(8) NOT NULL ,
  PRIMARY KEY (id_pk) ,
  FOREIGN KEY (encuesta_fk) REFERENCES Encuesta(id_pk) ,
  FOREIGN KEY (respondiente_fk) REFERENCES Respondiente(id_pk)
);
GO

CREATE TABLE Tipo (
  id_pk INT NOT NULL IDENTITY ,
  tipo VARCHAR(45) NOT NULL ,
  PRIMARY KEY (id_pk)
);
GO

CREATE TABLE Pregunta (
  id_pk INT NOT NULL IDENTITY ,
  texto VARCHAR(255) NOT NULL ,
  encuesta_fk VARCHAR(8) NOT NULL ,
  tipo_fk INT NOT NULL ,
  PRIMARY KEY (id_pk) ,
  FOREIGN KEY (encuesta_fk) REFERENCES Encuesta(id_pk) ,
  FOREIGN KEY (tipo_fk) REFERENCES Tipo(id_pk)
);
GO

CREATE TABLE Pregunta_Respuesta (
  id_pk INT NOT NULL IDENTITY ,
  respuesta VARCHAR(255) NOT NULL ,
  pregunta_fk INT NOT NULL ,
  PRIMARY KEY (id_pk) ,
  FOREIGN KEY (pregunta_fk) REFERENCES Pregunta(id_pk)
);
GO

CREATE TABLE Respuesta_Seleccion (
  id_pk INT NOT NULL IDENTITY ,
  pregunta_respuesta_fk INT NOT NULL ,
  aplicacion_fk INT NOT NULL ,
  PRIMARY KEY (id_pk) ,
  FOREIGN KEY (pregunta_respuesta_fk) REFERENCES Pregunta_Respuesta(id_pk) ,
  FOREIGN KEY (aplicacion_fk) REFERENCES Aplicacion(id_pk) ,
);
GO

CREATE TABLE Respuesta_Abierta (
  respuesta VARCHAR(255) NOT NULL ,
  pregunta_fk INT NOT NULL ,
  aplicacion_fk INT NOT NULL ,
  FOREIGN KEY (pregunta_fk) REFERENCES Pregunta(id_pk) ,
  FOREIGN KEY (aplicacion_fk) REFERENCES Aplicacion(id_pk)
);
GO

INSERT INTO Tipo VALUES ('Single');
GO
INSERT INTO Tipo VALUES ('Multiple');
GO
INSERT INTO Tipo VALUES ('Open');
GO

/*CREATE PROCEDURE test -- Deleted, leaving this here as examples
  AS
  SELECT 'Hello World' AS hi;
  GO

CREATE PROCEDURE test_in @some VARCHAR(255)
  AS
  SELECT @some as value;
  GO

CREATE PROCEDURE test_out @value INT OUT
  AS
  SELECT @value = 2688164;
GO*/

CREATE PROCEDURE login @email VARCHAR(45), @password VARCHAR(45), @login INT OUT
  AS
  --Check for match
  SELECT @login = id_pk FROM Respondiente WHERE email = @email AND password = @password GROUP BY id_pk
GO

CREATE PROCEDURE register @firstName VARCHAR(45), @secondName VARCHAR(45), @email VARCHAR(45), @password VARCHAR(45), @success BIT OUT
  AS
  --Insert values
  INSERT INTO Respondiente VALUES (@firstName, @secondName, @email, @password)
  --Confirm Insert
  IF @@ROWCOUNT = 1
      SET @success = 1
  ELSE
    SET @success = 0
  --Send Confirmation
  SELECT @success;
GO

CREATE PROCEDURE canCreateAccount @email VARCHAR(45), @confirm BIT OUT
  AS
  --Check for existence
  IF NOT exists(SELECT email FROM Respondiente WHERE email = @email)
      BEGIN
        SET @confirm = 1
      END
  ELSE SET @confirm = 0
  --Send confirmation
  SELECT @confirm
GO

CREATE VIEW Poll AS SELECT id_pk AS token, titulo AS titulo, activa AS activa, periodo AS periodo, propietario_fk AS propietario FROM Encuesta
GO

CREATE PROCEDURE getIsPollActive @token VARCHAR(8), @active BIT OUT
  AS
  --Check for existence
  IF exists(SELECT token FROM Poll WHERE activa = 1 AND token = @token)
      BEGIN
        SET @active = 1
      END
  ELSE SET @active = 0
  --Send confirmation
  SELECT @active
GO

CREATE PROCEDURE getIsPollOwner @owner INT, @token VARCHAR(8), @isOwner BIT OUT
  AS
  --Check for existence
  IF exists(SELECT * FROM Poll WHERE propetario = @owner AND token = @token)
      BEGIN
        SET @isOwner = 1
      END
  ELSE SET @isOwner = 0
  --Send confirmation
  SELECT @isOwner
GO

CREATE PROCEDURE generateToken @token VARCHAR(8) OUT
AS
  --Declare variables
  DECLARE @chars VARCHAR(35), @generated VARCHAR(8), @count INT, @unique BIT
  SET @chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'
  SET @generated = ''
  SET @unique = 0
  SET @count = 0
  --Loop string generator
  WHILE @unique = 0 BEGIN
    IF (@count < 8)
      BEGIN
        --Increase char count
        SET @count = @count + 1
        --Concatenate varchar
        SET @generated = @generated + substring(@chars, (abs(checksum(newid())) % 36) + 1, 1)
      END
    ELSE
      BEGIN
        SET @count = 0
        --Check for non existence
        IF NOT exists(SELECT * FROM Poll WHERE token = @generated)
          BEGIN
            SET @token = @generated
            SET @unique = 1
          END
      END
  END
  --Return token
  SELECT @token
GO

CREATE PROCEDURE createPoll @title VARCHAR(45), @owner INT, @active BIT, @term VARCHAR(7), @token VARCHAR(8) OUT
AS
  --Check for existence
  IF exists(SELECT * FROM Poll WHERE propietario = @owner AND token = @token)
    BEGIN
      --Update Poll
      UPDATE Poll SET titulo = @title, activa = @active, periodo = @term WHERE propietario = @owner AND token = @token
    END
  ELSE
    BEGIN
      DECLARE @generatedToken VARCHAR(8)
      --Generate token
      EXEC generateToken @token = @generatedToken OUT
      IF @generatedToken IS NOT NULL
        BEGIN
          --Insert values
          INSERT INTO Poll(token, titulo, activa, periodo, propietario) VALUES (@generatedToken, @title, @active, @term, @owner)
          SET @token = @generatedToken;
        END
    END
  SELECT @token
GO

CREATE VIEW Poll_Questions AS SELECT E.id_pk AS token, P.id_pk AS id, P.texto AS texto, P.tipo_fk AS kind FROM Pregunta P INNER JOIN Encuesta E ON P.encuesta_fk = E.id_pk
GO
CREATE VIEW Question AS SELECT id_pk AS id, texto, encuesta_fk AS token, tipo_fk AS tipo FROM Pregunta
GO

CREATE PROCEDURE createQuestion @text VARCHAR(255), @kind INT, @token VARCHAR(8), @id INT OUT
  AS
  --Check for existence
  IF exists(SELECT * FROM Question WHERE id = @id)
    --Update Question
    UPDATE Question SET texto = @text, tipo = @kind WHERE id = @id
  ELSE
  BEGIN
    BEGIN
    --Insert values
    INSERT INTO Question(texto, token, tipo) VALUES (@text, @token, @kind)
    --Confirm Insert
    IF @@ROWCOUNT = 1
      SET @id = scope_identity()
    END
  END
  --Send Information
  SELECT @id
GO

CREATE VIEW Question_Answer AS SELECT P.id_pk AS pregunta, R.id_pk AS id, R.respuesta AS respuesta FROM Pregunta_Respuesta R INNER JOIN Pregunta P ON R.pregunta_fk = P.id_pk
GO
CREATE VIEW Answer AS SELECT id_pk AS id, respuesta AS respuesta, pregunta_fk AS pregunta FROM Pregunta_Respuesta
GO

CREATE PROCEDURE createAnswer @answer VARCHAR(255), @question INT, @id INT OUT
  AS
  --Check if the question is not Open
  IF NOT exists(SELECT * FROM Question WHERE id = @question AND tipo = 3)
    BEGIN
      --Check for existence
      IF exists(SELECT id FROM Question_Answer WHERE id = @id)
        BEGIN
          UPDATE Answer SET respuesta = @answer WHERE id = @id
        END
      ELSE
        BEGIN
          --Insert values
          INSERT INTO Answer(respuesta, pregunta) VALUES (@answer, @question)
          --Confirm Insert
          IF @@ROWCOUNT = 1
            SET @id = scope_identity()
        END
    END
  --Send Information
  SELECT @id
GO

CREATE VIEW Poll_Question_Answer AS SELECT P.token AS token, Q.id AS question, A.id AS answer FROM Poll P INNER JOIN Poll_Questions Q ON P.token = Q.token LEFT JOIN Question_Answer A ON A.pregunta = Q.id
GO

CREATE PROCEDURE getPoll @token VARCHAR(8), @title VARCHAR(45) OUT, @active BIT OUT, @term VARCHAR(7) OUT
  AS
  SELECT @title = titulo, @active = activa, @term = periodo FROM Poll WHERE token = @token
GO

CREATE PROCEDURE getQuestion @token VARCHAR(8)
  AS
  SELECT id, texto, tipo FROM Poll_Questions WHERE token = @token
GO

CREATE PROCEDURE getAnswer @question INT
  AS
  SELECT id, respuesta FROM Question_Answer WHERE pregunta = @question
GO

CREATE VIEW Survey AS SELECT periodo, fecha, encuesta_fk AS token, respondiente_fk AS respondiente FROM Aplicacion
GO

CREATE PROCEDURE getCanAnswerPoll @respondent INT, @token VARCHAR(8), @confirmation BIT OUT
  AS
  IF NOT exists(SELECT * FROM Survey S INNER JOIN Poll E ON S.token = E.token AND S.periodo = E.periodo WHERE S.respondiente = @respondent AND S.token = @token)
    BEGIN
      SET @confirmation = 1
    END
  ELSE SET @confirmation = 0
  SELECT @confirmation
GO

CREATE PROCEDURE savePoll @respondent INT, @token VARCHAR(8), @term VARCHAR(3), @id INT OUT
  AS
  DECLARE @active BIT
  --Check if it is active
  EXEC getIsPollActive @token, @active = @active OUT
  IF @active = 1
      BEGIN
        --Check if it can be answered
        EXEC getCanAnswerPoll @respondent, @token, @confirmation = @active OUT
        IF @active = 1
          BEGIN
            --Insert values
             INSERT INTO Survey VALUES (@term, sysdatetime(), @token, @respondent)
             --Confirm Insert
            IF @@ROWCOUNT = 1
              SET @id = scope_identity()
          END
      END
  SELECT @id
GO

CREATE VIEW AnswerSelection AS SELECT aplicacion_fk AS aplicacion, pregunta_respuesta_fk AS respuesta FROM Respuesta_Seleccion
GO

CREATE VIEW AnswerInput AS SELECT aplicacion_fk AS aplicacion, pregunta_fk AS pregunta, respuesta AS respuesta FROM Respuesta_Abierta
GO

CREATE PROCEDURE saveAnswerSelection @application INT, @question INT, @token VARCHAR(8), @answer INT
  AS
  DECLARE @active INT, @count INT, @kind INT
  --Check if it is active
  EXEC getIsPollActive @token, @active = @active OUT
  IF @active = 1
      BEGIN
        SELECT @kind = tipo FROM Poll_Questions WHERE id = @question
        IF @kind = 1
          BEGIN
            SELECT @count = count(*) FROM AnswerSelection WHERE aplicacion = @application AND respuesta = @answer
            IF @count = 0 OR @count IS NULL
              BEGIN
                INSERT INTO AnswerSelection VALUES (@application, @answer)
              END
          END
        ELSE IF @kind = 2
          BEGIN
            INSERT INTO AnswerSelection VALUES (@application, @answer)
          END
      END
GO

CREATE PROCEDURE saveAnswerInput @application INT, @token VARCHAR(8), @question INT, @answer VARCHAR(255)
  AS
  DECLARE @active INT
  EXEC getIsPollActive @token, @active = @active OUT
  IF @active = 1
      BEGIN
        INSERT INTO AnswerInput VALUES (@application, @question, @answer);
      END
GO

CREATE PROCEDURE savePoll @respondent INT, @token VARCHAR(8), @term VARCHAR(3), @id INT OUT
  AS
  DECLARE @active BIT
  --Check if it is active
  EXEC getIsPollActive @token, @active = @active OUT
  IF @active = 1
      BEGIN
        --Check if it can be answered
        EXEC getCanAnswerPoll @respondent, @token, @confirmation = @active OUT
        IF @active = 1
          BEGIN
            --Insert values
             INSERT INTO Survey VALUES (@term, sysdatetime(), @token, @respondent)
             --Confirm Insert
            IF @@ROWCOUNT = 1
              SET @id = scope_identity()
          END
      END
  SELECT @id
GO

