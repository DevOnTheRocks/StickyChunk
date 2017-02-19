/*
 * This file is part of StickyChunk by DevOnTheRocks, licensed under GPL-3.0
 *
 * Copyright (C) 2017 DevOnTheRocks
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * The above notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package rocks.devonthe.stickychunk.database;

import org.slf4j.Logger;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.config.database.SqliteConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteDatabase extends SqlDatabase {
	private SqliteConfig config;
	private String databaseLocation;
	private String databaseName;
	private Connection connection;

	private Logger logger;

	public SqliteDatabase() {
		config = StickyChunk.getInstance().getConfig().database.sqlite;
		databaseLocation = config.location;
		databaseName = config.databaseName;

		logger = StickyChunk.getInstance().getLogger();

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(
				String.format("jdbc:sqlite:%s%s%s.db", databaseLocation, File.separator, databaseName));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("Unable to load the JDBC driver");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Unable to connect to the database.");
		}

		try (Statement statement = getConnection().createStatement()) {
			statement.setQueryTimeout(30);
			statement.executeUpdate(Schema.createChunkSchema());
			statement.executeUpdate(Schema.createUserSchema());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Unable to create StickyChunk database");
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connection;
	}
}
