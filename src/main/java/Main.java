
import model.Ciudad;
import model.Equipo;
import model.Superheroe;
import services.CiudadService;
import services.EquipoService;
import services.SuperheroeService;

import java.util.ArrayList;
import java.util.List;
public class Main {

    public static void main(String[] args) {

        CiudadService ciudadService = new CiudadService();
        EquipoService equipoService = new EquipoService();
        SuperheroeService superheroeService = new SuperheroeService();

        Superheroe super1 = new Superheroe();
        super1.setNome("Spiderman");
        Superheroe super2 = new Superheroe();
        super2.setNome("Batman");
        Superheroe super3 = new Superheroe();
        super3.setNome("Superman");

        List<Superheroe> equipo1 = new ArrayList<>();
        equipo1.add(super1);

        List<Superheroe> equipo2 = new ArrayList<>();
        equipo2.add(super2);
        equipo2.add(super3);

        Equipo vengadores = new Equipo("Vengadores", "Torre Stark", equipo1);
        Equipo ligaJusticia = new Equipo("Liga de la Justicia", "Fortaleza de la Soledad", equipo2);

        // Crear Ciudades
        Ciudad nuevaYork = new Ciudad("Nueva York", 200000, 6);
        Ciudad gotham = new Ciudad("Gotham", 150000, 10);
        Ciudad metropolis = new Ciudad("Metropolis", 300000, 8);

        // Completar Spiderman
        super1.setNome_real("Peter Parker");
        super1.setPoder("Sentido arácnido");
        super1.setEquipo(vengadores);
        super1.setCiudad(nuevaYork);

        // Completar Batman
        super2.setNome_real("Bruce Wayne");
        super2.setPoder("Inteligencia y habilidades de combate");
        super2.setEquipo(ligaJusticia);
        super2.setCiudad(gotham);

        // Completar Superman
        super3.setNome_real("Clark Kent");
        super3.setPoder("Super fuerza y vuelo");
        super3.setEquipo(ligaJusticia);
        super3.setCiudad(metropolis);


        // Creación de registros
        ciudadService.crearCiudad(nuevaYork);
        ciudadService.crearCiudad(gotham);
        ciudadService.crearCiudad(metropolis);

        equipoService.crearEquipo(vengadores);
        equipoService.crearEquipo(ligaJusticia);

        // Leer y mostrar datos
        System.out.println("-------------CIUDADES-------------");
        List<Ciudad> ciudades = ciudadService.leerCiudades();
        for (Ciudad ciudad : ciudades) {
            System.out.println(ciudad);
        }

        System.out.println("-------------EQUIPOS (SUPERHEROES)-------------");
        List<Equipo> equipos = equipoService.leerEquipos();
        for (Equipo equipo : equipos) {
            System.out.println(equipo);
        }

        /* Borrar Superman NO FUNCIONA POR COMO ES LA RELACIÓN, NO ENTRA EN EXAMEN
        System.out.println("-------------BORRAR SUPERMAN-------------");
        superheroeService.borrarSuperheroe(super3.getId());
        */
        // Actualizar Gotham
        System.out.println("-------------ACTUALIZAR GOTHAM-------------");
        Ciudad gothamActualizada = new Ciudad("Gotham City", 160000, 11);
        ciudadService.actualizarCiudad(gotham.getId(), gothamActualizada);
        System.out.println(ciudadService.leerCiudadNome("Gotham City"));

        // Actualizar Spiderman
        System.out.println("-------------ACTUALIZAR SPIDERMAN-------------");
        Superheroe spidermanActualizado = new Superheroe();
        spidermanActualizado.setNome("Spider-Man");
        spidermanActualizado.setNome_real("Peter B. Parker");
        spidermanActualizado.setPoder("Sentido arácnido mejorado");
        superheroeService.actualizarSuperheroe(super1.getId(), spidermanActualizado);
        System.out.println(superheroeService.leerSuperheroeNome("Spider-Man"));

        System.out.println("-------------EQUIPOS (SUPERHEROES) DESPUES DE Actualizar-------------");
        List<Equipo> equiposActualizados = equipoService.leerEquipos();
        for (Equipo equipo : equiposActualizados) {
            System.out.println(equipo);
        }

        System.out.println("-------------BORRANDO TODO-------------");
        for (Equipo equipo : equipoService.leerEquipos()) {
            equipoService.borrarEquipo(equipo.getId());
        }
        for (Ciudad ciudad : ciudadService.leerCiudades()) {
            ciudadService.borrarCiudad(ciudad.getId());
        }
        // Lo siguiente es redundanet ya que Equipo tiene CASCADE.ALL en la relación con Superheroe, pero lo dejo para que se vea el funcionamiento de borrarSuperheroe
        /*
        for (Superheroe superheroe : superheroeService.leerSuperheroes()) {
            superheroeService.borrarSuperheroe(superheroe.getId());
        }
         Ahora leeremos superheroes apra ver que no queda nada*/
        System.out.println("Ahora debería de no dar nada");
        List<Superheroe> superheroesFin = superheroeService.leerSuperheroes();

        if (superheroesFin == null) {
            System.out.println("No hay superheroes");
        } else {
            for (Superheroe superheroe : superheroesFin) {
                System.out.println(superheroe);
            }
        }
    }
}

