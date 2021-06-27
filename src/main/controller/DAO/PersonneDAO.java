package main.controller.DAO;

import main.model.Gestionnaire;
import main.model.Personne;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PersonneDAO {
    private final DataAccess instance;

    public PersonneDAO() {
        instance = DataAccess.getInstance();
    }

    public Personne getThisPersone() throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement("select * from Personne where login=?");
        stmt.setString(1, DataAccess.getLogin());
        ResultSet rs = stmt.executeQuery();

        Personne result = null;

        if (rs.next()) {
            result = new Personne(rs.getString("nom"), rs.getString("prenom"), rs.getString("telephone"));
        }

        rs.close();
        stmt.close();

        return result;
    }
}
