package main.controller.DAO;

import main.model.Reparation;
import main.model.enums.TypeReparation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReparationDAO {
    private final DataAccess instance;

    public ReparationDAO() {
        instance = DataAccess.getInstance();
    }

    public List<Reparation> getAllReparations() throws SQLException {
        Statement stmt = instance.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from reparation;");

        ArrayList<Reparation> result = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            Reparation reparation = new Reparation(rs.getInt("idAscenceur"), rs.getDate("datePanne"), TypeReparation.get(rs.getString("typeReparation")));
            reparation.setLoginAscensoriste(rs.getString("login"));
            reparation.setCommentaire(rs.getString("commentaire"));
            reparation.setAvancement("avancement");
            result.add(reparation);
        }

        rs.close();
        stmt.close();

        return result;
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
     *
     * @param reparation
     * @param intervention
     * @throws SQLException
     */
    public void attachIntervention(Reparation reparation, Reparation.Intervention intervention) throws SQLException {
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
     *
     * @param reparation
     * @param trajet
     * @throws SQLException
     */
    public void attachTrajet(Reparation reparation, Reparation.TrajetAller trajet) throws SQLException {
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

