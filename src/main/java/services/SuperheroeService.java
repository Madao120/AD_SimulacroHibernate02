package services;

import config.HibernateConfig;
import model.Superheroe;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SuperheroeService {

    public void crearSuperheroe(Superheroe superheroe){
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(superheroe);

            transaction.commit();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public Superheroe leerSuperheroe (int id){
        try (Session session =
                     HibernateConfig.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();

            Superheroe superheroe = session.get(Superheroe.class, id);

            return superheroe;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public Superheroe leerSuperheroeNome (String nome){
        try (Session session =
                     HibernateConfig.getSessionFactory().openSession()) {

            return session.createQuery("FROM Superheroe WHERE nome = :nome", Superheroe.class)
                    .setParameter("nome", nome).uniqueResult();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public List<Superheroe> leerSuperheroes (){
        try (Session session =
                     HibernateConfig.getSessionFactory().openSession()) {

            return session.createQuery("FROM Superheroe", Superheroe.class).list();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public void actualizarSuperheroe (int id, Superheroe superheroe_actualizado){
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Superheroe superheroe = session.get(Superheroe.class, id);

            if (superheroe != null){
                superheroe.setNome(superheroe_actualizado.getNome());
                superheroe.setNome_real(superheroe_actualizado.getNome_real());
                superheroe.setPoder(superheroe_actualizado.getPoder());

                session.update(superheroe);
                transaction.commit();
            }
            else{
                transaction.rollback();
                System.out.println("Parece que no se ha podido actualizar el/la Superheroe");
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void borrarSuperheroe(int id){
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Superheroe superheroe = session.get(Superheroe.class, id);

            if (superheroe != null){
                session.delete(superheroe);
                transaction.commit();
            }
            else{
                transaction.rollback();
                System.out.println("Parece que no se pudo borrar el/la Superheroe");
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
