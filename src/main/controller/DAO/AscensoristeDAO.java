package main.controller.DAO;

import  main.model.*;
import  main.model.enums.EtatAscenseur;


import java.sql.*;
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
                    rs.getString("telephone"), rs.getFloat("latitude"), rs.getFloat("longitude"));
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
        stmt.close();

        // ajouter personne dans liste ascensoristes
        PreparedStatement stmt1 = instance.getConnection().prepareStatement(
                "select login from Personne where nom=? and prenom=? and telephone=?");
        stmt1.setString(1, ascensoriste.getNom());
        stmt1.setString(2, ascensoriste.getPrenom());
        stmt1.setString(3, ascensoriste.getTelephone());
        ResultSet rs = stmt1.executeQuery();

        if (rs.next()) {
            ascensoriste.setLogin(rs.getString("login"));
            PreparedStatement stmt2 = instance.getConnection().prepareStatement("insert into Ascensoriste(login) values(?)");
            stmt2.setString(1, rs.getString("login"));
            stmt2.executeUpdate();
            stmt2.close();

            // ajoute employe dans liste user
            PreparedStatement stmt3 = instance.getConnection().prepareStatement("create user ? IDENTIFIED BY ?  default role `e-lift_employe`;");
            stmt3.setString(1, ascensoriste.getLogin());
            stmt3.setString(2, password);
            stmt3.executeUpdate();
            stmt3.close();
        }

        stmt1.close();

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

    /**
     * Find the first ascensoriste <b>available</b> order by <b>distance</b>
     * <p>
     * If not, find one ascensoriste who have the <b>least of work</b>
     *
     * @param reparation
     * @return
     * @throws SQLException
     */
    public Ascensoriste findAvailableAscensoriste(Reparation reparation) throws SQLException {
        // Obtenir les informations concernant une réparation
        PreparedStatement stmt = instance.getConnection()
                .prepareStatement("select * from reparation as r natural join ascenseur natural join immeuble " +
                        "where r.idAscenseur=? and r.datePanne=?;");
        stmt.setInt(1, reparation.getAscenseur().getIdAscenseur());
        stmt.setTimestamp(2, new Timestamp(reparation.getDatePanne().getTime()));
        ResultSet rs = stmt.executeQuery();

        Ascensoriste ascensoriste = null;

        if(rs.next()) {
            Ascenseur ascenseur = new Ascenseur(rs.getString("marque"), rs.getString("modele"),
                    rs.getDate("miseEnService"), rs.getInt("etage"),
                    EtatAscenseur.get(rs.getString("etat")));
            ascenseur.setIdAscenseur(rs.getInt("idAscenseur"));

            // Chercher ascensoriste uniquement si on en a besoin
            EtatAscenseur etatAscenseur = ascenseur.getState();
            if (etatAscenseur != EtatAscenseur.EnCoursDeReparation && etatAscenseur != EtatAscenseur.EnService) {
                Immeuble immeuble = new Immeuble(rs.getString("nom"), rs.getInt("nbEtage"),
                        new Adresse(rs.getString("rue"), rs.getString("ville"), rs.getString("CP"), rs.getFloat("latitude"), rs.getFloat("longitude")));
                immeuble.setIdImmeuble(rs.getInt("IdImmeuble"));

                // Obtenir ascensoriste disponible les plus proche
                PreparedStatement stmt1 = instance.getConnection()
                        .prepareStatement("select *, distance(latitude,longitude,?,?) as distance from ascensoriste where not exists( " +
                                "select login from ascensoriste natural join intervention natural join reparation natural join trajetaller " +
                                "where now() between dateIntervention and date_add(dateIntervention,INTERVAL duree MINUTE) " +
                                "or now() between dateTrajet and date_add(dateTrajet, INTERVAL  dureeTrajet MINUTE )) " +
                                "order by distance limit 1;");
                stmt1.setFloat(1, immeuble.getAdresse().getLatitude());
                stmt1.setFloat(2, immeuble.getAdresse().getLongitude());
                ResultSet rs1 = stmt1.executeQuery();
                if(rs1.next()) {
                    ascensoriste = new Ascensoriste(rs.getString("nom"), rs.getString("prenom"),
                            rs.getString("telephone"), rs.getFloat("latitude"), rs.getFloat("longitude"));
                    ascensoriste.setLogin(rs.getString("login"));
                } else {
                    // sinon, rechercher les ascensoristes les plus proches ayant le moins de travail
                    PreparedStatement stmt2 = instance.getConnection()
                            .prepareStatement("select *, distance(latitude,longitude,?,?) as distance from ascensoriste where exists( " +
                                    "select login from ascensoriste natural left join intervention natural left join trajetaller " +
                                    "group by login order by count(login)) " +
                                    "order by distance limit 1;");
                    ResultSet rs2 = stmt2.executeQuery();
                    if(rs2.next()) {
                        ascensoriste = new Ascensoriste(rs.getString("nom"), rs.getString("prenom"),
                                rs.getString("telephone"), rs.getFloat("latitude"), rs.getFloat("longitude"));
                        ascensoriste.setLogin(rs.getString("login"));
                    }
                    rs2.close();
                }

                rs1.close();
                stmt1.close();
            }

        }

        rs.close();
        stmt.close();

        return ascensoriste;
    }
}
