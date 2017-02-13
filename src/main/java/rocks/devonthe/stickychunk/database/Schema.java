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

public interface Schema {
	static final String CHUNKSCHEMA =	"id			STRING PRIMARY KEY," +
										"owner		STRING," +
										"world 		STRING," +
										"type		STRING," +
										"fromX		INT," +
										"fromZ		INT," +
										"toX		INT," +
										"toZ		INT," +
										"created	DATETIME";

	static final String CHUNKPROPS = "id, owner, world, type, fromX, fromZ, toX, toZ, created";

	static final String USERSCHEMA =	"user		STRING PRIMARY KEY," +
										"seen		DATETIME," +
										"joined		DATETIME";

	static final String USERPROPS = "user, seen, joined";

	static String createChunkSchema() {
		return String.format("CREATE TABLE IF NOT EXISTS chunks (%s)", CHUNKSCHEMA);
	}

	static String getChunkProperties() {
		return CHUNKPROPS;
	}

	static String createUserSchema() {
		return String.format("CREATE TABLE IF NOT EXISTS users (%s)", USERSCHEMA);
	}

	static String getUserProperties() {
		return USERPROPS;
	}
}
