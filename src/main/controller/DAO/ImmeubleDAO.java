package main.controller.DAO;

import main.model.Adresse;
import main.model.Immeuble;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class  ImmeubleDAO {
    private final DataAccess instance;

    public ImmeubleDAO() {
        instance = DataAccess.getInstance();
    }

    public List<Immeuble> getAllImmeubles() throws SQLException {
        Statement stmt = instance.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from Immeuble natural join Adresse;");

        ArrayList<Immeuble> result = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            Immeuble immeuble = new Immeuble(rs.getString("nom"), rs.getInt("nbEtage"),
                    new Adresse(rs.getString("rue"), rs.getString("ville"), rs.getString("CP"), rs.getFloat("latitude"), rs.getFloat("longitude")));
            immeuble.setIdImmeuble(rs.getInt("IdImmeuble"));
            result.add(immeuble);
        }

        rs.close();
        stmt.close();

        return result;
    }

    private void insertAdresse(@NotNull Adresse adresse) throws SQLException {
        PreparedStatement stmtAdresse = instance.getConnection()
                .prepareStatement("insert into Adresse(rue,ville,CP,latitude,longitude) values(?,?,?,?,?)");
        stmtAdresse.setString(1, adresse.getRue());
        stmtAdresse.setString(2, adresse.getVille());
        stmtAdresse.setString(3, adresse.getCodePostal());
        stmtAdresse.setFloat(4, 0);// TODO get real latitude
        stmtAdresse.setFloat(5, 0);// TODO get real longitude

        stmtAdresse.executeUpdate();
        stmtAdresse.close();
    }

    private boolean isAdresseExistante(@NotNull Adresse adresse) throws SQLException {
        PreparedStatement stmtAdresse = instance.getConnection()
                .prepareStatement("select count(*) as count from Adresse where rue=? AND ville=? AND cp=?;");
        stmtAdresse.setString(1, adresse.getRue());
        stmtAdresse.setString(2, adresse.getVille());
        stmtAdresse.setString(3, adresse.getCodePostal());

        ResultSet rset = stmtAdresse.executeQuery();
        rset.next();
        int nbSameAdresse = rset.getInt("count");
        rset.close();
        stmtAdresse.close();
        return nbSameAdresse == 0;
    }

    public void addImmeuble(Immeuble immeuble) throws SQLException {
        // insertion adressse si inexistante
        Adresse immeubleAdresseAdress = immeuble.getAdresse();
        if (isAdresseExistante(immeubleAdresseAdress)) {
            insertAdresse(immeubleAdresseAdress);
        }

        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into immeuble(nom,nbEtage,rue,ville,login) values(?,?,?,?,?);");
        stmt.setString(1, immeuble.getNom().stripTrailing());
        stmt.setInt(2, immeuble.getNbEtage());
        stmt.setString(3, immeuble.getAdresse().getRue());
        stmt.setString(4, immeuble.getAdresse().getVille());
        stmt.setString(5, instance.getLogin());
        stmt.executeUpdate();
        stmt.close();
    }

    public void editImmeuble(int idImmeuble, Immeuble immeuble) throws SQLException {
        // insertion adressse si inexistante
        Adresse immeubleAdresseAdress = immeuble.getAdresse();
        if (isAdresseExistante(immeubleAdresseAdress)) {
            insertAdresse(immeubleAdresseAdress);
        }

        PreparedStatement stmt = instance.getConnection().prepareStatement(
                "update immeuble set nom=?,nbEtage=?,rue=?,ville=? where IdImmeuble=?;");
        stmt.setString(1, immeuble.getNom().stripTrailing());
        stmt.setInt(2, immeuble.getNbEtage());
        stmt.setString(3, immeuble.getAdresse().getRue());
        stmt.setString(4, immeuble.getAdresse().getVille());
        stmt.setInt(5, idImmeuble);
        stmt.executeUpdate();
        stmt.close();
    }

    public void removeImmeuble(int idImmeuble) throws SQLException {
        PreparedStatement stmt = instance.getConnection()
                .prepareStatement("delete from immeuble where IdImmeuble=?;");
        stmt.setInt(1, idImmeuble);
        stmt.executeUpdate();
        stmt.close();
    }
}
