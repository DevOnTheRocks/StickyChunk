import org.junit.Test;
import rocks.devonthe.stickychunk.world.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class TestRegion {
	World world = new World();

	@Test
	public void testCalcChunks() {
		prepareTest();

		List<Coordinate> chunks = new ArrayList<Coordinate>();
		int height, width, area, pointerX, pointerZ, lowestX, lowestZ, highestX, highestZ;

		Coordinate from = new Coordinate(-4, -4);
		Coordinate to = new Coordinate(4, 4);

		lowestX = Math.min(from.getX(), to.getX());
		lowestZ = Math.min(from.getZ(), to.getZ());
		highestX = Math.max(from.getX(), to.getX());
		highestZ = Math.max(from.getZ(), to.getZ());

		width = Math.abs(lowestX - highestX);
		height = Math.abs(lowestZ - highestZ);

		area = Math.abs((width + 1) * (height + 1));

		pointerX = lowestX;
		pointerZ = lowestZ;

		for (int i = 0; i <= width; i++) {
			System.out.println(String.format("X: %s", pointerX));
			for (int l = 0; l <= height; l++) {
				System.out.println(String.format("Z: %s", pointerZ));
				world.getChunk(pointerX, pointerZ).ifPresent(chunks::add);
				pointerZ++;
			}

			pointerZ = lowestZ;
			pointerX++;
		}

		Coordinate last = chunks.get(chunks.size() - 1);
		assertTrue(chunks.size() == area && last.getX() == 4 && last.getZ() == 4);
	}

	private void prepareTest() {
		for (int i = -5; i <= 5; i++)
			for (int l = -5; l <= 5; l++)
				world.getWorldChunks().add(new Coordinate(i, l));
	}

	public class World {
		List<Coordinate> worldChunks = new ArrayList<Coordinate>();

		Optional<Coordinate> getChunk(int x, int z) {
			for (Coordinate chunk : worldChunks)
				if (chunk.getX() == x && chunk.getZ() == z)
					return Optional.of(chunk);

			return Optional.empty();
		}

		List<Coordinate> getWorldChunks() {
			return worldChunks;
		}
	}
}