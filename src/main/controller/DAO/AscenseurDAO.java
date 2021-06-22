package main.controller.DAO;

import main.model.Ascenseur;
import main.model.enums.EtatAscenseur;
import main.model.enums.TypeReparation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AscenseurDAO {
    private final DataAccess instance;

    public AscenseurDAO() {
        instance = DataAccess.getInstance();
    }

    public List<Ascenseur> getAllAscenseurs() throws SQLException {
        Statement stmt = instance.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from ascenseur;");

        ArrayList<Ascenseur> result = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            Ascenseur ascenseur = new Ascenseur(rs.getInt("idAscenseur"),rs.getString("marque"), rs.getString("modele"), rs.getDate("miseEnService"), rs.getInt("etage"), EtatAscenseur.get(rs.getString("etat")));
            result.add(ascenseur);
        }

        rs.close();
        stmt.close();

        return result;
    }

    public void addImmeuble(Ascenseur ascenseur, int idImmeuble) throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into ascenseur(idAscenseur,marque,modele,miseEnService,etat,etage,IdImmeuble) values(?,?,?,?,?,?,?);");
        stmt.setInt(1, ascenseur.getIdAscenseur());
        stmt.setString(2, ascenseur.getMarque());
        stmt.setString(3, ascenseur.getModele());
        stmt.setDate(4, (java.sql.Date) ascenseur.getDateMiseEnService());
        stmt.setString(5,  ascenseur.getState().toString());
        stmt.setInt(6, ascenseur.getEtage());
        stmt.setInt(7, idImmeuble);
        stmt.executeUpdate();
        stmt.close();
    }

    public void editAscenseur(Ascenseur ascenseur) throws SQLException {
        PreparedStatement stmt = instance.getConnection()
                .prepareStatement("update Ascenseur set marque=?, modele=?, miseEnService=?, etage=?, etat=? where idAscenseur=?;");
        stmt.setString(1, ascenseur.getMarque());
        stmt.setString(2, ascenseur.getModele());
        stmt.setDate(3, (java.sql.Date) ascenseur.getDateMiseEnService());
        stmt.setInt(4, ascenseur.getEtage());
        stmt.setString(5, ascenseur.getState().toString());
        stmt.executeUpdate();
        stmt.close();
    }

    public void removeAscenceur(int idAscenceur) throws SQLException {
        PreparedStatement stmt = instance.getConnection()
                .prepareStatement("delete from ascenseur where idAscenseur=?;");
        stmt.setInt(1, idAscenceur);
        stmt.executeUpdate();
        stmt.close();
    }
}
