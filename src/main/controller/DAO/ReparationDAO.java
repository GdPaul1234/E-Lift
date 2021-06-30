package main.controller.DAO;

import main.model.Reparation;
import main.model.enums.TypeReparation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into reparation(idAscenseur,datePanne,typeReparation) values(?,?,?);");
        stmt.setInt(1, idAscenseur);
        stmt.setDate(2, new java.sql.Date(reparation.getDatePanne().getTime()));
        stmt.setString(3, reparation.getType().toString());
        stmt.executeUpdate();
        stmt.close();
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
            PreparedStatement stmt = instance.getConnection().prepareStatement("insert into intervention (dateIntervention) values(?);");
            stmt.setDate(1, new java.sql.Date(intervention.getDateIntervention().getTime()));
            stmt.executeUpdate();
            stmt.close();

            // La rattacher à la panne
            Statement stmt1 = instance.getConnection().createStatement();
            ResultSet rs1 = stmt1.executeQuery("SELECT LAST_INSERT_ID();");

            if (rs1.next()) {
                int idIntervention = rs1.getInt("idIntervention");

                PreparedStatement stmt2 = instance.getConnection().prepareStatement("update reparation set IdIntervention=? where idAscenseur=? and datePanne=?;");
                stmt2.setInt(1, idIntervention);
                stmt2.setInt(2, reparation.getIdAscenseur());
                stmt2.setDate(3, new java.sql.Date(reparation.getDatePanne().getTime()));
                stmt2.executeUpdate();
                stmt2.close();
            }
            rs1.close();
            stmt1.close();

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
            PreparedStatement stmt = instance.getConnection().prepareStatement("insert into trajetaller (dateTrajet, dureeTrajet) values(?,?);");
            stmt.setDate(1, new java.sql.Date(trajet.getDateTrajet().getTime()));
            stmt.setInt(2, trajet.getDureeTrajet());
            stmt.executeUpdate();
            stmt.close();

            // La rattacher à la panne
            PreparedStatement stmt1 = instance.getConnection().prepareStatement("select * from trajetaller where dateTrajet=? and dureeTrajet=?;");
            stmt1.setDate(1, new java.sql.Date(trajet.getDateTrajet().getTime()));
            stmt1.setInt(2, trajet.getDureeTrajet());
            ResultSet rs1 = stmt1.executeQuery();

            if (rs1.next()) {
                int idTrajet = rs1.getInt("idTrajet");

                PreparedStatement stmt2 = instance.getConnection().prepareStatement("update reparation set idTrajet=? where idAscenseur=? and datePanne=?;");
                stmt2.setInt(1, idTrajet);
                stmt2.setInt(2, reparation.getIdAscenseur());
                stmt2.setDate(3, new java.sql.Date(reparation.getDatePanne().getTime()));
                stmt2.executeUpdate();
                stmt2.close();
            }
            rs1.close();
            stmt1.close();

            instance.getConnection().commit();

        } catch (SQLException throwables) {
            instance.getConnection().rollback();
            instance.getConnection().setAutoCommit(true);
            throw throwables;
        }

        instance.getConnection().setAutoCommit(true);
    }

    public void updateStatusReparation(Reparation reparation, String loginAscensoriste, String avancement) throws SQLException {
        PreparedStatement stmt = instance.getConnection()
                .prepareStatement("update Reparation set login=?, avancement=? where idAscenseur=? and datePanne=?;");
        stmt.setString(1, loginAscensoriste);
        stmt.setString(2, avancement);
        stmt.setInt(3, reparation.getIdAscenseur());
        stmt.setDate(4, new java.sql.Date(reparation.getDatePanne().getTime()));
        stmt.executeUpdate();
        stmt.close();
    }

}

