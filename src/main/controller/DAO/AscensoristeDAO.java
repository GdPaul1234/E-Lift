package main.controller.DAO;

import main.model.Ascensoriste;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AscensoristeDAO {
    private final DataAccess instance;

    public AscensoristeDAO() {
        instance = DataAccess.getInstance();
    }

    public List<Ascensoriste> getAllAscensoristes() throws SQLException {
        Statement stmt = instance.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from Personne natural join Ascensoriste order by nom, prenom;");

        ArrayList<Ascensoriste> result = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            Ascensoriste ascensoriste = new Ascensoriste(rs.getString("nom"), rs.getString("prenom"),
                    rs.getString("telephone"), rs.getFloat("longitude"), rs.getFloat("latitude"));
            ascensoriste.setLogin(rs.getString("login"));
            result.add(ascensoriste);
        }

        rs.close();
        stmt.close();

        return result;
    }

    public String getLoginBuilder(String nom, String prenom) {
        StringBuilder newLogin = new StringBuilder();
        newLogin.append(prenom.toLowerCase());
        newLogin.append('.');
        newLogin.append(nom.toLowerCase());
        {
            Random value = new Random();
            newLogin.append(String.format("%03d", value.nextInt(999)));
        }
        newLogin.append("@e-lift.fr");
        return newLogin.toString();
    }

    public void addAscensoriste(Ascensoriste ascensoriste, String newLogin, String password) throws SQLException {
        instance.getConnection().setAutoCommit(false);

        String nom = ascensoriste.getNom().stripTrailing();
        String prenom = ascensoriste.getPrenom().stripTrailing();

        // ajouter personne dans BDD
        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into Personne(nom,prenom,telephone,login) values(?,?,?,?);");
        stmt.setString(1, nom);
        stmt.setString(2, prenom);
        stmt.setString(3, ascensoriste.getTelephone());

        ascensoriste.setLogin(newLogin);

        stmt.setString(4, ascensoriste.getLogin());
        stmt.executeUpdate();

        // ajouter personne dans liste ascensoristes
        stmt = instance.getConnection().prepareStatement(
                "select login from Personne where nom=? and prenom=? and telephone=?");
        stmt.setString(1, ascensoriste.getNom());
        stmt.setString(2, ascensoriste.getPrenom());
        stmt.setString(3, ascensoriste.getTelephone());
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            ascensoriste.setLogin(rs.getString("login"));
            stmt = instance.getConnection().prepareStatement("insert into Ascensoriste(login) values(?)");
            stmt.setString(1, rs.getString("login"));
            stmt.executeUpdate();

            // ajoute employe dans liste user
            stmt = instance.getConnection().prepareStatement("create user ? IDENTIFIED BY ?  default role `e-lift_employe`;");
            stmt.setString(1, ascensoriste.getLogin());
            stmt.setString(2, password);
            stmt.executeUpdate();

        }

        stmt.close();

        instance.getConnection().setAutoCommit(true);
    }

    public void editAscensoriste(String login, Ascensoriste ascensoriste) throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement("update Personne set nom=?, prenom=?, telephone=? where login=?;");
        stmt.setString(1, ascensoriste.getNom());
        stmt.setString(2, ascensoriste.getPrenom());
        stmt.setString(3, ascensoriste.getTelephone());
        stmt.setString(4, login);

        stmt.executeUpdate();
        stmt.close();
    }

    public void deleteAscensoriste(String login) throws SQLException {
        String[] queries = { "delete from Ascensoriste where login=?;", "drop user if exists ?;" };

        for (String query : queries) {
            PreparedStatement stmt = instance.getConnection().prepareStatement(query);
            stmt.setString(1, login);
            stmt.executeUpdate();
            stmt.close();
        }
    }

    public boolean isAscensoriste(String login) throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement("select login from Ascensoriste where login=?;");
        stmt.setString(1, login);
        ResultSet rs = stmt.executeQuery();

        boolean result = false;
        if(rs.next()) result = rs.getString("login").equals(login);

        rs.close();
        stmt.close();

        return result;
    }

    public List<Ascensoriste> getAvailableAscensoriste(int latitude, int longitude) throws SQLException {

        PreparedStatement stmt = instance.getConnection()
                .prepareStatement("select *, distance(latitude, longitude, ?, ?) " +
                        "as distance from ascensoriste where not exists (select login from reparation where " +
                        "ascensoriste.login = reparation.login) order by distance;");

        stmt.setInt(1, latitude);
        stmt.setInt(2, longitude);

        ResultSet rs = stmt.executeQuery();

        ArrayList<Ascensoriste> result = new ArrayList<>(rs.getFetchSize());

        while (rs.next()) {
            Ascensoriste ascensoriste = new Ascensoriste(rs.getString("nom"), rs.getString("prenom"),
                    rs.getString("telephone"), rs.getInt("longitude"), rs.getInt("latitude"));
            result.add(ascensoriste);
        }

        rs.close();
        stmt.close();

        return result;
    }
}
