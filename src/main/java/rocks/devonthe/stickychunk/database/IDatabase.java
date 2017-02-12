/*
 * This file is part of StickyChunk by DevOnTheRocks, licensed under GPL-3.0
 *
 * Copyright © 2017 DevOnTheRocks
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

import com.google.common.collect.ImmutableSet;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public interface IDatabase {
	void saveRegionData(LoadedRegion loadedRegion);

	void saveRegionData(ImmutableSet<LoadedRegion> loadedRegions);

	void saveRegionData(ArrayList<LoadedRegion> data);

	HashMap<UUID, ArrayList<LoadedRegion>> loadRegionData();

	void deleteRegionData(LoadedRegion region);

	void deleteRegionData( ArrayList<LoadedRegion> regions);

	void saveUserData(User user);

	void saveUserData(ImmutableSet<User> loadedUsers);

	void saveUserData(ArrayList<User> loadedUsers);

	void deleteUserData(User user);

	void deleteUserData(ArrayList<User> users);

	ArrayList<User> loadUserData();
}
