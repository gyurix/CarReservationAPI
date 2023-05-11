package gyurix.carreservation.db.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "reservation")
public class ReservationEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carId")
    private CarEntity car;

    @Column
    private LocalDateTime endTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private LocalDateTime startTime;

    @Column
    private Integer userId;
}