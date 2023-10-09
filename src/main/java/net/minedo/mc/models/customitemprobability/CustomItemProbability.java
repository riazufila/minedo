package net.minedo.mc.models.customitemprobability;

/**
 * Custom item with its probability to be selected.
 *
 * @param customItemId Custom item ID
 * @param probability  Probability to be selected
 */
public record CustomItemProbability(int customItemId,
                                    double probability) {
}
