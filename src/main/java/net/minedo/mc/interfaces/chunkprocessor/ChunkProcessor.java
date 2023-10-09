package net.minedo.mc.interfaces.chunkprocessor;

/**
 * Chunk processor interface.
 */
public interface ChunkProcessor {

    /**
     * Process chunk details to get or create snapshots.
     *
     * @param params params
     * @return whether process fails or not
     */
    boolean process(Object[] params);

}
