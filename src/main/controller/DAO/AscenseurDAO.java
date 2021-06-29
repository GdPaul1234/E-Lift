package main.controller.DAO;

import main.model.Ascenseur;
import main.model.enums.EtatAscenseur;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AscenseurDAO {
    private final DataAccess instance;

    public AscenseurDAO() {
        instance = DataAccess.getInstance();
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
        // Verifier nbEtage avant ajouter ascenseur
        PreparedStatement stmt = instance.getConnection().prepareStatement("select nbEtage from immeuble where IdImmeuble=?");
        stmt.setInt(1, idImmeuble);
        ResultSet rs = stmt.executeQuery();

        if(rs.next()) {
            int nNbEtageImmeuble = rs.getInt("nbEtage");

            if(ascenseur.getEtage() <= nNbEtageImmeuble) {
                stmt = instance.getConnection().prepareStatement(
                        "insert into ascenseur(idAscenseur,marque,modele,miseEnService,etat,etage,IdImmeuble) values(?,?,?,?,?,?,?);");
                stmt.setInt(1, ascenseur.getIdAscenseur());
                stmt.setString(2, ascenseur.getMarque());
                stmt.setString(3, ascenseur.getModele());
                stmt.setDate(4, new Date(ascenseur.getDateMiseEnService().getTime()));
                stmt.setString(5, ascenseur.getState().toString());
                stmt.setInt(6, ascenseur.getEtage());
                stmt.setInt(7, idImmeuble);
                stmt.executeUpdate();
            } else {
                rs.close();
                stmt.close();
                throw new SQLException("Constraint failed : etage > etageImmeuble !");
            }
        }

        rs.close();
        stmt.close();

    }

    public void editAscenseur(int ascenseurID, Ascenseur ascenseur) throws SQLException {
        // Verifier nbEtage avant modifier ascenseur
        PreparedStatement stmt = instance.getConnection().prepareStatement("select nbEtage from immeuble natural join ascenseur where idAscenseur=?");
        stmt.setInt(1, ascenseurID);
        ResultSet rs = stmt.executeQuery();

        if(rs.next()) {
            int nNbEtageImmeuble = rs.getInt("nbEtage");

            if(ascenseur.getEtage() <= nNbEtageImmeuble) {
                stmt = instance.getConnection()
                        .prepareStatement("update Ascenseur set marque=?, modele=?, miseEnService=?, etage=?, etat=? where idAscenseur=?;");
                stmt.setString(1, ascenseur.getMarque());
                stmt.setString(2, ascenseur.getModele());
                stmt.setDate(3,  new java.sql.Date(ascenseur.getDateMiseEnService().getTime()));
                stmt.setInt(4, ascenseur.getEtage());
                stmt.setString(5, ascenseur.getState().toString());
                stmt.setInt(6, ascenseurID);
                stmt.executeUpdate();
            } else {
                rs.close();
                stmt.close();
                throw new SQLException("Constraint failed : etage > etageImmeuble !");
            }
        }

        rs.close();
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
