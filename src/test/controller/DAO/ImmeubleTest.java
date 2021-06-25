package controller.DAO;

import main.controller.DAO.DataAccess;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class ImmeubleTest {
    private static DataAccess dataAccess;

    @BeforeAll
    static void setUpBeforeClass() {
        try {
            dataAccess = DataAccess.getInstance("root", "ultrasecurepwd");
            assertNotNull(dataAccess.getConnection());
        } catch (IllegalArgumentException | SQLException e) {
            fail(e.getMessage());
        }

    }

    @Test
    void testInsertAdresse() throws SQLException {
        PreparedStatement stmtAdresse = dataAccess.getConnection()
                .prepareStatement("insert into Adresse(rue,ville,CP,latitude,longitude) values(?,?,?,?,?)");
        stmtAdresse.setString(1, "Rue");
        stmtAdresse.setString(2, "Ville");
        stmtAdresse.setString(3, "CP");
        stmtAdresse.setFloat(4, 0);// TODO get real latitude
        stmtAdresse.setFloat(5, 0);// TODO get real longitude

        assertNotNull(stmtAdresse.executeUpdate());
        stmtAdresse.close();

        fail("insertAdress doesn't work");
    }

    @AfterAll
    static void tearDownAfterClass() {
        assertNotNull(dataAccess.getConnection());
        try {
            dataAccess.close();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

}
