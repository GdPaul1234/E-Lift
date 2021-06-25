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

    public List<Reparation> getAllReparation() throws SQLException {
        Statement stmt = instance.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from reparation;");

        ArrayList<Reparation> result = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            Reparation reparation = new Reparation(rs.getInt("idAscenceur"), rs.getString("login"), rs.getDate("dateReparation"), TypeReparation.get(rs.getString("typeReparation")), rs.getString("commentaire"),rs.getString("avancement"));
            result.add(reparation);
        }

        rs.close();
        stmt.close();

        return result;
    }

    public void addReparation(Reparation reparation, int idAscenseur, String login) throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into reparation(idAscenseur,login,dateReparation,commentaire,typeReparation,avancement) values(?,?,?,?,?,?);");
        stmt.setInt(1, reparation.getIdAscenseur());
        stmt.setString(2, reparation.getLogin());
        stmt.setDate(3, new java.sql.Date(reparation.getDateReparation().getTime()));
        stmt.setString(4, reparation.getCommentaire());
        stmt.setString(5,  reparation.getType().toString());
        stmt.setString(6, reparation.getAvancement());
        stmt.executeUpdate();
        stmt.close();
    }

    public void editReparation(int ascenseurID, String login, Reparation reparation) throws SQLException {
        PreparedStatement stmt = instance.getConnection()
                .prepareStatement("update Reparation set idAscenseur=?, login=?, dateReparation=?, commentaire=?, typeReparation=? where idAscenseur=? and login=?;");
        stmt.setInt(1, reparation.getIdAscenseur());
        stmt.setString(2, reparation.getLogin());
        stmt.setDate(3,  new java.sql.Date(reparation.getDateReparation().getTime()));
        stmt.setString(4, reparation.getCommentaire());
        stmt.setString(5, reparation.getType().toString());
        stmt.setString(6, reparation.getAvancement());
        stmt.executeUpdate();
        stmt.close();
    }

    public void removeReparation(int idAscenceur, String login) throws SQLException {
        PreparedStatement stmt = instance.getConnection()
                .prepareStatement("delete from reparation where idAscenseur=? and login=?;");
        stmt.setInt(1, idAscenceur);
        stmt.setString(2,login);
        stmt.executeUpdate();
        stmt.close();
    }
}

