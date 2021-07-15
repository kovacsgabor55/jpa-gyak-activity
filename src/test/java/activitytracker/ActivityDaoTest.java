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
        Activity employee = new Activity(LocalDateTime.of(2020, Month.JANUARY, 15, 5, 9)
                , "description", ActivityType.RUNNING);
        activityDao.saveActivity(employee);

        long id = employee.getId();

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
}
