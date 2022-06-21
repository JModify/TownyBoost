package me.modify.townyboost.objects;

import com.modify.fundamentum.exceptions.UUIDFormatException;
import com.modify.fundamentum.util.PlugUtil;
import lombok.Getter;
import me.modify.townyboost.exceptions.BoostFormatException;

import java.util.UUID;

/**
 * A Boost object represents a queued/active booster.
 */
public class Boost {

    /** Duration remaining in seconds. Default booster durations are 4 hours or 14400 seconds */
    @Getter private int duration;

    /** The id of the boost */
    @Getter private UUID id;

    /** ID of the player which started the boost */
    @Getter private UUID playerId;

    /** The boost multiplier.
     *
     * Example:
     * 2x would be multiplier 2.
     * 4x would be multiplier 4.
     *
     */
    @Getter private int multiplier;

    public Boost(UUID id, UUID playerId, int multiplier, int duration) {
        this.id = id;
        this.playerId = playerId;
        this.multiplier = multiplier;
        this.duration = duration;
    }

    public boolean hasExpired() {
        return duration <= 0;
    }

    /**
     * Deducts one second from the duration of the boost.
     */
    public void deductSecond() {
        if (this.duration >= 1) {
            this.duration = duration - 1;
        }
    }

    /**
     * Converts this boost object into an encoded string.
     * @return encoded boost string
     */
    @Override
    public String toString() {
        return String.format("%s %s %d %d", id.toString(), playerId.toString(), multiplier, duration);
    }

    /**
     * Converts encoded boost string into Boost object.
     * @param boostStr encoded boost string
     * @return boost object
     *
     * @throws BoostFormatException if the format of the encoded string is invalid.
     */
    public static Boost fromString(String boostStr) throws BoostFormatException {
        String[] parts = boostStr.split(" ");

        if (parts.length != 4) {
            throw new BoostFormatException("Corrupted boost string. Invalid length.");
        }

        UUID id, playerId;
        int multiplier, duration;

        try {
            id = PlugUtil.parseUUID(parts[0]);
            playerId = PlugUtil.parseUUID(parts[1]);
            multiplier = Integer.parseInt(parts[2]);
            duration = Integer.parseInt(parts[3]);

        } catch (UUIDFormatException | IllegalArgumentException e) {
            throw new BoostFormatException("Failed to parse encoded boost string.");
        }

        return new Boost(id, playerId, multiplier, duration);
    }
}
