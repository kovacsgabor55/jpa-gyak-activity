package activitytracker;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "activities")
public class Activity {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(generator = "activity_id_generator")
    @TableGenerator(name = "activity_id_generator", table = "act_id_gen", pkColumnName = "id_gen ", pkColumnValue = "id_val")
    Long id;

    @Column(name = "start_time", nullable = false)
    LocalDateTime startTime;

    @Column(name = "description", nullable = false, length = 200)
    String desc;

    @Column(name = "activity_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    ActivityType type;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    public Activity(LocalDateTime startTime, String desc, ActivityType type) {
        this.startTime = startTime;
        this.desc = desc;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }
}
