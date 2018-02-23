CREATE DATABASE poller COLLATE Latin1_General_CI_AS
GO
USE poller
GO

CREATE TABLE Respondiente (
  id_pk INT NOT NULL IDENTITY ,
  firstName NVARCHAR(45)) COLLATE Latin1_General_CI_AS NOT NULL ,
  secondName NVARCHAR(255)) COLLATE Latin1_General_CI_AS NOT NULL ,
  email NVARCHAR(255)) COLLATE Latin1_General_CI_AS NOT NULL UNIQUE ,
  password NVARCHAR(45)) COLLATE Latin1_General_CI_AS NOT NULL ,
  PRIMARY KEY (id_pk)
);
GO

CREATE TABLE Encuesta (
  id_pk VARCHAR(8) NOT NULL UNIQUE ,
  titulo VARCHAR(255)) COLLATE Latin1_General_CI_AS NOT NULL ,
  activa BIT NOT NULL ,
  propietario_fk INT NOT NULL ,
  periodo NVARCHAR(45)) COLLATE Latin1_General_CI_AS NOT NULL ,
  PRIMARY KEY (id_pk) ,
  FOREIGN KEY (propietario_fk) REFERENCES Respondiente(id_pk)
);
GO

CREATE TABLE Aplicacion (
  id_pk INT NOT NULL IDENTITY ,
  periodo NVARCHAR(45)) COLLATE Latin1_General_CI_AS NOT NULL ,
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
  texto NVARCHAR(255)) COLLATE Latin1_General_CI_AS NOT NULL ,
  encuesta_fk NVARCHAR(8) NOT NULL ,
  tipo_fk INT NOT NULL ,
  PRIMARY KEY (id_pk) ,
  FOREIGN KEY (encuesta_fk) REFERENCES Encuesta(id_pk) ,
  FOREIGN KEY (tipo_fk) REFERENCES Tipo(id_pk)
);
GO

CREATE TABLE Pregunta_Respuesta (
  id_pk INT NOT NULL IDENTITY ,
  respuesta NVARCHAR(255)) COLLATE Latin1_General_CI_AS NOT NULL ,
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
  respuesta NVARCHAR(255)) COLLATE Latin1_General_CI_AS NOT NULL ,
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

CREATE VIEW Respondent AS SELECT id_pk AS id, firstName, secondName, email, password FROM Respondiente;
GO

CREATE PROCEDURE login @email NVARCHAR(45), @password NVARCHAR(45), @login INT OUT
  AS
  --Check for match
  SELECT @login = id FROM Respondent WHERE email = cast(@email AS VARCHAR) AND password = @password GROUP BY id
GO

CREATE PROCEDURE register @firstName NVARCHAR(255), @secondName NVARCHAR(255), @email NVARCHAR(45), @password NVARCHAR(45), @success BIT OUT
  AS
  --Insert values
  INSERT INTO Respondent(firstName, secondName, email, password) VALUES (@firstName, @secondName, CAST(@email AS VARCHAR), @password)
  --Confirm Insert
  IF @@ROWCOUNT = 1
      SET @success = 1
  ELSE
    SET @success = 0
  --Send Confirmation
  SELECT @success;
GO

CREATE PROCEDURE canCreateAccount @email NVARCHAR(45), @confirm BIT OUT
  AS
  --Check for existence
  IF NOT exists(SELECT email FROM Respondiente WHERE email = cast(@email AS VARCHAR))
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

CREATE PROCEDURE createPoll @title NVARCHAR(45), @owner INT, @active BIT, @term NVARCHAR(45), @token VARCHAR(8) OUT
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

CREATE PROCEDURE createQuestion @text NVARCHAR(255), @kind INT, @token VARCHAR(8), @id INT OUT
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

CREATE PROCEDURE createAnswer @answer NVARCHAR(255), @question INT, @id INT OUT
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

CREATE PROCEDURE getPoll @token VARCHAR(8), @title NVARCHAR(45) OUT, @active BIT OUT, @term NVARCHAR(45) OUT
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

CREATE VIEW Survey AS SELECT id_pk AS id, periodo, fecha, encuesta_fk AS token, respondiente_fk AS respondiente FROM Aplicacion
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

CREATE PROCEDURE savePoll @respondent INT, @token VARCHAR(8), @term NVARCHAR(45), @id INT OUT
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
            IF NOT exists(SELECT * FROM AnswerSelection WHERE aplicacion = @application AND respuesta = @answer)
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

CREATE PROCEDURE saveAnswerInput @application INT, @token VARCHAR(8), @question INT, @answer NVARCHAR(255)
  AS
  DECLARE @active INT
  EXEC getIsPollActive @token, @active = @active OUT
  IF @active = 1
      BEGIN
        INSERT INTO AnswerInput VALUES (@application, @question, @answer);
      END
GO

CREATE PROCEDURE savePoll @respondent INT, @token VARCHAR(8), @term NVARCHAR(45), @id INT OUT
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
             INSERT INTO Survey(periodo, fecha, token, respondiente) VALUES (@term, sysdatetime(), @token, @respondent)
             --Confirm Insert
            IF @@ROWCOUNT = 1
              SET @id = scope_identity()
          END
      END
  SELECT @id
GO

CREATE PROCEDURE dropAll -- Purge All OOF
  AS
  DELETE FROM dbo.Respuesta_Seleccion
  DELETE FROM dbo.Respuesta_Abierta
  DELETE FROM dbo.Aplicacion
  DELETE FROM dbo.Pregunta_Respuesta
  DELETE FROM dbo.Pregunta
  DELETE FROM dbo.Encuesta
GO

CREATE PROCEDURE getSelectionStatistics @token VARCHAR(8), @question INT, @term NVARCHAR(45)
  AS
  SELECT A.respuesta, count(A.respuesta) FROM Survey S --Select from Surveys by Respondents
    INNER JOIN AnswerSelection AA ON AA.aplicacion = S.id --By matching id
    INNER JOIN Answer A ON AA.respuesta = A.id --The Answer text and count
    WHERE S.token = @token AND S.periodo = @term --That match a token and a term
  GROUP BY A.respuesta --Counting by each answer
GO

CREATE PROCEDURE getInputStatistics @token VARCHAR(8), @question INT, @term NVARCHAR(7)
  AS
  SELECT AI.respuesta FROM Survey S --Select from Surveys by Respondents
    INNER JOIN AnswerInput AI ON S.id = AI.aplicacion --By matching id, the Answer text
  WHERE S.token = @token AND S.periodo = @term AND AI.pregunta = @question --That match a token, a term and a question
GO

CREATE PROCEDURE getTerms @token VARCHAR(8)
  AS
  SELECT periodo AS term FROM Survey S WHERE token = @token GROUP BY periodo
GO

CREATE PROCEDURE getPollInfo @token VARCHAR(8)
  AS
  SELECT titulo AS title, periodo AS term, activa AS active FROM Poll WHERE token = @token
GO

CREATE PROCEDURE getTokens @owner INT
  AS
  SELECT token FROM Poll WHERE propietario = @owner
GO

CREATE TRIGGER limitAnswerNumber
  ON Answer INSTEAD OF INSERT
  AS
  DECLARE @count INT, @pregunta INT, @tipo INT
  SELECT @pregunta = pregunta FROM inserted
  SELECT @count = count(*) FROM Answer WHERE pregunta = @pregunta
  SELECT @tipo = tipo FROM Question WHERE id = @pregunta
  IF @count < 5 AND @tipo != 3
      INSERT INTO Answer(respuesta, pregunta) SELECT respuesta, pregunta FROM inserted
GO