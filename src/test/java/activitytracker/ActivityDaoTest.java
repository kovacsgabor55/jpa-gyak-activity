package activitytracker;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDaoTest {

    private ActivityDao activityDao;

    @BeforeEach
    void init() throws SQLException {
        MariaDbDataSource dataSource;
        dataSource = new MariaDbDataSource();
        dataSource.setUrl("jdbc:mariadb://localhost:3306/activitytracker?useUnicode=true");
        dataSource.setUser("activitytracker");
        dataSource.setPassword("activitytracker");

        /*Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.clean();
        flyway.migrate();*/

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pu");
        activityDao = new ActivityDao(entityManagerFactory);
    }

    @Test
    void testSaveThenFindById() {
        Activity activity = new Activity(LocalDateTime.of(2020, Month.JANUARY, 15, 5, 9)
                , "description", ActivityType.RUNNING);
        activityDao.saveActivity(activity);

        long id = activity.getId();

        Activity another = activityDao.findActivityById(id);
        assertEquals("description", another.getDesc());
    }

    @Test
    void testSaveThenListAll() {
        activityDao.saveActivity(new Activity(LocalDateTime.of(2020, Month.APRIL, 12, 5, 9)
                , "first activity", ActivityType.BIKING));
        activityDao.saveActivity(new Activity(LocalDateTime.of(2021, Month.OCTOBER, 16, 5, 9)
                , "second activity", ActivityType.HIKING));

        List<Activity> activities = activityDao.listActivities();
        assertEquals(List.of("first activity", "second activity"),
                activities.stream()
                        .map(Activity::getDesc)
                        .collect(Collectors.toList()));
    }

    @Test
    void testSaveThenUpdateActivity() {
        Activity activity = new Activity(LocalDateTime.of(2020, Month.JANUARY, 15, 5, 9)
                , "description", ActivityType.RUNNING);
        activityDao.saveActivity(activity);

        long id = activity.getId();

        activityDao.updateActivity(id, "noitpircsed");
        Activity resultActivity = activityDao.findActivityById(id);
        assertEquals("noitpircsed", resultActivity.getDesc());
        assertTrue(resultActivity.getCreatedAt().isBefore(resultActivity.getUpdatedAt()));
    }

    @Test
    void testActivityWithLabels() {
        Activity activity = new Activity(LocalDateTime.of(2020, Month.JANUARY, 15, 5, 9)
                , "description", ActivityType.RUNNING);
        activity.setLabels(List.of("slovakia", "sturovo"));
        activityDao.saveActivity(activity);

        Activity anotherActivity = activityDao.findActivityByIdWithLabels(activity.getId());
        assertEquals(List.of("slovakia", "sturovo"), anotherActivity.getLabels());
    }
}
