package gyurix.carreservation.db.repositories;

import gyurix.carreservation.db.entities.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, String> {
    @Query("SELECT c FROM CarEntity c " +
            "WHERE NOT EXISTS (" +
            "   SELECT r FROM c.reservations r " +
            "   WHERE (:endTime > r.startTime AND :startTime < r.endTime)" +
            ") " +
            "ORDER BY c.id")
    List<CarEntity> findAvailableCars(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
