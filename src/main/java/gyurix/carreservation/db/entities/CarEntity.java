package gyurix.carreservation.db.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Data
@Table(name = "car")
public class CarEntity {
    @Id
    @Column
    private String id;

    @Column
    private String make;

    @Column
    private String model;

    @OneToMany(mappedBy = "car", fetch = FetchType.LAZY)
    private List<ReservationEntity> reservations;
}