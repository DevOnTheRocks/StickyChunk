import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.UUID;

public class TestDatabase {
	@Test
	public void main() {

	}

	@DatabaseTable(tableName = "userdata")
	public class UserData {
		@DatabaseField(id = true)
		private UUID user;
		private EnumMap<ChunkLoaderType, ArrayList<ChunkLoader>> chunkloaders;

		public UserData() {
			chunkloaders = Maps.newEnumMap(ChunkLoaderType.class);
			this.user = UUID.randomUUID();
		}

		public UUID getUser() {
			return user;
		}

		public void addChunkLoader(ChunkLoader chunkLoader) {
			System.out.println(chunkLoader.getType().toString());
			System.out.println(chunkloaders.size());

			if (chunkloaders.containsKey(chunkLoader.getType())) {
				chunkloaders.get(chunkLoader.getType()).add(chunkLoader);
			} else {
				chunkloaders.put(chunkLoader.getType(), Lists.newArrayList(chunkLoader));
			}
		}
	}

	@DatabaseTable(tableName = "chunkloader")
	public class ChunkLoader {
		@DatabaseField(id = true)
		public UUID id = UUID.randomUUID();
		@DatabaseField
		private UUID worldId = UUID.randomUUID();
		@DatabaseField
		private UUID owner;
		@DatabaseField
		private ChunkLoaderType type = ChunkLoaderType.COMMAND;

		public ChunkLoader() {}

		public ChunkLoader(UUID owner) {
			this.worldId =
			this.owner = owner;
		}

		public UUID getId() {
			return id;
		}

		public UUID getOwner() {
			return owner;
		}

		public String getOwnerString() {
			return owner.toString();
		}

		public UUID getWorldId() {
			return worldId;
		}

		public ChunkLoaderType getType() {
			return type;
		}
	}

	public enum ChunkLoaderType {
		COMMAND,
		BLOCK
	}

	private void constructDatabase() {
		Connection connection = null;

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s%s%s.db", "./", File.separator, "stickychunks"));
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		if (connection != null) {
			try (Statement statement = connection.createStatement()) {
				statement.setQueryTimeout(30);
				statement.executeUpdate("CREATE DATABASE stickychunks");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
