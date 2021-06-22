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

    public List<Ascenseur> getAllAscenseur() throws SQLException {
        Statement stmt = instance.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from ascenseur;");

        ArrayList<Ascenseur> result = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            Ascenseur ascenseur = new Ascenseur(rs.getInt("idAscenseur"),rs.getString("marque"), rs.getString("modele"), rs.getDate("miseEnService"), rs.getInt("etage"), (EtatAscenseur.get(rs.getString("etat"))), (TypeReparation.get(rs.getString("typeReparation"))));
            result.add(ascenseur);
        }

        rs.close();
        stmt.close();

        return result;
    }

    public void addImmeuble(Ascenseur ascenseur) throws SQLException {
        instance.getConnection().setAutoCommit(false);

        Integer idAscenseur = ascenseur.getIdAscenseur();
        Integer etage = ascenseur.getEtage();
        String marque = ascenseur.getMarque();
        String modele = ascenseur.getModele();
        String state = ascenseur.getEtatAscenceur().toString();
        Date miseEnService = ascenseur.getDateMiseEnService();
        TypeReparation type = ascenseur.getTypeReparation();

        // ajouter personne dans BDD
        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into ascenseur(idAscenseur,marque,modele,miseEnService,etat,etage) values(?,?,?,?,?,?);");
        stmt.setInt(1, idAscenseur);
        stmt.setString(2, marque);
        stmt.setString(3, modele);
        stmt.setDate(4, (java.sql.Date) miseEnService);
        stmt.setString(5, state);
        stmt.setInt(6, etage);

        stmt.close();

        instance.getConnection().setAutoCommit(true);
    }

    public void editAscenseur(Ascenseur ascenseur) throws SQLException {
        // insertion vehicule
        PreparedStatement stmtVehicule = instance.getConnection()
                .prepareStatement("update Ascenseur set marque=?, modele=?, miseEnService=?, etage=?, etat=? where idAscenseur=?;");
        stmtVehicule.setString(1, ascenseur.getMarque());
        stmtVehicule.setString(2, ascenseur.getModele());
        stmtVehicule.setDate(3, (java.sql.Date) ascenseur.getDateMiseEnService());
        stmtVehicule.setInt(4, ascenseur.getEtage());
        stmtVehicule.setString(5, ascenseur.getEtatAscenceur().toString());
        stmtVehicule.executeUpdate();
        stmtVehicule.close();
    }

    public void removeAscenceur(int idAscenceur) throws SQLException {
        PreparedStatement stmtVehicule = instance.getConnection()
                .prepareStatement("delete from ascenseur where idAscenseur=?;");
        stmtVehicule.setInt(1, idAscenceur);
        stmtVehicule.executeUpdate();
        stmtVehicule.close();
    }
}
