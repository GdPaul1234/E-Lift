package main.controller.DAO;

import javafx.util.Pair;
import  main.model.*;
import  main.model.enums.EtatAscenseur;


import java.sql.*;
import java.sql.Date;
import java.util.*;

public class AscensoristeDAO {
    private final DataAccess instance;

    public AscensoristeDAO() {
        instance = DataAccess.getInstance();
    }

    /**
     *
     * @return
     * @throws SQLException
     */
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

    /**
     *
     * @param nom
     * @param prenom
     * @return
     */
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

    /**
     *
     * @param ascensoriste
     * @param newLogin
     * @param password
     * @throws SQLException
     */
    public void addAscensoriste(Ascensoriste ascensoriste, String newLogin, String password) throws SQLException {
        instance.getConnection().setAutoCommit(false);

        String nom = ascensoriste.getNom().stripTrailing();
        String prenom = ascensoriste.getPrenom().stripTrailing();

        // ajouter personne dans BDD
        try (PreparedStatement stmt = instance.getConnection().prepareStatement(
                "insert into Personne(nom,prenom,telephone,login) values(?,?,?,?);")) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, ascensoriste.getTelephone());

            ascensoriste.setLogin(newLogin);

            stmt.setString(4, ascensoriste.getLogin());
            stmt.executeUpdate();
        }

        // ajouter personne dans liste ascensoristes
        try (PreparedStatement stmt1 = instance.getConnection().prepareStatement(
                "select login from Personne where nom=? and prenom=? and telephone=?")) {
            stmt1.setString(1, ascensoriste.getNom());
            stmt1.setString(2, ascensoriste.getPrenom());
            stmt1.setString(3, ascensoriste.getTelephone());
            try (ResultSet rs = stmt1.executeQuery()) {

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
            }

        }

        instance.getConnection().setAutoCommit(true);
    }

    /**
     *
     * @param login
     * @param ascensoriste
     * @throws SQLException
     */
    public void editAscensoriste(String login, Ascensoriste ascensoriste) throws SQLException {
        try (PreparedStatement stmt = instance.getConnection().prepareStatement("update Personne set nom=?, prenom=?, telephone=? where login=?;")) {
            stmt.setString(1, ascensoriste.getNom());
            stmt.setString(2, ascensoriste.getPrenom());
            stmt.setString(3, ascensoriste.getTelephone());
            stmt.setString(4, login);

            stmt.executeUpdate();
        }
    }

    public void deleteAscensoriste(String login) throws SQLException {
        String[] queries = { "delete from Ascensoriste where login=?;", "drop user if exists ?;" };

        for (String query : queries) {
            try (PreparedStatement stmt = instance.getConnection().prepareStatement(query)) {
                stmt.setString(1, login);
                stmt.executeUpdate();
            }
        }
    }

    /**
     *
     * @param login
     * @return
     * @throws SQLException
     */
    public boolean isAscensoriste(String login) throws SQLException {
        boolean result;
        try (PreparedStatement stmt = instance.getConnection().prepareStatement("select login from Ascensoriste where login=?;")) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {

                result = false;
                if (rs.next()) result = rs.getString("login").equals(login);

            }
        }

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
        Ascensoriste ascensoriste = null;

        // Obtenir les informations concernant une réparation
        try (PreparedStatement stmt = instance.getConnection()
                .prepareStatement("select * from reparation as r natural join ascenseur" +
                        " join immeuble using(IdImmeuble) natural join adresse " +
                        "where r.idAscenseur=? -- and r.datePanne=?;")) {
            stmt.setInt(1, reparation.getAscenseur().getIdAscenseur());
            //stmt.setTimestamp(2, new Timestamp(reparation.getDatePanne().getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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

                        // Obtenir ascensoriste ayant le moins de travail le plus proche
                        try (PreparedStatement stmt2 = instance.getConnection()
                                .prepareStatement("select *, distance(latitude,longitude,?,?) as distance " +
                                        "from personne natural join ascensoriste " +
                                        "where exists(select login from ascensoriste natural left join reparation " +
                                        "natural left join intervention natural left join trajetaller " +
                                        "group by login order by count(login)) " +
                                        "order by distance limit 1;")) {
                            Adresse adresse = immeuble.getAdresse();
                            stmt2.setFloat(1, adresse.getLatitude());
                            stmt2.setFloat(2, adresse.getLongitude());
                            try (ResultSet rs2 = stmt2.executeQuery()) {
                                if (rs2.next()) {
                                    ascensoriste = new Ascensoriste(rs2.getString("nom"), rs2.getString("prenom"),
                                            rs2.getString("telephone"), rs2.getFloat("latitude"), rs2.getFloat("longitude"));
                                    ascensoriste.setLogin(rs2.getString("login"));
                                }

                            }
                        }
                    }

                }

            }
        }

        return ascensoriste;
    }

    /**
     *
     * @param loginAscensoriste
     * @return
     * @throws SQLException
     */
    public Pair<java.util.Date, Adresse> getDateLieuLastReparation(String loginAscensoriste) throws SQLException {
        // get last trajet
        Date lastTrajetDate = null;
        int dureeTrajet = 0;
        int duree = 0;
        Adresse adresse = null;

        try(PreparedStatement stmt = instance.getConnection()
                .prepareStatement("select * from reparation natural join ascenseur " +
                        "join immeuble using(IdImmeuble) natural join adresse left join trajetaller using(idTrajet) " +
                        "where reparation.login=? order by dateTrajet limit 1")) {
            stmt.setString(1, loginAscensoriste);
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    lastTrajetDate = new Date(rs.getTimestamp("dateTrajet").getTime());
                    dureeTrajet = rs.getInt("dureeTrajet");
                    duree = rs.getInt("duree");
                    adresse = new Adresse(rs.getString("rue"), rs.getString("ville"), rs.getString("CP"),
                            rs.getFloat("latitude"), rs.getFloat("longitude"));
                }
            }
        }

        java.util.Date dateFin = null;
        if(lastTrajetDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastTrajetDate);
            calendar.add(Calendar.MINUTE, duree + dureeTrajet);
            dateFin = calendar.getTime();
        }

        return new Pair<>(dateFin, adresse);
    }

    /**
     *
     * @param idAscenseur
     * @param reparation
     * @throws SQLException
     */
    public void moveAscensoriste(int idAscenseur, Reparation reparation) throws SQLException {
        AscenseurDAO ascenseurDAO = new AscenseurDAO();
        ReparationDAO reparationDAO = new ReparationDAO();

        Ascensoriste ascensoriste = findAvailableAscensoriste(reparation);
        reparation.setLoginAscensoriste(ascensoriste.getLogin());

        // Obtenir info pour planifier le trajet
        Pair<java.util.Date, Adresse> dateAdresseLastTask = getDateLieuLastReparation(ascensoriste.getLogin());
        Adresse adresseLastIntervention = dateAdresseLastTask.getValue();
        Adresse adresseIntervention = ascenseurDAO.getImmeubleAscenseur(idAscenseur).getAdresse();

        float distance = 0;
        int tempsTrajet = 0;
        java.util.Date debutTrajet = new java.util.Date();

        // Si première intervention ascensoriste, prendre sa position
        if(adresseLastIntervention == null) {
            adresseLastIntervention = new Adresse("","","",
                    ascensoriste.getLatitude(), ascensoriste.getLongitude());
        } else {
            debutTrajet = dateAdresseLastTask.getKey();
        }

        distance = getDistance(adresseLastIntervention, adresseIntervention);
        /*
         * Pour le moment, on suppose qu'un trajet prend 5% de plus de distance qu'en vol d'oiseau
         * et que le véhicule d'intervention roule à 45 km/h en moyenne.
         */
        distance += 0.05*distance; // en km
        tempsTrajet = Double.valueOf(Math.floor(distance / (45.0/60.0))).intValue();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(debutTrajet);
        calendar.add(Calendar.MINUTE, 5);
        debutTrajet = calendar.getTime();


        // Planification du trajet
        TrajetAller trajet = new TrajetAller(reparation, debutTrajet, tempsTrajet);
        reparationDAO.attachTrajet(reparation, trajet);

        // Planification de l'intervention
        Ascenseur ascenseur = ascenseurDAO.getAscenseur(idAscenseur);

        // On suppose qu'un ascensoriste met 1 minute pour monter 1 étage à pied
        calendar = Calendar.getInstance();
        calendar.setTime(debutTrajet);
        calendar.add(Calendar.MINUTE, tempsTrajet + ascenseur.getEtage());
        java.util.Date dateIntervention = calendar.getTime();

        Intervention intervention = new Intervention(reparation, dateIntervention);
        reparationDAO.attachIntervention(reparation, intervention);
    }

    /**
     *
     * @param adresseLastIntervention
     * @param adresseIntervention
     * @return
     * @throws SQLException
     */
    private float getDistance(Adresse adresseLastIntervention, Adresse adresseIntervention) throws SQLException {
        float distance = -1;

        // Calculer durée trajet
        try(PreparedStatement stmt = instance.getConnection()
                .prepareStatement("select distance(?,?,?,?) as distance;")) {
            stmt.setFloat(1, adresseLastIntervention.getLatitude());
            stmt.setFloat(2, adresseLastIntervention.getLongitude());
            stmt.setFloat(3, adresseIntervention.getLatitude());
            stmt.setFloat(4, adresseIntervention.getLongitude());

            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    distance = rs.getFloat("distance");
                }
            }
        }

        return distance;
    }

}
