-- CREATE SCHEMA `e-lift` ;
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
        latitude    float NULL,
        longitude    float NULL
	,CONSTRAINT Ascensoriste_PK PRIMARY KEY (login)

	,CONSTRAINT Ascensoriste_Personne_FK FOREIGN KEY (login) REFERENCES Personne(login)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Intervention
#------------------------------------------------------------


CREATE TABLE Intervention(
         IdIntervention   Int  Auto_increment  NOT NULL ,
         dateIntervention Datetime NOT NULL,
         avancement       Int
    ,CONSTRAINT Intervention_PK PRIMARY KEY (IdIntervention)
)ENGINE=InnoDB;

#------------------------------------------------------------
# Table: Adresse
#------------------------------------------------------------

CREATE TABLE Adresse(
        rue   Varchar (100) NOT NULL ,
        ville Varchar (50) NOT NULL ,
        CP    Char (5) NOT NULL,
        latitude  Float NOT NULL ,
        longitude Float NOT NULL
	,CONSTRAINT Adresse_PK PRIMARY KEY (rue,ville)
)ENGINE=InnoDB;

#------------------------------------------------------------
# Table: Immeuble
#------------------------------------------------------------

CREATE TABLE Immeuble(
        IdImmeuble Int  Auto_increment  NOT NULL ,
        nom        Varchar (50) NOT NULL ,
        nbEtage    Int NOT NULL ,
        login      Varchar (50) NOT NULL ,
        rue        Varchar (100) NOT NULL ,
        ville      Varchar (50) NOT NULL
	,CONSTRAINT Immeuble_PK PRIMARY KEY (IdImmeuble)

	,CONSTRAINT Immeuble_Gestionnaire_FK FOREIGN KEY (login) REFERENCES Gestionnaire(login)
	,CONSTRAINT Immeuble_Adresse0_FK FOREIGN KEY (rue,ville) REFERENCES Adresse(rue,ville)
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

	,CONSTRAINT Ascenseur_Immeuble_FK FOREIGN KEY (IdImmeuble) REFERENCES Immeuble(IdImmeuble) on delete cascade
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
# Table: TrajetAller
#------------------------------------------------------------

CREATE TABLE TrajetAller(
        idTrajet    Int  Auto_increment  NOT NULL ,
        dateTrajet  Datetime NOT NULL ,
        dureeTrajet Int NOT NULL
	,CONSTRAINT TrajetAller_PK PRIMARY KEY (idTrajet)
)ENGINE=InnoDB;

#------------------------------------------------------------
# Table: reparation
#------------------------------------------------------------

CREATE TABLE reparation(
       idAscenseur    Int NOT NULL ,
       login          Varchar (50) NULL ,
       IdIntervention Int NULL ,
       idTrajet       Int NULL ,
       datePanne      Datetime NOT NULL ,
       typeReparation Varchar (50) NOT NULL ,
       duree          Int NOT NULL COMMENT 'duree en min',
       commentaire    Text
    ,CONSTRAINT reparation_PK PRIMARY KEY (idAscenseur,datePanne)

    ,CONSTRAINT reparation_Ascenseur_FK FOREIGN KEY (idAscenseur) REFERENCES Ascenseur(idAscenseur) on delete cascade
    ,CONSTRAINT reparation_Ascensoriste0_FK FOREIGN KEY (login) REFERENCES Ascensoriste(login) on delete cascade
    ,CONSTRAINT reparation_Intervention1_FK FOREIGN KEY (IdIntervention) REFERENCES Intervention(IdIntervention)
    ,CONSTRAINT reparation_TrajetAller2_FK FOREIGN KEY (idTrajet) REFERENCES TrajetAller(idTrajet)
)ENGINE=InnoDB;

CREATE ROLE 'e-lift_employe', 'e-lift_gestionnaire';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `e-lift`.* TO 'e-lift_employe';

GRANT SELECT, EXECUTE ON `e-lift`.* TO 'e-lift_gestionnaire';
GRANT INSERT ON `e-lift`.adresse TO 'e-lift_gestionnaire';
GRANT INSERT, UPDATE ON `e-lift`.reparation TO 'e-lift_gestionnaire';
GRANT INSERT ON `e-lift`.trajetaller TO 'e-lift_gestionnaire';
GRANT INSERT ON `e-lift`.intervention TO 'e-lift_gestionnaire';
GRANT INSERT, UPDATE, DELETE ON `e-lift`.ascenseur TO 'e-lift_gestionnaire';
GRANT INSERT, UPDATE, DELETE ON `e-lift`.contratmaintenance TO 'e-lift_gestionnaire';
GRANT INSERT, UPDATE, DELETE ON `e-lift`.immeuble TO 'e-lift_gestionnaire';


USE `e-lift`;
DROP function IF EXISTS `distance`;

DELIMITER $$
USE `e-lift`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `distance`(lat1 float, long1 float, lat2 float, long2 float) RETURNS float
    DETERMINISTIC
BEGIN
# 01Net, Astuce Excel : calculez la distance entre deux points de la Terre 
# https://www.01net.com/astuces/astuce-excel-calculez-la-distance-entre-deux-points-de-la-terre-555908.html
RETURN ACOS(SIN(RADIANS(lat1))*SIN(RADIANS(lat2))+COS(RADIANS(lat1))*COS(RADIANS(lat2))*COS(RADIANS(long1-long2)))*6371;
END
$$

DELIMITER ;

-- distance Paris Marseille
select distance(48.862725,2.3514616,43.2961743,5.3699525) as distance;

select *, distance(latitude, longitude, 43.2961743, 5.3699525)  as distance from ascensoriste order by distance;
