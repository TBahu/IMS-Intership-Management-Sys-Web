/*CREATE DATABASE stage;

USE stage;*/

CREATE USER 'stagiere'@'localhost' IDENTIFIED BY 'stagiere';
GRANT ALL  ON stage.* TO 'stagiere'@'localhost';

CREATE  TABLE etudiant(

  nom CHAR(30) NOT NULL,
  prenom CHAR(30) NOT NULL,
  sexe CHAR(1) NOT NULL,
  email CHAR(30) NOT NULL,
  adresse CHAR(30) NOT NULL,
  tel CHAR(15) NOT NULL,
  PRIMARY KEY (email)

);
CREATE TABLE choix (

  id INT AUTO_INCREMENT NOT NULL,
  email_etudiant CHAR(30) NOT NULL,
  choix1 INT NOT NULL,
  choix2 INT NOT NULL,
  choix3 INT NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY(email_etudiant) REFERENCES etudiant(email) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(choix1) REFERENCES theme(id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(choix2) REFERENCES theme(id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(choix3) REFERENCES theme(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE  TABLE phase(

  id INT AUTO_INCREMENT NOT NULL,
  description VARCHAR(5000)   NOT NULL,
  number_phase INT  NOT NULL,
  date_start DATE NOT NULL,
  date_end DATE NOT NULL,
  state TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

CREATE  TABLE etat (

  id  INT AUTO_INCREMENT,
  email_etudiant CHAR(30) NOT NULL,0
  note_enseignant DOUBLE(10,2)  NOT NULL,
  note_tuteur DOUBLE(10,2)  NOT NULL,
  id_phase        INT     NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY(email_etudiant) REFERENCES etudiant(email),
  FOREIGN KEY(id_phase) REFERENCES phase(id)

);

CREATE  TABLE reclamation(
  id INT AUTO_INCREMENT NOT NULL,
  id_etat INT NOT NULL,
  description VARCHAR(5000) NOT NULL,
  email_sender CHAR(30) NOT NULL,
  email_recepteur CHAR(30) NOT NULL,
  switch TINYINT NOT NULL DEFAULT 0,
  read_status TINYINT NOT NULL DEFAULT 0,
  date TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id),
  FOREIGN KEY(id_etat) REFERENCES etat(id)
);


CREATE  TABLE tuteur(

  nom CHAR(30) NOT NULL,
  prenom CHAR(30) NOT NULL,
  sexe CHAR(1) NOT NULL,
  email CHAR(30) NOT NULL,
  societe CHAR(30) NOT NULL,
  adresse CHAR(30) NOT NULL,
  tel CHAR(15) NOT NULL,
  PRIMARY KEY (email)

);


CREATE  TABLE enseignant(

  nom CHAR(30) NOT NULL,
  prenom CHAR(30) NOT NULL,
  sexe CHAR(1) NOT NULL,
  email CHAR(30) NOT NULL,
  adresse CHAR(30) NOT NULL,
  tel CHAR(15) NOT NULL,
  PRIMARY KEY (email)

);

CREATE  TABLE responsable(

  nom CHAR(30) NOT NULL,
  prenom CHAR(30) NOT NULL,
  sexe CHAR(1) NOT NULL,
  email CHAR(30) NOT NULL,
  adresse CHAR(30) NOT NULL,
  tel CHAR(15) NOT NULL,
  PRIMARY KEY (email)

);

CREATE  TABLE agent(

  nom CHAR(30) NOT NULL,
  prenom CHAR(30) NOT NULL,
  sexe CHAR(1) NOT NULL,
  email CHAR(30) NOT NULL,
  adresse CHAR(30) NOT NULL,
  tel CHAR(15) NOT NULL,
  PRIMARY KEY (email)


);


CREATE TABLE identification(

  identifier CHAR(6) NOT NULL,
  type INT NOT NULL,
  PRIMARY KEY (identifier)

);



CREATE TABLE login(

  email CHAR(30) NOT NULL,
  password CHAR(100) NOT NULL,
  salt CHAR(100) NOT NULL,
  type INT NOT NULL,
  PRIMARY KEY (email)

);

CREATE TABLE theme(

  id INT  AUTO_INCREMENT NOT NULL,
  titre CHAR(150) NOT NULL UNIQUE ,
  description VARCHAR(5000) NOT NULL,
  proposeur_type INT NOT NULL,
  proposeur_email CHAR(30) NOT NULL,
  PRIMARY KEY (id)
  FOREIGN KEY(proposeur_email) REFERENCES login(email) ON DELETE CASCADE ON UPDATE CASCADE;
);

CREATE TABLE recovery(

  id INT AUTO_INCREMENT,
  email CHAR(30) NOT NULL,
  code CHAR(10) NOT NULL,
  date TIMESTAMP NOT NULL DEFAULT NOW(),
  active TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  FOREIGN KEY(email) REFERENCES login(email)

);

CREATE TABLE affectation (
  id INT AUTO_INCREMENT,
  email_etudiant CHAR(30) NOT NULL,
  email_enseignant CHAR(30) NOT NULL,
  id_theme INT NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY(email_enseignant) REFERENCES enseignant(email) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(email_etudiant) REFERENCES etudiant(email) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(id_theme) REFERENCES theme(id) ON DELETE CASCADE ON UPDATE CASCADE

);

CREATE TABLE faculte (
  id INT AUTO_INCREMENT,
  nom CHAR(30) NOT NULL,
  chef_fac CHAR(30) NOT NULL,
  location CHAR(30) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE departement (
  id INT AUTO_INCREMENT,
  nom CHAR(30) NOT NULL,
  id_faculte INT NOT NULL,
  chef_dep CHAR(30) NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY(id_faculte) REFERENCES faculte(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE specialite (
  id INT AUTO_INCREMENT,
  nom CHAR(30) NOT NULL,
  id_dep INT NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY(id_dep) REFERENCES departement(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE formation (
  id INT AUTO_INCREMENT,
  nom CHAR(30) NOT NULL,
  id_faculte INT NOT NULL,
  id_spec INT NOT NULL,
  type TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY(id),
  FOREIGN KEY(id_faculte) REFERENCES faculte(id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(id_spec) REFERENCES specialite(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE master (
  id INT AUTO_INCREMENT,
  nom CHAR(30) NOT NULL,
  id_formation INT NOT NULL,
  type TINYINT NOT NULL DEFAULT 2,
  PRIMARY KEY(id),
  FOREIGN KEY(id_formation) REFERENCES formation(id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE licence (
  id INT AUTO_INCREMENT,
  nom CHAR(30) NOT NULL,
  id_formation INT NOT NULL,
  type TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY(id),
  FOREIGN KEY(id_formation) REFERENCES formation(id) ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE `etudiant` (
ADD `date_naissance` DATE NOT NULL AFTER `tel`,
ADD `lieu_naissance` CHAR(30) NOT NULL AFTER `date_naissance`,
ADD `ville` CHAR(30) NOT NULL AFTER `lieu_naissance`,
ADD `nationalite` CHAR(30) NOT NULL AFTER `ville`,
ADD `formation` INT NOT NULL AFTER `nationalite`,
ADD `specialite` INT NOT NULL AFTER `formation`

);

CREATE TABLE deliberation (
  id INT AUTO_INCREMENT,
  nom CHAR(30) NOT NULL,
  prenom CHAR(30) NOT NULL,
  phase1 DOUBLE (10,2) NOT NULL,
  phase2 DOUBLE(10,2)  NOT NULL,
  phase3 DOUBLE(10,2)  NOT NULL,
  phase4 DOUBLE(10,2)  NOT NULL,
  moyenne DOUBLE(10,2) NOT NULL,
  decision TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY(id)
);

