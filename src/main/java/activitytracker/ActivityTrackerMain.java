package activitytracker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public class ActivityTrackerMain {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("pu");
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        addActivity(em);

        em.getTransaction().commit();

        List<Activity> activities = em.createQuery("select a from Activity a", Activity.class)
                .getResultList();
        System.out.println(activities);

        em.close();
        factory.close();
    }

    private static void addActivity(EntityManager em) {
        Activity a = new Activity(LocalDateTime.of(2020, Month.APRIL, 12, 5, 9)
                , "description", ActivityType.BIKING);
        Activity b = new Activity(LocalDateTime.of(2020, Month.OCTOBER, 16, 5, 9)
                , "description", ActivityType.HIKING);
        Activity c = new Activity(LocalDateTime.of(2020, Month.FEBRUARY, 22, 5, 9)
                , "description", ActivityType.BASKETBALL);
        Activity d = new Activity(LocalDateTime.of(2020, Month.JANUARY, 15, 5, 9)
                , "description", ActivityType.RUNNING);
        em.persist(a);
        em.persist(b);
        em.persist(c);
        em.persist(d);
    }
}
