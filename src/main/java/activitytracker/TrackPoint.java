package activitytracker;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "track_points")
public class TrackPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate time;
    private double lat;
    private double lon;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Activity activity;

    public TrackPoint(LocalDate time, double lat, double lon) {
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }
}
