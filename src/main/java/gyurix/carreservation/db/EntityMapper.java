package gyurix.carreservation.db;

import gyurix.carreservation.db.entities.CarEntity;
import gyurix.carreservation.db.entities.ReservationEntity;
import gyurix.carreservation.dto.CarDTO;
import gyurix.carreservation.dto.ReservationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface EntityMapper {
    CarDTO toCarDTO(CarEntity entity);

    CarEntity toCarEntity(CarDTO dto);

    @Mapping(source = "entity.car.id", target = "carId")
    ReservationDTO toReservationDTO(ReservationEntity entity);
}