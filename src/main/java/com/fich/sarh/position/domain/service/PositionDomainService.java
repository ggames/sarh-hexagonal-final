package com.fich.sarh.position.domain.service;

import com.fich.sarh.position.domain.model.Position;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class PositionDomainService {
    public List<Position> calculatePosition(List<Position> positions, Long amountPoint) {

        if (positions == null || positions.isEmpty()) {
            return positions;
        }

        // Usamos una variable local para no modificar el parámetro directamente
        long remainingToDeduct = amountPoint;

        for (int i = 0; i < positions.size(); i++) {
            Position originator = positions.get(i);
            long totalItemPoints = originator.getPoint().getAmountPoint();

            // Calculamos cuántos puntos reales hay disponibles actualmente en esta posición
            // Si pointsAvailable es un porcentaje (0-100)
            long currentPointsReal = Math.round((totalItemPoints * originator.getPointsAvailable()) / 100.0);

            if (remainingToDeduct <= 0) {
                break; // No hay más que descontar
            }

            if (remainingToDeduct >= currentPointsReal) {
                // El gasto es mayor o igual a lo que tiene esta posición: queda en 0
                remainingToDeduct -= currentPointsReal;
                originator.setPointsAvailable(0L);

                log.info("Posición vaciada. Restante por descontar: " + remainingToDeduct);
            } else {
                // El gasto es menor a lo que tiene la posición: restamos y calculamos nuevo %
                long pointsAfterDeduction = currentPointsReal - remainingToDeduct;

                // Calculamos el nuevo porcentaje de forma precisa
                // Multiplicamos por 100.0 para forzar punto flotante antes de la división
                long newPercent = Math.round(((double) pointsAfterDeduction / totalItemPoints) * 100);

                originator.setPointsAvailable(newPercent);
                remainingToDeduct = 0;

                log.info("Deducción parcial. Nuevo porcentaje: " + newPercent);
                break;
            }
        }

        return positions;
    }
}
