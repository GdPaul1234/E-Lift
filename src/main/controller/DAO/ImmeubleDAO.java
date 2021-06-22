package main.controller.DAO;

import main.model.Immeuble;
import main.model.Adresse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ImmeubleDAO {
    private final DataAccess instance;

    public ImmeubleDAO() {
        instance = DataAccess.getInstance();
    }

    public List<Immeuble> getAllImmeuble() throws SQLException {
        Statement stmt = instance.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from Immeuble;");

        ArrayList<Immeuble> result = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            Immeuble immeuble = new Immeuble(rs.getString("nom"),rs.getInt("IdImmeuble"), rs.getInt("nbEtage"));
            new Adresse(rs.getString("rue"), rs.getString("ville"), rs.getString("CP"),rs.getFloat("longitude"), rs.getFloat("lattitude"));
            result.add(immeuble);
        }

        rs.close();
        stmt.close();

        return result;
    }

    public void addImmeuble(Immeuble immeuble) throws SQLException {
        instance.getConnection().setAutoCommit(false);

        String nom = immeuble.getNom().stripTrailing();
        Integer nbEtage = immeuble.getNbEtage();
        Integer idImmeuble = immeuble.getIdImmeuble();

        // ajouter personne dans BDD
        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into immeuble(nom,IdImmeuble,nbEtage) values(?,?,?);");
        stmt.setString(1, nom);
        stmt.setInt(2, nbEtage);
        stmt.setInt(3, idImmeuble);


        stmt.close();

        instance.getConnection().setAutoCommit(true);
    }
}
