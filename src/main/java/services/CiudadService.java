package services;

import model.Ciudad;
import config.HibernateConfig;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CiudadService {
    public void crearCiudad(Ciudad ciudad){
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(ciudad);

            transaction.commit();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public Ciudad leerCiudad (int id){
        try (Session session =
                     HibernateConfig.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();

            Ciudad ciudad = session.get(Ciudad.class, id);

            return ciudad;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public Ciudad leerCiudadNome (String nome){
        try (Session session =
                     HibernateConfig.getSessionFactory().openSession()) {

            return session.createQuery("FROM Ciudad WHERE nome = :nome", Ciudad.class)
                    .setParameter("nome", nome).uniqueResult();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public List<Ciudad> leerCiudades(){
        try (Session session =
                     HibernateConfig.getSessionFactory().openSession()) {

            return session.createQuery("FROM Ciudad", Ciudad.class).list();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public void actualizarCiudad (int id, Ciudad ciudad_actualizado){
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Ciudad ciudad = session.get(Ciudad.class, id);

            if (ciudad != null){
                ciudad.setNome(ciudad_actualizado.getNome());
                ciudad.setPoboacion(ciudad_actualizado.getPoboacion());
                ciudad.setNivel_perigo(ciudad_actualizado.getNivel_perigo());

                session.update(ciudad);
                transaction.commit();
            }
            else{
                transaction.rollback();
                System.out.println("Parece que no se ha podido actualizar el/la Ciudad");
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void borrarCiudad(int id){
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Ciudad ciudad = session.get(Ciudad.class, id);

            if (ciudad != null){
                session.delete(ciudad);
                transaction.commit();
            }
            else{
                transaction.rollback();
                System.out.println("Parece que no se pudo borrar el/la Ciudad");
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
