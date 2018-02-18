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
  PRIMARY KEY (id_pk) ,
  FOREIGN KEY (propietario_fk) REFERENCES Respondiente(id_pk)
);
GO

CREATE TABLE Aplicacion (
  id_pk VARCHAR(8) NOT NULL UNIQUE ,
  periodo VARCHAR(3) NOT NULL ,
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
  aplicacion_fk VARCHAR(8) NOT NULL ,
  PRIMARY KEY (id_pk) ,
  FOREIGN KEY (pregunta_respuesta_fk) REFERENCES Pregunta_Respuesta(id_pk) ,
  FOREIGN KEY (aplicacion_fk) REFERENCES Aplicacion(id_pk) ,
);
GO

CREATE TABLE Respuesta_Abierta (
  respuesta VARCHAR(255) NOT NULL ,
  pregunta_fk INT NOT NULL ,
  aplicacion_fk VARCHAR(8) NOT NULL ,
  FOREIGN KEY (pregunta_fk) REFERENCES Pregunta(id_pk) ,
  FOREIGN KEY (aplicacion_fk) REFERENCES Aplicacion(id_pk)
);
GO

CREATE PROCEDURE test
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
GO

CREATE PROCEDURE login @email VARCHAR(45), @password VARCHAR(45), @login INT OUT
  AS
  --Declare variable
  DECLARE @id INT
  --Check for match
  IF exists(SELECT @id = id_pk FROM Respondiente WHERE email = @email AND password = @password GROUP BY id_pk)
      BEGIN
        SET @login = @id
      END
  ELSE SET @login = -1
  --Send confirmation
  SELECT @login
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

CREATE VIEW Active_Polls AS SELECT id_pk FROM Encuesta WHERE activa = 1;
GO

CREATE PROCEDURE getIsPollActive @token VARCHAR(8), @active BIT OUT
  AS
  --Check for existence
  IF exists(SELECT id_pk FROM Active_Polls WHERE id_pk = @token)
      BEGIN
        SET @active = 1
      END
  ELSE SET @active = 0
  --Send confirmation
  SELECT @active
GO

CREATE VIEW Poll_Owners AS SELECT R.id_pk AS Propetario, E.id_pk AS Token FROM Respondiente R INNER JOIN Encuesta E ON R.id_pk = E.propietario_fk;

CREATE PROCEDURE getIsPollOwner @owner INT, @token VARCHAR(8), @isOwner BIT OUT
  AS
  --Check for existence
  IF exists(SELECT * FROM Poll_Owners WHERE propetario = @owner AND token = @token)
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
        IF NOT exists(SELECT * FROM Poll_Owners WHERE token = @generated)
          BEGIN
            SET @token = @generated
            SET @unique = 1
          END
      END
  END
  --Return token
  SELECT @token
GO