package main.controller.DAO;

import main.model.Gestionnaire;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GestionnaireDAO {
    private final DataAccess instance;

    public GestionnaireDAO() {
        instance = DataAccess.getInstance();
    }

    public List<Gestionnaire> getAllGestionnaires() throws SQLException {
        Statement stmt = instance.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from Personne natural join Gestionnaire order by nom, prenom;");

        ArrayList<Gestionnaire> result = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            Gestionnaire gestionnaire = new Gestionnaire(rs.getString("nom"), rs.getString("prenom"),
                    rs.getString("telephone"));
            gestionnaire.setLogin(rs.getString("login"));
            result.add(gestionnaire);
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
        return newLogin.toString();
    }

    public void addGestionnaire(Gestionnaire gestionnaire, String newLogin, String password) throws SQLException {
        instance.getConnection().setAutoCommit(false);

        String nom = gestionnaire.getNom().stripTrailing();
        String prenom = gestionnaire.getPrenom().stripTrailing();

        // ajouter personne dans BDD
        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into Personne(nom,prenom,telephone,login) values(?,?,?,?);");
        stmt.setString(1, nom);
        stmt.setString(2, prenom);
        stmt.setString(3, gestionnaire.getTelephone());

        gestionnaire.setLogin(newLogin);

        stmt.setString(4, newLogin);
        stmt.executeUpdate();

        // ajouter personne dans liste gestionnaires
        stmt = instance.getConnection().prepareStatement(
                "select login from Personne where nom=? and prenom=? and telephone=?");
        stmt.setString(1, gestionnaire.getNom());
        stmt.setString(2, gestionnaire.getPrenom());
        stmt.setString(3, gestionnaire.getTelephone());
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            gestionnaire.setLogin(rs.getString("login"));
            stmt = instance.getConnection().prepareStatement("insert into Gestionnaire(login) values(?)");
            stmt.setString(1, rs.getString("login"));
            stmt.executeUpdate();

            // ajoute employe dans liste user
            stmt = instance.getConnection().prepareStatement("create user ? IDENTIFIED BY ?  default role `e-lift_gestionnaire`;");
            stmt.setString(1, gestionnaire.getLogin());
            stmt.setString(2, password);
            stmt.executeUpdate();

        }

        stmt.close();

        instance.getConnection().setAutoCommit(true);
    }

    public void editGestionnaire(String login, Gestionnaire gestionnaire) throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement("update Personne set nom=?, prenom=?, telephone=? where login=?;");
        stmt.setString(1, gestionnaire.getNom());
        stmt.setString(2, gestionnaire.getPrenom());
        stmt.setString(3, gestionnaire.getTelephone());
        stmt.setString(4, login);

        stmt.executeUpdate();
        stmt.close();
    }

    public void deleteGestionnaire(String login) throws SQLException {
        String[] queries = { "delete from Gestionnaire where login=?;", "drop user if exists ?;" };

        for (String query : queries) {
            PreparedStatement stmt = instance.getConnection().prepareStatement(query);
            stmt.setString(1, login);
            stmt.executeUpdate();
            stmt.close();
        }
    }

    public boolean isGestionnaire(String login) throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement("select login from Gestionnaire where login=?;");
        stmt.setString(1, login);
        ResultSet rs = stmt.executeQuery();

        boolean result = false;
        if(rs.next()) result = rs.getString("login").equals(login);

        rs.close();
        stmt.close();

        return result;
    }


}
