package puregero.multipaper.mastermessagingprotocol;

public class ChunkKey {

    public final String world;

    public final int x;
    public final int z;

    public ChunkKey(String world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ChunkKey) {
            return ((ChunkKey) other).world.equals(world)
                    && ((ChunkKey) other).x == x
                    && ((ChunkKey) other).z == z;
        }

        return super.equals(other);
    }

    @Override
    public int hashCode() {
        // Taken from ChunkCoordIntPair
        int i = 1664525 * this.x + 1013904223;
        int j = 1664525 * (this.z ^ -559038737) + 1013904223;

        return world.hashCode() ^ i ^ j;
    }

    @Override
    public String toString() {
        return world + "," + x + "," + z;
    }
}
