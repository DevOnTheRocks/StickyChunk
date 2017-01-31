package rocks.devonthe.stickychunk.world;

import com.flowpowered.math.vector.Vector3i;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class Coordinate {
    private int x;
    private int z;

    public Coordinate(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public Coordinate(Vector3i point) {
        this.x = point.getX();
        this.z = point.getZ();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
