package puregero.multipaper.server;

public class ChunkKey {
    public final String name;
    public final int x;
    public final int z;

    public ChunkKey(String name, int x, int z) {
        this.name = name;
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ChunkKey) {
            return ((ChunkKey) other).name.equals(name)
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

        return name.hashCode() ^ i ^ j;
    }
}
