package main.controller.DAO;

import main.model.Intervention;
import main.model.Reparation;
import main.model.TrajetAller;
import main.model.enums.TypeReparation;
import main.model.interfaces.PlanningRessource;
import main.model.interfaces.PlanningRessourceComparator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReparationDAO {
    private final DataAccess instance;

    public ReparationDAO() {
        instance = DataAccess.getInstance();
    }

    public List<PlanningRessource> getMyPlanning() throws SQLException {
        ArrayList<PlanningRessource> result;

        // get available reparations according to our role
        if(DataAccess.isAscensoriste()) {
            try (PreparedStatement stmt = instance.getConnection().prepareStatement("select * from reparation where login=?;")) {
                stmt.setString(1, DataAccess.getLogin());
                try (ResultSet rs = stmt.executeQuery()) {
                    result = getReparations(rs);
                }
            }
        } else if(DataAccess.isGestionnaire()) {
            try (PreparedStatement stmt = instance.getConnection().prepareStatement("select * from reparation natural join ascenseur natural  join immeuble where immeuble.login=?;")) {
               stmt.setString(1, DataAccess.getLogin());
                try (ResultSet rs = stmt.executeQuery()) {
                    result = getReparations(rs);
                }
            }
        } else {
            try (Statement stmt = instance.getConnection().createStatement()) {
                try (ResultSet rs = stmt.executeQuery("select * from reparation;")) {
                    result = getReparations(rs);
                }
            }
        }

        // get intervention
        try (Statement stmt1 = instance.getConnection().createStatement()) {
            try (ResultSet rs1 = stmt1.executeQuery("select * from reparation natural join intervention;")) {
                while (rs1.next()) {
                    int idAscenseur = rs1.getInt("idAscenseur");
                    Timestamp datePanne = rs1.getTimestamp("datePanne");

                    Reparation reparation = getThisReparation(result, idAscenseur, datePanne);

                    Intervention intervention = new Intervention(reparation, rs1.getTimestamp("dateIntervention"));
                    result.add(intervention);
                }
            }
        }

        // get trajet
        try (Statement stmt2 = instance.getConnection().createStatement()) {
            try (ResultSet rs2 = stmt2.executeQuery("select * from reparation natural join trajetaller;")) {
                while (rs2.next()) {
                    int idAscenseur = rs2.getInt("idAscenseur");
                    Timestamp datePanne = rs2.getTimestamp("datePanne");

                    Reparation reparation = getThisReparation(result, idAscenseur, datePanne);

                    TrajetAller trajetAller = new TrajetAller(reparation, rs2.getTimestamp("dateTrajet"), rs2.getInt("dureeTrajet"));
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
            Reparation reparation = new Reparation(rs.getInt("idAscenceur"), rs.getDate("datePanne"),
                    typeReparation, typeReparation.duree);
            reparation.setLoginAscensoriste(rs.getString("login"));
            reparation.setCommentaire(rs.getString("commentaire"));
            reparation.setAvancement("avancement");
            result.add(reparation);
        }
        return result;
    }

    private Reparation getThisReparation(ArrayList<PlanningRessource> result, int idAscenseur, Timestamp datePanne) {
        return (Reparation) result.stream().filter(v -> {
                    if (v instanceof Reparation) {
                        return ((Reparation) v).getIdAscenseur() == idAscenseur &&
                                ((Reparation) v).getDatePanne().equals(datePanne);
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
            stmt.setTimestamp(2, new Timestamp(reparation.getDatePanne().getTime()));
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
                stmt.setTimestamp(1, new Timestamp(intervention.getDateIntervention().getTime()));
                stmt.executeUpdate();

                try (ResultSet rs1 = stmt.getGeneratedKeys()) {
                    // La rattacher à la panne
                    if (rs1.next()) {
                        int idIntervention = rs1.getInt(1);

                        try (PreparedStatement stmt2 = instance.getConnection()
                                .prepareStatement("update reparation set IdIntervention=? where idAscenseur=? and datePanne=?;")) {
                            stmt2.setInt(1, idIntervention);
                            stmt2.setInt(2, reparation.getIdAscenseur());
                            stmt2.setTimestamp(3, new Timestamp(reparation.getDatePanne().getTime()));
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
                    .prepareStatement("insert into trajetaller (dateTrajet, dureeTrajet) values(?,?);")) {
                stmt.setTimestamp(1, new Timestamp(trajet.getDateTrajet().getTime()));
                stmt.setInt(2, trajet.getDureeTrajet());
                stmt.executeUpdate();
            }

            // La rattacher à la panne
            try (PreparedStatement stmt1 = instance.getConnection()
                    .prepareStatement("select * from trajetaller where dateTrajet=? and dureeTrajet=?;")) {
                stmt1.setTimestamp(1, new Timestamp(trajet.getDateTrajet().getTime()));
                stmt1.setInt(2, trajet.getDureeTrajet());

                try (ResultSet rs1 = stmt1.executeQuery()) {
                    if (rs1.next()) {
                        int idTrajet = rs1.getInt("idTrajet");

                        try (PreparedStatement stmt2 = instance.getConnection()
                                .prepareStatement("update reparation set idTrajet=? where idAscenseur=? and datePanne=?;")) {
                            stmt2.setInt(1, idTrajet);
                            stmt2.setInt(2, reparation.getIdAscenseur());
                            stmt2.setTimestamp(3, new Timestamp(reparation.getDatePanne().getTime()));
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

    public void updateStatusReparation(Reparation reparation, String loginAscensoriste, String avancement) throws SQLException {
        try (PreparedStatement stmt = instance.getConnection()
                .prepareStatement("update Reparation set login=?, avancement=? where idAscenseur=? and datePanne=?;")) {
            stmt.setString(1, loginAscensoriste);
            stmt.setString(2, avancement);
            stmt.setInt(3, reparation.getIdAscenseur());
            stmt.setTimestamp(4, new Timestamp(reparation.getDatePanne().getTime()));
            stmt.executeUpdate();
        }
    }

}

