package activitytracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrackPointDaoTest {
    private TrackPointDao trackPointDao;

    private ActivityDao activityDao;

    @BeforeEach
    void init() {
        EntityManagerFactory factory = Persistence
                .createEntityManagerFactory("pu");
        trackPointDao = new TrackPointDao(factory);
        activityDao = new ActivityDao(factory);
    }

    @Test
    void saveTrackPoint() {
        TrackPoint trackPoint = new TrackPoint(LocalDate.now(), 34.543, 25.765);
        trackPointDao.saveTrackPoint(trackPoint);
        TrackPoint another = trackPointDao.findTrackPoint(trackPoint.getId());
        assertEquals(trackPoint.getLat(), another.getLat());
        assertEquals(trackPoint.getLon(), another.getLon());
    }

    @Test
    void testSaveActivityWithTrackPoint() {
        TrackPoint trackPoint = new TrackPoint(LocalDate.now(), 34.543, 25.765);
        //trackPointDao.saveTrackPoint(trackPoint);

        Activity activity = new Activity(LocalDateTime.of(2020, Month.JANUARY, 15, 5, 9)
                , "description", ActivityType.RUNNING);
        activity.addTrackPoint(trackPoint);
        activityDao.saveActivity(activity);

        Activity anotherActivity = activityDao.findActivityByIdWithTrackPoints(activity.getId());
        assertEquals(trackPoint.getLon(), anotherActivity.getTrackPoints().get(0).getLon());
    }


}
