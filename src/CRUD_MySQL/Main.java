package CRUD_MySQL;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author jquezada
 */
public class Main {

    private static final PersonaDAO dao = new PersonaDAO();

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion();
            procesarOpcion(opcion);
        } while (opcion != 5);
    }

    private static void mostrarMenu() {
        System.out.println("**CRUD Personas**");
        System.out.println("1. Crear persona");
        System.out.println("2. Listar personas");
        System.out.println("3. Leer persona");
        System.out.println("4. Actualizar persona");
        System.out.println("5. Eliminar persona");
        System.out.println("6. Salir");
    }

    private static int leerOpcion() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduzca una opción: ");
        return scanner.nextInt();
    }

    private static void procesarOpcion(int opcion) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        switch (opcion) {
            case 1:
                crearPersona();
                break;
            case 2:
                listarPersonas();
                break;
            case 3:
                leerPersona();
                break;
            case 4:
                actualizarPersona();
                break;
            case 5:
                eliminarPersona();
                break;
            case 6:
                System.out.println("Saliendo del programa...");
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void crearPersona() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduzca el nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Introduzca la edad: ");
        int edad = scanner.nextInt();

        Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setEdad(edad);

        dao.create(persona);
        System.out.println("Persona creada correctamente.");
    }

    private static void leerPersona() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduzca el ID de la persona: ");
        int id = scanner.nextInt();

        Persona persona = dao.read(id);

        if (persona != null) {
            System.out.println("**Persona:**");
            System.out.println("ID: " + persona.getId());
            System.out.println("Nombre: " + persona.getNombre());
            System.out.println("Edad: " + persona.getEdad());
        } else {
            System.out.println("Persona no encontrada.");
        }
    }

    private static void actualizarPersona() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduzca el ID de la persona: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Introduzca el nombre: ");
        String nombre = scanner.nextLine();
        System.out.println("\n");
        System.out.print("Introduzca la edad: ");
        int edad = scanner.nextInt();

        Persona persona = new Persona();
        persona.setId(id);
        persona.setNombre(nombre);
        persona.setEdad(edad);

        dao.update(persona);
        System.out.println("Persona actualizada correctamente.");
    }

    private static void eliminarPersona() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduzca el ID de la persona: ");
        int id = scanner.nextInt();

        dao.delete(id);
        System.out.println("Persona eliminada correctamente.");
    }

    private static void listarPersonas() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        List<Persona> personas = dao.getAll();
        
        for (Persona persona : personas) {
            System.out.println("ID: " + persona.getId());
            System.out.println("Nombre: " + persona.getNombre());
            System.out.println("Edad: " + persona.getEdad());
            System.out.println();
        }
    }

}
