package CRUD_MySQL;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import CRUD_MySQL.Persona;
import CRUD_MySQL.PersonaDAO;

public class PersonaDAOTest {

    private PersonaDAO dao;
    
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;

    @BeforeEach
    public void setUp() {
        dao = new PersonaDAO();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    //
    // CREATE
    //

    @Test
    public void testCP_C01_InsertarRegistroValido() throws Exception {
        Persona p = new Persona();
        p.setNombre("Carlos Pérez");
        p.setEdad(30);

        dao.create(p);

        boolean existe = false;
        Connection conn = PersonaDAO.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM personas WHERE nombre = 'Carlos Pérez' AND edad = 30");
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            existe = true;
            dao.delete(rs.getInt("id")); 
        }

        assertTrue(existe, "El registro válido debería haberse insertado en la base de datos.");
    }

    @Test
    public void testCP_C02_InsertarPersonaNula() throws Exception {
        dao.create(null);
        
        String salidaConsola = outputStreamCaptor.toString().trim();
        assertEquals("Error: la persona no puede ser nula.", salidaConsola);
    }

    @Test
    public void testCP_C03_InsertarNombreVacio() throws Exception {
        Persona p = new Persona();
        p.setNombre("   ");
        p.setEdad(25);

        dao.create(p);

        String salidaConsola = outputStreamCaptor.toString().trim();
        assertEquals("Error: el nombre no puede estar vacío.", salidaConsola);
    }

    @Test
    public void testCP_C05_InsertarEdadCeroONegativa() throws Exception {
        Persona p = new Persona();
        p.setNombre("Ana López");
        p.setEdad(0);

        dao.create(p);

        String salidaConsola = outputStreamCaptor.toString().trim();
        assertEquals("Error: la edad debe ser mayor que cero.", salidaConsola);
    }

   
    //
    // UPDATE
    //
    
    @Test
    public void testCP_U01_ActualizarRegistroValido() throws Exception {
        Persona personaPrueba = new Persona();
        personaPrueba.setNombre("Temporal");
        personaPrueba.setEdad(20);
        dao.create(personaPrueba);
        
        Connection conn = PersonaDAO.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(id) as last_id FROM personas");
        rs.next();
        int idInsertado = rs.getInt("last_id");

        Persona personaActualizar = new Persona();
        personaActualizar.setId(idInsertado);
        personaActualizar.setNombre("Miguel Ángel");
        personaActualizar.setEdad(46);
        dao.update(personaActualizar);

        Persona personaModificada = dao.read(idInsertado);
        
        assertEquals("Miguel Ángel", personaModificada.getNombre());
        assertEquals(46, personaModificada.getEdad());

        dao.delete(idInsertado);
    }

    @Test
    public void testCP_U03_VulnerabilidadUpdateNombreVacio() throws Exception {
        Persona p = new Persona();
        p.setNombre("Valido");
        p.setEdad(30);
        dao.create(p);
        
        ResultSet rs = PersonaDAO.getConnection().createStatement().executeQuery("SELECT MAX(id) as last_id FROM personas");
        rs.next();
        int idInsertado = rs.getInt("last_id");

        Persona pUpdate = new Persona();
        pUpdate.setId(idInsertado);
        pUpdate.setNombre("   ");
        pUpdate.setEdad(35);
        
        dao.update(pUpdate);

        Persona leida = dao.read(idInsertado);
        assertEquals("   ", leida.getNombre(), "Falla de seguridad: El sistema permitió actualizar con un nombre vacío");

        dao.delete(idInsertado);
    }

        //
        //Validación de datos de entrada
        //
        
        public class PersonaDAOValidationTest {
    
        private PersonaDAO personaDAO;
    
        @BeforeEach
        void setUp() {
            personaDAO = new PersonaDAO();
        }
    
        @Test
        void testCreatePersonaValida() {
            Persona persona = new Persona(0, "Juan", 25);
            assertDoesNotThrow(() -> personaDAO.create(persona));
        }
    
        @Test
        void testNombreVacio() {
            Persona persona = new Persona(0, "", 25);
            assertThrows(IllegalArgumentException.class, () -> {
                personaDAO.create(persona);
            });
        }
    
        @Test
        void testNombreNull() {
            Persona persona = new Persona(0, null, 25);
            assertThrows(IllegalArgumentException.class, () -> {
                personaDAO.create(persona);
            });
        }
    
        @Test
        void testEdadNegativa() {
            Persona persona = new Persona(0, "Ana", -5);
            assertThrows(IllegalArgumentException.class, () -> {
                personaDAO.create(persona);
            });
        }
    
        @Test
        void testEdadCero() {
            Persona persona = new Persona(0, "Luis", 0);
            assertThrows(IllegalArgumentException.class, () -> {
                personaDAO.create(persona);
            });
        }
    }

    //
    //Manejo de errores y excepciones
    //

    public class PersonaDAOExceptionTest {

    private PersonaDAO personaDAO;

    @BeforeEach
    void setUp() {
        personaDAO = new PersonaDAO();
    }

    @Test
    void testBuscarIdInexistente() {
        Persona persona = personaDAO.getById(99999);
        assertNull(persona);
    }

    @Test
    void testEliminarIdInexistente() {
        assertDoesNotThrow(() -> personaDAO.delete(99999));
    }

    @Test
    void testActualizarIdInexistente() {
        Persona persona = new Persona(99999, "Carlos", 40);
        assertDoesNotThrow(() -> personaDAO.update(persona));
    }

    @Test
    void testErrorConexion() {
        PersonaDAO daoConError = new PersonaDAO("jdbc:mysql://localhost:3307/mydb", "root", "wrongpass");

        Persona persona = new Persona(0, "Test", 20);

        assertThrows(Exception.class, () -> {
            daoConError.create(persona);
        });
    }
}
}
