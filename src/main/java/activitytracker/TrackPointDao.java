package activitytracker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class TrackPointDao {

    private final EntityManagerFactory entityManagerFactory;

    public TrackPointDao(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void saveTrackPoint(TrackPoint trackPoint) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(trackPoint);
        em.getTransaction().commit();
        em.close();
    }

    public TrackPoint findTrackPoint(long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        TrackPoint trackPoint = em.createQuery("select t from TrackPoint t where t.id = :id", TrackPoint.class)
                .setParameter("id", id)
                .getSingleResult();
        em.close();
        return trackPoint;
    }

}
