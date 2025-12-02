package application.game_use_cases.item_interaction;

import domain.GamePosition;
import domain.entities.Entity;
import domain.repositories.EntityStorage;

import java.util.function.Predicate;

public class EntityTargetingService {

    private final EntityStorage entityStorage;

    public EntityTargetingService(EntityStorage entityStorage) {
        this.entityStorage = entityStorage;
    }

    /**
     * Returns true if targetPosition is within a cone in front of the origin.
     */
    public boolean isInFront(GamePosition origin,
                             GamePosition forwardDirection,
                             GamePosition targetPosition,
                             float maxDistance,
                             float maxAngleDeg) {

        float minCosAngle = (float) Math.cos(Math.toRadians(maxAngleDeg));

        GamePosition toTarget = new GamePosition(targetPosition).sub(origin);
        float distance = toTarget.len();

        if (distance > maxDistance) {
            return false;
        }

        GamePosition forwardNorm = new GamePosition(forwardDirection).nor();
        GamePosition toTargetNorm = new GamePosition(toTarget).nor();

        float cosAngle = forwardNorm.dot(toTargetNorm);
        return cosAngle >= minCosAngle;
    }

    /**
     * Find the closest entity of a given type that is in front of the origin.
     */
    public <T extends Entity> T findClosestInFront(
        Class<T> type,
        GamePosition origin,
        GamePosition forwardDirection,
        float maxDistance,
        float maxAngleDeg,
        Predicate<T> extraFilter
    ) {
        T best = null;
        float closestDistance = Float.MAX_VALUE;

        for (int id : entityStorage.getAllIds()) {
            Entity e = entityStorage.getEntityByID(id);
            if (!type.isInstance(e)) continue;

            T candidate = type.cast(e);
            GamePosition candidatePos = candidate.getPosition();

            if (!isInFront(origin, forwardDirection, candidatePos, maxDistance, maxAngleDeg)) {
                continue;
            }

            if (!extraFilter.test(candidate)) {
                continue;
            }

            float dist = candidatePos.dst(origin);
            if (dist < closestDistance) {
                closestDistance = dist;
                best = candidate;
            }
        }

        return best;
    }
}
