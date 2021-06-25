package main.controller.DAO;

import main.model.Ascenseur;
import main.model.Ascensoriste;
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
            Ascenseur ascenseur = new Ascenseur(rs.getString("marque"), rs.getString("modele"), rs.getDate("miseEnService"), rs.getInt("etage"), EtatAscenseur.get(rs.getString("etat")));
            ascenseur.setIdAscenseur(rs.getInt("idAscenseur"));
            result.add(ascenseur);
        }

        rs.close();
        stmt.close();

        return result;
    }

    public List<Ascenseur> getAscenseursImmeuble(int idImmeuble) throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement("select * from ascenseur where IdImmeuble=?;");
        stmt.setInt(1, idImmeuble);
        ResultSet rs = stmt.executeQuery();

        ArrayList<Ascenseur> result = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            Ascenseur ascenseur = new Ascenseur(rs.getString("marque"), rs.getString("modele"),
                    rs.getDate("miseEnService"), rs.getInt("etage"),
                    EtatAscenseur.get(rs.getString("etat")));
            ascenseur.setIdAscenseur(rs.getInt("idAscenseur"));
            result.add(ascenseur);
        }

        rs.close();
        stmt.close();

        return result;
    }

    public void addAscenseur(Ascenseur ascenseur, int idImmeuble) throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into ascenseur(idAscenseur,marque,modele,miseEnService,etat,etage,IdImmeuble) values(?,?,?,?,?,?,?);");
        stmt.setInt(1, ascenseur.getIdAscenseur());
        stmt.setString(2, ascenseur.getMarque());
        stmt.setString(3, ascenseur.getModele());
        stmt.setDate(4, new java.sql.Date(ascenseur.getDateMiseEnService().getTime()));
        stmt.setString(5,  ascenseur.getState().toString());
        stmt.setInt(6, ascenseur.getEtage());
        stmt.setInt(7, idImmeuble);
        stmt.executeUpdate();
        stmt.close();
    }

    public void editAscenseur(int ascenseurID, Ascenseur ascenseur) throws SQLException {
        PreparedStatement stmt = instance.getConnection()
                .prepareStatement("update Ascenseur set marque=?, modele=?, miseEnService=?, etage=?, etat=? where idAscenseur=?;");
        stmt.setString(1, ascenseur.getMarque());
        stmt.setString(2, ascenseur.getModele());
        stmt.setDate(3,  new java.sql.Date(ascenseur.getDateMiseEnService().getTime()));
        stmt.setInt(4, ascenseur.getEtage());
        stmt.setString(5, ascenseur.getState().toString());
        stmt.setInt(6, ascenseurID);
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

    public List<Ascensoriste> getAvailableAscensoriste(int latitude, int longitude) throws SQLException {

        PreparedStatement stmt = instance.getConnection()
                .prepareStatement("select *, distance(latitude, longitude, ?, ?) " +
                        "as distance from ascensoriste order by distance;");

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

        return null;
    }
}
