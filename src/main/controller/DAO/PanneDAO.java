package main.controller.DAO;

import main.model.Adresse;
import main.model.Ascenseur;
import main.model.Immeuble;
import main.model.Panne;
import main.model.TrajetAller;
import main.model.enums.EtatAscenseur;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanneDAO {
    private final DataAccess instance;

    public PanneDAO() {
        instance = DataAccess.getInstance();
    }

    public List<Panne> getAllMyAscenseurs() throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement("select * from immeuble natural join ascenseur where login=?;");
        stmt.setString(1, DataAccess.getLogin());
        ResultSet rs = stmt.executeQuery();

        List<Panne> result = getPannes(rs);

        rs.close();
        stmt.close();

        return result;
    }

    public List<Panne> getAllAscenseurs() throws SQLException {
        Statement stmt = instance.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from immeuble natural join ascenseur;");

        List<Panne> result = getPannes(rs);

        rs.close();
        stmt.close();

        return result;
    }

    @NotNull
    private List<Panne> getPannes(ResultSet rs) throws SQLException {
        ArrayList<Panne> result = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            Ascenseur ascenseur = new Ascenseur(rs.getString("marque"), rs.getString("modele"), rs.getDate("miseEnService"), rs.getInt("etage"), EtatAscenseur.get(rs.getString("etat")));
            ascenseur.setIdAscenseur(rs.getInt("idAscenseur"));
            Immeuble immeuble = new Immeuble(rs.getString("nom"), rs.getInt("nbEtage"),
                    new Adresse(rs.getString("rue"), rs.getString("ville"), rs.getString("CP"), rs.getFloat("latitude"), rs.getFloat("longitude")));
            immeuble.setIdImmeuble(rs.getInt("IdImmeuble"));
            result.add(new Panne(immeuble, ascenseur));
        }
        return result;
    }
    public void addPanne(TrajetAller trajetAller, int idAscenseur, String login) throws SQLException {
        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into trajetaller(dateTrajet,dureeTrajet,destImmeuble) values(?,?,?);");
        stmt.setDate(1, new java.sql.Date(trajetAller.getDateTrajet().getTime()));
        stmt.setInt(2, trajetAller.getDureeTrajet());
        stmt.setInt(3, trajetAller.getDestImmeuble());
        stmt.executeUpdate();
        stmt.close();
    }
}
