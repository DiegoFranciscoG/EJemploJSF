package controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author diego
 */
public abstract class AbstractFacade<T> {

    private static EntityManagerFactory emf;

    private Class<T> entityClass;

    static {
        // Payara Micro no registra el driver JDBC automaticamente, hay que cargarlo manualmente
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("No se encontro el driver MySQL: " + e.getMessage());
        }
        Map<String, String> props = new HashMap<>();
        String password = System.getenv("DB_PASSWORD");
        if (password != null && !password.isEmpty()) {
            props.put("javax.persistence.jdbc.password", password);
        }
        emf = Persistence.createEntityManagerFactory("EJemploJSFPU", props);
    }

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void edit(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void remove(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.remove(em.merge(entity));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public T find(Object id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    public List<T> findRange(int[] range) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            javax.persistence.Query q = em.createQuery(cq);
            q.setMaxResults(range[1] - range[0] + 1);
            q.setFirstResult(range[0]);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public int count() {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
            cq.select(em.getCriteriaBuilder().count(rt));
            javax.persistence.Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
