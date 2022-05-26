package me.modify.townyboost.objects;

import com.modify.fundamentum.exceptions.UUIDFormatException;
import com.modify.fundamentum.util.PlugUtil;
import lombok.Getter;
import me.modify.townyboost.data.ConfigHelper;
import me.modify.townyboost.exceptions.BoostFormatException;
import me.modify.townyboost.exceptions.BoostTokenFormatException;

import java.util.UUID;

/**
 * A BoostToken object represents a virtual booster token which a player owns.
 */
public class BoostToken {

    /** The id of the boost */
    @Getter private UUID id;

    /** The boost multiplier.
     *
     * Example:
     * 2x would be multiplier 2.
     * 4x would be multiplier 4.
     *
     */
    @Getter private int multiplier;

    /** The boost type. Currently, only MCMMO and Jobs are supported */
    @Getter private BoostType type;

    public BoostToken(UUID id, BoostType type, int multiplier) {
        this.id = id;
        this.type = type;
        this.multiplier = multiplier;
    }

    /**
     * Converts this boost object into an encoded string.
     * @return encoded boost string
     */
    @Override
    public String toString() {
        return String.format("%s %s %d", id.toString(), type.toString(), multiplier);
    }

    /**
     * Converts encoded boost token string into BoostToken object.
     * @param boostStr encoded boost token string
     * @return boost token object
     *
     * @throws BoostTokenFormatException if the format of the encoded string is invalid.
     */
    public static BoostToken fromString(String boostStr) throws BoostTokenFormatException {
        String[] parts = boostStr.split(" ");

        if (parts.length != 3) {
            throw new BoostTokenFormatException("Corrupted boost token string. Invalid length.");
        }

        UUID id;
        BoostType boosterType;
        int multiplier;

        try {
            id = PlugUtil.parseUUID(parts[0]);
            boosterType = BoostType.valueOf(parts[1].toUpperCase());
            multiplier = Integer.parseInt(parts[2]);

        } catch (UUIDFormatException | IllegalArgumentException e) {
            throw new BoostTokenFormatException("Failed to parse encoded boost token string.");
        }

        return new BoostToken(id, boosterType, multiplier);
    }
}
