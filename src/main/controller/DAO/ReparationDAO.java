package main.controller.DAO;

import main.model.*;
import main.model.enums.EtatAscenseur;
import main.model.enums.TypeReparation;
import main.model.interfaces.PlanningRessource;
import main.model.interfaces.PlanningRessourceComparator;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReparationDAO {
    private final DataAccess instance;

    public ReparationDAO() {
        instance = DataAccess.getInstance();
    }

    public List<PlanningRessource> getMyPlanning() throws SQLException, ParseException {
        List<PlanningRessource> result;

        // get available reparations according to our role
        if (DataAccess.isAscensoriste()) {
            try (PreparedStatement stmt = instance.getConnection()
                    .prepareStatement("select * from immeuble natural join adresse natural join ascenseur join reparation using(idAscenseur) where reparation.login=?;")) {
                stmt.setString(1, DataAccess.getLogin());
                try (ResultSet rs = stmt.executeQuery()) {
                    result = getReparations(rs);
                }
            }
        } else if (DataAccess.isGestionnaire()) {
            try (PreparedStatement stmt = instance.getConnection()
                    .prepareStatement("select * from immeuble natural join adresse natural join ascenseur join reparation using(idAscenseur) where immeuble.login=?;")) {
                stmt.setString(1, DataAccess.getLogin());
                try (ResultSet rs = stmt.executeQuery()) {
                    result = getReparations(rs);
                }
            }
        } else {
            try (Statement stmt = instance.getConnection().createStatement()) {
                try (ResultSet rs = stmt
                        .executeQuery("select * from immeuble natural join adresse natural join ascenseur join reparation using(idAscenseur);")) {
                    result = getReparations(rs);
                }
            }
        }

        // get trajets
        if (DataAccess.isGestionnaire()) {
            try (PreparedStatement stmt2 = instance.getConnection()
                    .prepareStatement("select * from immeuble natural join adresse natural join ascenseur " +
                            "join reparation using(idAscenseur) natural join trajetaller " +
                            "where immeuble.login=?;")) {
                stmt2.setString(1, DataAccess.getLogin());
                try (ResultSet rs2 = stmt2.executeQuery()) {
                    getTrajets(result, rs2);
                }
            }
        } else if (DataAccess.isAscensoriste()) {
            try (PreparedStatement stmt2 = instance.getConnection()
                    .prepareStatement("select * from immeuble natural join adresse natural join ascenseur " +
                            "join reparation using(idAscenseur) natural join trajetaller " +
                            "where reparation.login=?;")) {
                stmt2.setString(1, DataAccess.getLogin());
                try (ResultSet rs2 = stmt2.executeQuery()) {
                    getTrajets(result, rs2);
                }
            }
        } else {
            try (Statement stmt2 = instance.getConnection().createStatement()) {
                try (ResultSet rs2 = stmt2
                        .executeQuery("select * from immeuble natural join adresse natural join ascenseur " +
                                "join reparation using(idAscenseur) natural join trajetaller;")) {
                    getTrajets(result, rs2);
                }
            }
        }

        // get interventions
        if (DataAccess.isGestionnaire()) {
            try (PreparedStatement stmt1 = instance.getConnection()
                    .prepareStatement("select * from immeuble natural join adresse natural join ascenseur " +
                            "join reparation using(idAscenseur) natural join intervention " +
                            "where immeuble.login=?;")) {
                stmt1.setString(1, DataAccess.getLogin());

                try (ResultSet rs1 = stmt1.executeQuery()) {
                    getInterventions(result, rs1);
                }
            }
        } else if (DataAccess.isAscensoriste()) {
            try (PreparedStatement stmt1 = instance.getConnection()
                    .prepareStatement("select * from immeuble natural join adresse natural join ascenseur " +
                            "join reparation using(idAscenseur) natural join intervention " +
                            "where reparation.login=?;")) {
                stmt1.setString(1, DataAccess.getLogin());

                try (ResultSet rs1 = stmt1.executeQuery()) {
                    getInterventions(result, rs1);
                }
            }
        } else {
            try (Statement stmt1 = instance.getConnection().createStatement()) {
                try (ResultSet rs1 = stmt1
                        .executeQuery("select * from immeuble natural join adresse natural join ascenseur " +
                                "join reparation using(idAscenseur) natural join intervention;")) {
                    getInterventions(result, rs1);
                }
            }
        }

        // sort result
        result.sort(new PlanningRessourceComparator());

        return result;
    }


    private List<PlanningRessource> getReparations(ResultSet rs) throws SQLException {
        ArrayList<PlanningRessource> result = new ArrayList<>();

        while (rs.next()) {
            TypeReparation typeReparation = TypeReparation.get(rs.getString("typeReparation"));
            Ascenseur ascenseur = new Ascenseur(rs.getString("marque"), rs.getString("modele"),
                    rs.getTimestamp("miseEnService"), rs.getInt("etage"),
                    EtatAscenseur.get(rs.getString("etat")));
            ascenseur.setIdAscenseur(rs.getInt("idAscenseur"));

            Reparation reparation = new Reparation(ascenseur, rs.getTimestamp("datePanne"),
                    typeReparation, typeReparation.duree);
            reparation.setImmeuble(new Immeuble(rs.getString("nom"), rs.getInt("nbEtage"),
                    new Adresse(rs.getString("rue"), rs.getString("ville"),
                            rs.getString("CP"), rs.getFloat("latitude"), rs.getFloat("longitude"))));
            reparation.setLoginAscensoriste(rs.getString("reparation.login"));
            reparation.setCommentaire(rs.getString("commentaire"));
            result.add(reparation);
        }
        return result;
    }

    private void getInterventions(List<PlanningRessource> result, ResultSet rs) throws SQLException, ParseException {
        while (rs.next()) {
            int idAscenseur = rs.getInt("idAscenseur");
            java.util.Date datePanne = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("datePanne"));
            Timestamp dateIntervention = rs.getTimestamp("dateIntervention");
            int avancement = rs.getInt("avancement");

            Reparation reparation = getThisReparation(result, idAscenseur, datePanne);

            Intervention intervention = new Intervention(reparation, dateIntervention);
            intervention.setAvancement(avancement);
            result.add(intervention);
        }
    }

    private void getTrajets(List<PlanningRessource> result, ResultSet rs) throws SQLException, ParseException {
        while (rs.next()) {
            int idAscenseur = rs.getInt("idAscenseur");
            java.util.Date datePanne = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("datePanne"));
            Timestamp dateTrajet = rs.getTimestamp("dateTrajet");
            int dureeTrajet = rs.getInt("dureeTrajet");

            Reparation reparation = getThisReparation(result, idAscenseur, datePanne);

            TrajetAller trajetAller = new TrajetAller(reparation, dateTrajet, dureeTrajet);
            result.add(trajetAller);
        }
    }

    private Reparation getThisReparation(List<PlanningRessource> reparations, int idAscenseur, java.util.Date datePanne) {
        return (Reparation) reparations.stream().filter(v -> {
                    if (v instanceof Reparation) {
                        return ((Reparation) v).getAscenseur().getIdAscenseur() == idAscenseur &&
                                ((Reparation) v).getDatePanne().compareTo(datePanne) == 0;
                    }
                    return false;
                }
        ).findFirst().orElse(null);
    }

    /**
     * Ajout une nouvelle panne qui a pour date Maintenant
     *
     * @param reparation
     * @param idAscenseur
     * @throws SQLException
     */
    public void addReparation(Reparation reparation, int idAscenseur) throws SQLException {
        try (PreparedStatement stmt = instance.getConnection()
                .prepareStatement("insert into reparation(idAscenseur,datePanne,typeReparation,duree) values(?,?,?,?);")) {
            stmt.setInt(1, idAscenseur);
            stmt.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(reparation.getDatePanne()));
            stmt.setString(3, reparation.getType().toString());
            stmt.setInt(4, reparation.getType().duree);
            stmt.executeUpdate();
        }
    }


    /**
     * @param reparation
     * @param intervention
     * @throws SQLException
     */
    public void attachIntervention(Reparation reparation, Intervention intervention) throws SQLException {
        instance.getConnection().setAutoCommit(false);

        try {
            // Création intervention
            try (PreparedStatement stmt = instance.getConnection()
                    .prepareStatement("insert into intervention (dateIntervention) values(?);", Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(intervention.getDateIntervention()));
                stmt.executeUpdate();

                // La rattacher à la panne
                try (ResultSet rs1 = stmt.getGeneratedKeys()) {
                    if (rs1.next()) {
                        int idIntervention = rs1.getInt(1);

                        try (PreparedStatement stmt2 = instance.getConnection()
                                .prepareStatement("update reparation set IdIntervention=? where idAscenseur=? and datePanne=?;")) {
                            stmt2.setInt(1, idIntervention);
                            stmt2.setInt(2, reparation.getAscenseur().getIdAscenseur());
                            stmt2.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(reparation.getDatePanne()));
                            stmt2.executeUpdate();
                        }
                    }
                }

                // Passer l'ascenseur dans l'état Réparation
                Ascenseur ascenseur = new AscenseurDAO().getAscenseur(reparation.getAscenseur().getIdAscenseur());
                ascenseur.setState(EtatAscenseur.EnCoursDeReparation);

                new AscenseurDAO().editAscenseur(ascenseur.getIdAscenseur(), ascenseur);
            }

            instance.getConnection().commit();

        } catch (SQLException throwables) {
            instance.getConnection().rollback();
            instance.getConnection().setAutoCommit(true);
            throw throwables;
        }

        instance.getConnection().setAutoCommit(true);
    }

    /**
     * @param reparation
     * @param trajet
     * @throws SQLException
     */
    public void attachTrajet(Reparation reparation, TrajetAller trajet) throws SQLException {
        instance.getConnection().setAutoCommit(false);

        try {
            // Création trajet
            try (PreparedStatement stmt = instance.getConnection()
                    .prepareStatement("insert into trajetaller (dateTrajet, dureeTrajet) values(?,?);",
                            Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(trajet.getDateTrajet()));
                stmt.setInt(2, trajet.getDureeTrajet());
                stmt.executeUpdate();

                // La rattacher à la panne
                try (ResultSet rs1 = stmt.getGeneratedKeys()) {
                    if (rs1.next()) {
                        int idTrajet = rs1.getInt(1);

                        try (PreparedStatement stmt2 = instance.getConnection()
                                .prepareStatement("update reparation set idTrajet=?, login=? " +
                                        "where idAscenseur=? and datePanne=?;")) {
                            stmt2.setInt(1, idTrajet);
                            stmt2.setString(2, reparation.getLoginAscensoriste());
                            stmt2.setInt(3, reparation.getAscenseur().getIdAscenseur());
                            // Not working if using Timestamp
                            stmt2.setString(4, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(reparation.getDatePanne()));
                            stmt2.executeUpdate();
                        }
                    }
                }
            }
            instance.getConnection().commit();

        } catch (SQLException throwables) {
            instance.getConnection().rollback();
            instance.getConnection().setAutoCommit(true);
            throw throwables;
        }

        instance.getConnection().setAutoCommit(true);
    }

    /**
     * @param intervention
     * @throws SQLException
     */
    public void updateStatusIntervention(Intervention intervention) throws SQLException {
        Reparation reparation = intervention.getReparation();

        // set commentaire
        try (PreparedStatement stmt = instance.getConnection()
                .prepareStatement("update Reparation set commentaire=? where idAscenseur=? and datePanne=?;")) {
            stmt.setString(1, reparation.getCommentaire());
            stmt.setInt(2, reparation.getAscenseur().getIdAscenseur());
            stmt.setTimestamp(3, new Timestamp(reparation.getDatePanne().getTime()));
            stmt.executeUpdate();
        }

        // set avancement
        String datePanne = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(reparation.getDatePanne());

        try (PreparedStatement stmt1 = instance.getConnection()
                .prepareStatement("update Intervention set avancement=? " +
                        "where IdIntervention=(select IdIntervention from Reparation where idAscenseur=? and datePanne=?);")) {
            stmt1.setInt(1, intervention.getAvancement());
            stmt1.setInt(2, reparation.getAscenseur().getIdAscenseur());
            stmt1.setString(3, datePanne);
            stmt1.executeUpdate();
        }

        /* Si l'intervention a commencé, forcer la position de l'ascensoriste
         * dans le lieu de l'intervention */
        if (intervention.getAvancement() > 0) {
            try (PreparedStatement stmt2 = instance.getConnection()
                    .prepareStatement("update ascensoriste set latitude=?, longitude=? where login=?;")) {
                Adresse adresse = reparation.getImmeuble().getAdresse();

                stmt2.setFloat(1, adresse.getLatitude());
                stmt2.setFloat(2, adresse.getLongitude());
                stmt2.setString(3, reparation.getLoginAscensoriste());
                stmt2.executeUpdate();

                // Remettre en service ascenseur si à  avancé est à 100%
                if(intervention.getAvancement() == 100) {
                    Ascenseur ascenseur = reparation.getAscenseur();
                    int idAscenseur = ascenseur.getIdAscenseur();
                    ascenseur.setState(EtatAscenseur.EnService);

                    new AscenseurDAO().editAscenseur(idAscenseur, ascenseur);
                }
            }
        }
    }

}

