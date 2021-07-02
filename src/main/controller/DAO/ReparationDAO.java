package main.controller.DAO;

import main.model.*;
import main.model.enums.EtatAscenseur;
import main.model.enums.TypeReparation;
import main.model.interfaces.PlanningRessource;
import main.model.interfaces.PlanningRessourceComparator;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReparationDAO {
    private final DataAccess instance;

    public ReparationDAO() {
        instance = DataAccess.getInstance();
    }

    public List<PlanningRessource> getMyPlanning() throws SQLException, ParseException {
        ArrayList<PlanningRessource> result = new ArrayList<>();

        // get available reparations according to our role
        if (DataAccess.isAscensoriste()) {
            try (PreparedStatement stmt = instance.getConnection()
                    .prepareStatement("select * from immeuble natural join adresse natural join ascenseur join reparation using(idAscenseur) where reparation.login=?;")) {
                stmt.setString(1, DataAccess.getLogin());
                try (ResultSet rs = stmt.executeQuery()) {
                    result = getReparations(rs);
                }
            }
        } else if (DataAccess.isGestionnaire()) {
            try (PreparedStatement stmt = instance.getConnection()
                    .prepareStatement("select * from immeuble natural join adresse natural join ascenseur join reparation using(idAscenseur) where immeuble.login=?;")) {
                stmt.setString(1, DataAccess.getLogin());
                try (ResultSet rs = stmt.executeQuery()) {
                    result = getReparations(rs);
                }
            }
        } else {
            try (Statement stmt = instance.getConnection().createStatement()) {
                try (ResultSet rs = stmt
                        .executeQuery("select * from immeuble natural join adresse natural join ascenseur join reparation using(idAscenseur);")) {
                    result = getReparations(rs);
                }
            }
        }

        // get intervention
        try (Statement stmt1 = instance.getConnection().createStatement()) {
            try (ResultSet rs1 = stmt1
                    .executeQuery("select * from immeuble natural join adresse natural join ascenseur join reparation using(idAscenseur) natural join intervention;")) {
                while (rs1.next()) {
                    int idAscenseur = rs1.getInt("idAscenseur");
                    java.util.Date datePanne = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(rs1.getString("datePanne"));
                    Timestamp dateIntervention = rs1.getTimestamp("dateIntervention");
                    int avancement = rs1.getInt("avancement");

                    Reparation reparation = getThisReparation(result, idAscenseur, datePanne);

                    Intervention intervention = new Intervention(reparation, dateIntervention);
                    intervention.setAvancement(avancement);
                    result.add(intervention);
                }
            }
        }

        // get trajet
        try (Statement stmt2 = instance.getConnection().createStatement()) {
            try (ResultSet rs2 = stmt2
                    .executeQuery("select * from immeuble natural join adresse natural join ascenseur join reparation using(idAscenseur) natural join trajetaller;")) {
                while (rs2.next()) {
                    int idAscenseur = rs2.getInt("idAscenseur");
                    java.util.Date datePanne = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(rs2.getString("datePanne"));
                    Timestamp dateTrajet = rs2.getTimestamp("dateTrajet");
                    int dureeTrajet = rs2.getInt("dureeTrajet");

                    Reparation reparation = getThisReparation(result, idAscenseur, datePanne);

                    TrajetAller trajetAller = new TrajetAller(reparation, dateTrajet, dureeTrajet);
                    result.add(trajetAller);
                }
            }
        }
        // sort result
        result.sort(new PlanningRessourceComparator());

        return result;
    }


    private ArrayList<PlanningRessource> getReparations(ResultSet rs) throws SQLException {
        ArrayList<PlanningRessource> result = new ArrayList<>();

        while (rs.next()) {
            TypeReparation typeReparation = TypeReparation.get(rs.getString("typeReparation"));
            Ascenseur ascenseur = new Ascenseur(rs.getString("marque"), rs.getString("modele"),
                    rs.getTimestamp("miseEnService"), rs.getInt("etage"),
                    EtatAscenseur.get(rs.getString("etat")));
            ascenseur.setIdAscenseur(rs.getInt("idAscenseur"));

            Reparation reparation = new Reparation(ascenseur, rs.getTimestamp("datePanne"),
                    typeReparation, typeReparation.duree);
            reparation.setImmeuble(new Immeuble(rs.getString("nom"), rs.getInt("nbEtage"),
                    new Adresse(rs.getString("rue"), rs.getString("ville"),
                            rs.getString("CP"), rs.getFloat("latitude"), rs.getFloat("longitude"))));
            reparation.setLoginAscensoriste(rs.getString("reparation.login"));
            reparation.setCommentaire(rs.getString("commentaire"));
            result.add(reparation);
        }
        return result;
    }

    private Reparation getThisReparation(List<PlanningRessource> reparations, int idAscenseur, java.util.Date datePanne) {
        return (Reparation) reparations.stream().filter(v -> {
                    if (v instanceof Reparation) {
                        return ((Reparation) v).getAscenseur().getIdAscenseur() == idAscenseur &&
                                ((Reparation) v).getDatePanne().compareTo(datePanne) == 0;
                    }
                    return false;
                }
        ).findFirst().orElse(null);
    }

    /**
     * Ajout une nouvelle panne qui a pour date Maintenant
     *
     * @param reparation
     * @param idAscenseur
     * @throws SQLException
     */
    public void addReparation(Reparation reparation, int idAscenseur) throws SQLException {
        try (PreparedStatement stmt = instance.getConnection()
                .prepareStatement("insert into reparation(idAscenseur,datePanne,typeReparation,duree) values(?,?,?,?);")) {
            stmt.setInt(1, idAscenseur);
            stmt.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(reparation.getDatePanne()));
            stmt.setString(3, reparation.getType().toString());
            stmt.setInt(4, reparation.getType().duree);
            stmt.executeUpdate();
        }
    }


    /**
     * @param reparation
     * @param intervention
     * @throws SQLException
     */
    public void attachIntervention(Reparation reparation, Intervention intervention) throws SQLException {
        instance.getConnection().setAutoCommit(false);

        try {
            // Création intervention
            try (PreparedStatement stmt = instance.getConnection()
                    .prepareStatement("insert into intervention (dateIntervention) values(?);", Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(intervention.getDateIntervention()));
                stmt.executeUpdate();

                // La rattacher à la panne
                try (ResultSet rs1 = stmt.getGeneratedKeys()) {
                    if (rs1.next()) {
                        int idIntervention = rs1.getInt(1);

                        try (PreparedStatement stmt2 = instance.getConnection()
                                .prepareStatement("update reparation set IdIntervention=? where idAscenseur=? and datePanne=?;")) {
                            stmt2.setInt(1, idIntervention);
                            stmt2.setInt(2, reparation.getAscenseur().getIdAscenseur());
                            stmt2.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(reparation.getDatePanne()));
                            stmt2.executeUpdate();
                        }
                    }
                }

                // Passer l'ascenseur dans l'état Réparation
                Ascenseur ascenseur = new AscenseurDAO().getAscenseur(reparation.getAscenseur().getIdAscenseur());
                ascenseur.setState(EtatAscenseur.EnCoursDeReparation);

                new AscenseurDAO().editAscenseur(ascenseur.getIdAscenseur(), ascenseur);
            }

            instance.getConnection().commit();

        } catch (SQLException throwables) {
            instance.getConnection().rollback();
            instance.getConnection().setAutoCommit(true);
            throw throwables;
        }

        instance.getConnection().setAutoCommit(true);
    }

    /**
     * @param reparation
     * @param trajet
     * @throws SQLException
     */
    public void attachTrajet(Reparation reparation, TrajetAller trajet) throws SQLException {
        instance.getConnection().setAutoCommit(false);

        try {
            // Création trajet
            try (PreparedStatement stmt = instance.getConnection()
                    .prepareStatement("insert into trajetaller (dateTrajet, dureeTrajet) values(?,?);",
                            Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(trajet.getDateTrajet()));
                stmt.setInt(2, trajet.getDureeTrajet());
                stmt.executeUpdate();

                // La rattacher à la panne
                try (ResultSet rs1 = stmt.getGeneratedKeys()) {
                    if (rs1.next()) {
                        int idTrajet = rs1.getInt(1);

                        try (PreparedStatement stmt2 = instance.getConnection()
                                .prepareStatement("update reparation set idTrajet=?, login=? " +
                                        "where idAscenseur=? and datePanne=?;")) {
                            stmt2.setInt(1, idTrajet);
                            stmt2.setString(2, reparation.getLoginAscensoriste());
                            stmt2.setInt(3, reparation.getAscenseur().getIdAscenseur());
                            // Not working if using Timestamp
                            stmt2.setString(4, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(reparation.getDatePanne()));
                            stmt2.executeUpdate();
                        }
                    }
                }
            }
            instance.getConnection().commit();

        } catch (SQLException throwables) {
            instance.getConnection().rollback();
            instance.getConnection().setAutoCommit(true);
            throw throwables;
        }

        instance.getConnection().setAutoCommit(true);
    }

    public void updateStatusReparation(Reparation reparation, Intervention intervention) throws SQLException {
        try (PreparedStatement stmt = instance.getConnection()
                .prepareStatement("update Reparation set login=? where idAscenseur=? and datePanne=?;")) {
            stmt.setString(1, reparation.getLoginAscensoriste());
            stmt.setInt(2, reparation.getAscenseur().getIdAscenseur());
            stmt.setTimestamp(3, new Timestamp(reparation.getDatePanne().getTime()));
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt1 = instance.getConnection()
                .prepareStatement("update Intervention set avancement=? " +
                        "where IdIntervention=(select IdIntervention from Reparation where idAscenseur=? and datePanne=?);")) {
            stmt1.setInt(1, intervention.getAvancement());
            stmt1.setInt(2, reparation.getAscenseur().getIdAscenseur());
            stmt1.setTimestamp(3, new Timestamp(reparation.getDatePanne().getTime()));
            stmt1.executeUpdate();
        }
    }

}

