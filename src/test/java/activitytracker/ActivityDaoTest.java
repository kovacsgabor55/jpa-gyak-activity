package activitytracker;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.sql.SQLException;
import java.time.LocalDate;
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

    @Test
    void testTrackPoint() {
        TrackPoint trackPointStart = new TrackPoint(LocalDate.of(2021, 12, 22), 34.543, 25.765);
        TrackPoint trackPointStop = new TrackPoint(LocalDate.of(2021, 12, 23), 34.560, 25.712);

        Activity activity = new Activity(LocalDateTime.now(), "description", ActivityType.BIKING);
        activity.addTrackPoint(trackPointStop);
        activity.addTrackPoint(trackPointStart);
        activityDao.saveActivity(activity);

        Activity anotherActivity = activityDao.findActivityByIdWithTrackPoints(activity.getId());

        assertEquals(2, anotherActivity.getTrackPoints().size());
        assertEquals("2021-12-22", anotherActivity.getTrackPoints().get(0).getTime().toString());
    }

    @Test
    void testAddTrackNumber() {
        Activity activity = new Activity(LocalDateTime.of(2020, Month.JANUARY, 15, 5, 9)
                , "description", ActivityType.RUNNING);
        activityDao.saveActivity(activity);

        activityDao.addTrackPoint(activity.getId(), new TrackPoint(LocalDate.now(), 34.543, 25.765));

        Activity anotherActivity = activityDao.findActivityByIdWithTrackPoints(activity.getId());

        assertEquals(1, anotherActivity.getTrackPoints().size());
    }

    @Test
    void testRemove() {
        Activity activity = new Activity(LocalDateTime.of(2020, Month.JANUARY, 15, 5, 9)
                , "description", ActivityType.RUNNING);
        TrackPoint trackPointStart = new TrackPoint(LocalDate.of(2021, 12, 22), 34.543, 25.765);
        TrackPoint trackPointStop = new TrackPoint(LocalDate.of(2021, 12, 23), 34.560, 25.712);

        activity.addTrackPoint(trackPointStop);
        activity.addTrackPoint(trackPointStart);

        activityDao.saveActivity(activity);

        activityDao.deleteActivity(activity.getId());
    }

    @Test
    void findTrackPointCoordinatesByDate() {
        TrackPoint trackPoint1 = new TrackPoint(LocalDate.of(2017, 12, 22), 1.1, 25.765);
        TrackPoint trackPoint2 = new TrackPoint(LocalDate.of(2018, 01, 01), 2.2, 25.765);
        TrackPoint trackPoint3 = new TrackPoint(LocalDate.of(2021, 12, 24), 3.3, 25.765);
        TrackPoint trackPoint4 = new TrackPoint(LocalDate.of(2021, 12, 25), 4.4, 25.712);
        TrackPoint trackPoint5 = new TrackPoint(LocalDate.of(2021, 12, 26), 5.5, 25.712);

        Activity activity = new Activity(LocalDateTime.now(), "description", ActivityType.BIKING);
        activity.addTrackPoint(trackPoint1);
        activity.addTrackPoint(trackPoint2);
        activity.addTrackPoint(trackPoint3);
        activity.addTrackPoint(trackPoint4);
        activity.addTrackPoint(trackPoint5);
        activityDao.saveActivity(activity);

        LocalDate localDate = LocalDate.of(2018, 01,01);
        List<Coordinate> coordinates = activityDao.findTrackPointCoordinatesByDate(localDate, 0, 3);
        System.out.println(coordinates.size());
        assertEquals(3,coordinates.size());
        assertEquals(trackPoint3.getLat(),coordinates.get(0).getLat());
        assertEquals(trackPoint4.getLat(),coordinates.get(1).getLat());
        assertEquals(trackPoint5.getLat(),coordinates.get(2).getLat());
        System.out.println(coordinates);
    }

    @Test
    void findTrackPointCoordinatesByDateStart1Stop2() {
        TrackPoint trackPoint1 = new TrackPoint(LocalDate.of(2017, 12, 22), 1.1, 25.765);
        TrackPoint trackPoint2 = new TrackPoint(LocalDate.of(2018, 01, 01), 2.2, 25.765);
        TrackPoint trackPoint3 = new TrackPoint(LocalDate.of(2021, 12, 24), 3.3, 25.765);
        TrackPoint trackPoint4 = new TrackPoint(LocalDate.of(2021, 12, 25), 4.4, 25.712);

        Activity activity = new Activity(LocalDateTime.now(), "description", ActivityType.BIKING);
        activity.addTrackPoint(trackPoint1);
        activity.addTrackPoint(trackPoint2);
        activity.addTrackPoint(trackPoint3);
        activity.addTrackPoint(trackPoint4);
        activityDao.saveActivity(activity);

        LocalDate localDate = LocalDate.of(2018, 01,01);
        List<Coordinate> coordinates = activityDao.findTrackPointCoordinatesByDate(localDate, 1, 1);
        System.out.println(coordinates.size());
        assertEquals(1,coordinates.size());
        //assertEquals(trackPoint3.getLat(),coordinates.get(0).getLat());
        assertEquals(trackPoint4.getLat(),coordinates.get(0).getLat());
        System.out.println(coordinates);
    }
}
