use `e-lift`;

#------------------------------------------------------------
# Table: Personne
#------------------------------------------------------------

CREATE TABLE Personne(
        login     Varchar (50) NOT NULL ,
        nom       Varchar (50) NOT NULL ,
        prenom    Varchar (50) NOT NULL ,
        telephone Varchar (50) NOT NULL
	,CONSTRAINT Personne_PK PRIMARY KEY (login)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Gestionnaire
#------------------------------------------------------------

CREATE TABLE Gestionnaire(
        login     Varchar (50) NOT NULL
	,CONSTRAINT Gestionnaire_PK PRIMARY KEY (login)

	,CONSTRAINT Gestionnaire_Personne_FK FOREIGN KEY (login) REFERENCES Personne(login)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Ascensoriste
#------------------------------------------------------------

CREATE TABLE Ascensoriste(
        login        Varchar (50) NOT NULL ,
        localisation Varchar (50)  NULL
	,CONSTRAINT Ascensoriste_PK PRIMARY KEY (login)

	,CONSTRAINT Ascensoriste_Personne_FK FOREIGN KEY (login) REFERENCES Personne(login)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Immeuble
#------------------------------------------------------------

CREATE TABLE Immeuble(
        IdImmeuble Int  Auto_increment  NOT NULL ,
        nom        Varchar (50) NOT NULL ,
        nbEtage    Int NOT NULL ,
        login      Varchar (50) NOT NULL
	,CONSTRAINT Immeuble_PK PRIMARY KEY (IdImmeuble)

	,CONSTRAINT Immeuble_Gestionnaire_FK FOREIGN KEY (login) REFERENCES Gestionnaire(login)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Ascenseur
#------------------------------------------------------------

CREATE TABLE Ascenseur(
        idAscenseur   Int  Auto_increment  NOT NULL ,
        marque        Varchar (50) NOT NULL ,
        modele        Varchar (50) NOT NULL ,
        miseEnService Date NOT NULL ,
        etat          Varchar (50) NOT NULL ,
        etage         Int NOT NULL ,
        IdImmeuble    Int NOT NULL
	,CONSTRAINT Ascenseur_PK PRIMARY KEY (idAscenseur)

	,CONSTRAINT Ascenseur_Immeuble_FK FOREIGN KEY (IdImmeuble) REFERENCES Immeuble(IdImmeuble)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: ContratMaintenance
#------------------------------------------------------------

CREATE TABLE ContratMaintenance(
        idContrat  Int  Auto_increment  NOT NULL ,
        debut      Date NOT NULL ,
        fin        Date NOT NULL ,
        IdImmeuble Int NOT NULL
	,CONSTRAINT ContratMaintenance_PK PRIMARY KEY (idContrat)

	,CONSTRAINT ContratMaintenance_Immeuble_FK FOREIGN KEY (IdImmeuble) REFERENCES Immeuble(IdImmeuble)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: DateReparation
#------------------------------------------------------------

CREATE TABLE DateReparation(
        dateReparation Datetime NOT NULL ,
        duree          Int NOT NULL COMMENT "duree en min" 
	,CONSTRAINT DateReparation_PK PRIMARY KEY (dateReparation)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: reparation
#------------------------------------------------------------

CREATE TABLE reparation(
        idAscenseur    Int NOT NULL ,
        login          Varchar (50) NOT NULL ,
        dateReparation Datetime NOT NULL ,
        commentaire    Text NOT NULL ,
        typeReparation Varchar (50) NOT NULL ,
        avancement     Varchar (50) NOT NULL
	,CONSTRAINT reparation_PK PRIMARY KEY (idAscenseur,login,dateReparation)

	,CONSTRAINT reparation_Ascenseur_FK FOREIGN KEY (idAscenseur) REFERENCES Ascenseur(idAscenseur)
	,CONSTRAINT reparation_Ascensoriste0_FK FOREIGN KEY (login) REFERENCES Ascensoriste(login)
	,CONSTRAINT reparation_DateReparation1_FK FOREIGN KEY (dateReparation) REFERENCES DateReparation(dateReparation)
)ENGINE=InnoDB;

CREATE ROLE 'e-lift_employe', 'e-lift_gestionnaire';
GRANT INSERT, UPDATE, DELETE ON `e-lift`.* TO 'e-lift_employe';