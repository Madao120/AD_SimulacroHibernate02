package services;

import config.HibernateConfig;
import model.Equipo;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EquipoService {
    public void crearEquipo(Equipo equipo){
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(equipo);

            transaction.commit();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public Equipo leerEquipo (int id){
        try (Session session =
                     HibernateConfig.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();

            Equipo equipo = session.get(Equipo.class, id);

            return equipo;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public Equipo leerEquipoNome (String nome){
        try (Session session =
                     HibernateConfig.getSessionFactory().openSession()) {

            return session.createQuery("FROM Equipo WHERE nome = :nome", Equipo.class)
                    .setParameter("nome", nome).uniqueResult();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public List<Equipo> leerEquipos (){
        try (Session session =
                     HibernateConfig.getSessionFactory().openSession()) {

            return session.createQuery("FROM Equipo", Equipo.class).list();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public void actualizarEquipo (int id, Equipo equipo_actualizado){
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Equipo equipo = session.get(Equipo.class, id);

            if (equipo != null){
                equipo.setNome(equipo_actualizado.getNome());
                equipo.setBase(equipo_actualizado.getBase());

                session.update(equipo);
                transaction.commit();
            }
            else{
                transaction.rollback();
                System.out.println("Parece que no se ha podido actualizar el/la Equipo");
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void borrarEquipo(int id){
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Equipo equipo = session.get(Equipo.class, id);

            if (equipo != null){
                session.delete(equipo);
                transaction.commit();
            }
            else{
                transaction.rollback();
                System.out.println("Parece que no se pudo borrar el/la Equipo");
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
