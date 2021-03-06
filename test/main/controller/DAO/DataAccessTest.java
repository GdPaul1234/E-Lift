package main.controller.DAO;


import main.controller.DAO.DataAccess;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataAccessTest {
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
    void testFailGetInstance()  {
        try {
            DataAccess dataAccess = DataAccess.getInstance("ultrasecurepwd", "root");
            assertNull(dataAccess.getConnection());

            dataAccess.close();
            fail("Should not be here");

        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Success");
        }

    }

    @Test
    void testGetConnection() {
        Connection connection = dataAccess.getConnection();
        assertNotNull(connection);
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