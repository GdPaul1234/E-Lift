package main.controller.DAO;

import main.model.Adresse;
import main.model.Ascenseur;
import main.model.Immeuble;
import main.model.Reparation;
import main.model.enums.EtatAscenseur;
import main.model.enums.TypeReparation;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class AscenseurDAO {
    private final DataAccess instance;

    /**
     *
     */
    public AscenseurDAO() {
        instance = DataAccess.getInstance();
    }

    public Ascenseur getAscenseur(int idAscenseur) throws SQLException {
        Ascenseur ascenseur = null;
        try(PreparedStatement stmt = instance.getConnection()
                .prepareStatement("select * from ascenseur where idAscenseur=?;")) {
            stmt.setInt(1, idAscenseur);
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    ascenseur = new Ascenseur(rs.getString("marque"), rs.getString("modele"),
                            rs.getTimestamp("miseEnService"), rs.getInt("etage"),
                            EtatAscenseur.get(rs.getString("etat")));
                    ascenseur.setIdAscenseur(rs.getInt("idAscenseur"));
                }
            }
        }
        return ascenseur;
    }

    /**
     *
     * @param idImmeuble
     * @return
     * @throws SQLException
     */
    public List<Ascenseur> getAscenseursImmeuble(int idImmeuble) throws SQLException {
        ArrayList<Ascenseur> result;
        try (PreparedStatement stmt = instance.getConnection().prepareStatement("select * from ascenseur where IdImmeuble=?;")) {
            stmt.setInt(1, idImmeuble);
            try (ResultSet rs = stmt.executeQuery()) {

                result = new ArrayList<>(rs.getFetchSize());
                while (rs.next()) {
                    Ascenseur ascenseur = new Ascenseur(rs.getString("marque"), rs.getString("modele"),
                            rs.getTimestamp("miseEnService"), rs.getInt("etage"),
                            EtatAscenseur.get(rs.getString("etat")));
                    ascenseur.setIdAscenseur(rs.getInt("idAscenseur"));
                    result.add(ascenseur);
                }
            }
        }
        return result;
    }

    /**
     *
     * @param idAscenseur
     * @return
     */
    public Immeuble getImmeubleAscenseur(int idAscenseur) throws SQLException {
        Immeuble immeuble = null;

        try(PreparedStatement stmt = instance.getConnection()
                .prepareStatement("select * from ascenseur natural join immeuble natural join adresse " +
                        "where idAscenseur=?;")) {
            stmt.setInt(1, idAscenseur);
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    immeuble = new Immeuble(rs.getString("nom"), rs.getInt("nbEtage"),
                            new Adresse(rs.getString("rue"), rs.getString("ville"), rs.getString("CP"), rs.getFloat("latitude"), rs.getFloat("longitude")));
                    immeuble.setIdImmeuble(rs.getInt("IdImmeuble"));
                }
            }
        }

        return immeuble;
    }

    /**
     *
     * @param idAscenseur
     * @param etatAscenseur
     * @throws SQLException
     */
    private void declencherReparation(int idAscenseur, EtatAscenseur etatAscenseur) throws SQLException {
        switch (etatAscenseur) {
            case EnPanne, EnPannePersonnes -> {
                // TODO Obtenir vrai cause panne
                /* Nous supposons que les ascenseurs sont capables de nous donner la
                 * cause de la panne grace à un auto diagnostic.
                 *
                 * Pour le moment, nous générons une réparation de type aléatoire
                 */
                TypeReparation[] values = TypeReparation.getValues();
                int index = new Random().nextInt(values.length);
                TypeReparation typeReparation = values[index];

                Ascenseur ascenseur = new Ascenseur();
                ascenseur.setIdAscenseur(idAscenseur);

                ReparationDAO reparationDAO = new ReparationDAO();
                Date now = new Date(new Date().getTime());

                Reparation reparation = new Reparation(ascenseur, now, typeReparation, typeReparation.duree);
                reparationDAO.addReparation(reparation, idAscenseur);

                // TODO Envoyer message aux parties prenantes (gestionnaire de l'immeuble et société)
                /*
                 * Solutions envisagées : JMS, SMS
                 * Solution retenue : polling
                 */

                // Envoyer ascensoriste sur le front
                new AscensoristeDAO().moveAscensoriste(idAscenseur, reparation);
            }
        }
    }

    /**
     *
     * @param ascenseur
     * @param idImmeuble
     * @throws SQLException
     */
    public void addAscenseur(Ascenseur ascenseur, int idImmeuble) throws SQLException {
        // Verifier nbEtage avant ajouter ascenseur
        try (PreparedStatement stmt = instance.getConnection().prepareStatement("select nbEtage from immeuble where IdImmeuble=?")) {
            stmt.setInt(1, idImmeuble);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int nNbEtageImmeuble = rs.getInt("nbEtage");

                    if (ascenseur.getEtage() <= nNbEtageImmeuble) {
                        try (PreparedStatement stmt1 = instance.getConnection().prepareStatement(
                                "insert into ascenseur(idAscenseur,marque,modele,miseEnService,etat,etage,IdImmeuble) values(?,?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS)) {
                            stmt1.setInt(1, ascenseur.getIdAscenseur());
                            stmt1.setString(2, ascenseur.getMarque());
                            stmt1.setString(3, ascenseur.getModele());
                            stmt1.setTimestamp(4, new Timestamp(ascenseur.getDateMiseEnService().getTime()));
                            stmt1.setString(5, ascenseur.getState().toString());
                            stmt1.setInt(6, ascenseur.getEtage());
                            stmt1.setInt(7, idImmeuble);
                            stmt1.executeUpdate();

                            try (ResultSet rs1 = stmt1.getGeneratedKeys()) {
                                if (rs1.next()) {
                                    int idAscenseur = rs1.getInt(1);
                                    declencherReparation(idAscenseur, ascenseur.getState());
                                }
                            }
                        }

                    } else {
                        rs.close();
                        stmt.close();
                        throw new SQLException("Constraint failed : etage > etageImmeuble !");
                    }
                }

            }
        }

    }

    /**
     *
     * @param ascenseurID
     * @param ascenseur
     * @throws SQLException
     */
    public void editAscenseur(int ascenseurID, Ascenseur ascenseur) throws SQLException {
        // Verifier nbEtage avant modifier ascenseur
        try (PreparedStatement stmt = instance.getConnection().prepareStatement("select nbEtage from immeuble natural join ascenseur where idAscenseur=?")) {
            stmt.setInt(1, ascenseurID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int nNbEtageImmeuble = rs.getInt("nbEtage");

                if (ascenseur.getEtage() <= nNbEtageImmeuble) {
                    try (PreparedStatement stmt1 = instance.getConnection()
                            .prepareStatement("update Ascenseur set marque=?, modele=?, miseEnService=?, etage=?, etat=? where idAscenseur=?;")) {
                        stmt1.setString(1, ascenseur.getMarque());
                        stmt1.setString(2, ascenseur.getModele());
                        stmt1.setTimestamp(3, new Timestamp(ascenseur.getDateMiseEnService().getTime()));
                        stmt1.setInt(4, ascenseur.getEtage());
                        stmt1.setString(5, ascenseur.getState().toString());
                        stmt1.setInt(6, ascenseurID);
                        stmt1.executeUpdate();

                        declencherReparation(ascenseurID, ascenseur.getState());
                    }
                } else {
                    rs.close();
                    stmt.close();
                    throw new SQLException("Constraint failed : etage > etageImmeuble !");
                }
            }

            rs.close();
        }
    }

    /**
     *
     * @param idAscenceur
     * @throws SQLException
     */
    public void removeAscenceur(int idAscenceur) throws SQLException {
        try (PreparedStatement stmt = instance.getConnection()
                .prepareStatement("delete from ascenseur where idAscenseur=?;")) {
            stmt.setInt(1, idAscenceur);
            stmt.executeUpdate();
        }
    }
}
