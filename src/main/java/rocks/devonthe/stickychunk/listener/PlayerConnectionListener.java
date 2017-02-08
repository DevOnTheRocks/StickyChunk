/*
 * This file is part of StickyChunk by DevOnTheRocks, licened under GPL-3.0
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
 *
 * Created by Cossacksman on 25/01/2017.
 */
package rocks.devonthe.stickychunk.listener;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.data.DataStore;
import rocks.devonthe.stickychunk.data.User;
import rocks.devonthe.stickychunk.database.IDatabase;

import java.sql.Date;
import java.time.Instant;

public class PlayerConnectionListener {
	DataStore dataStore = StickyChunk.getInstance().getDataStore();
	IDatabase database = StickyChunk.getInstance().getDatabase();
	Logger logger = StickyChunk.getInstance().getLogger();

	@Listener
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = (Player) event.player;
		Date now = (Date) java.util.Date.from(Instant.now());
		User user = dataStore.getUser(player).orElse(new User(player.getUniqueId(), 0, now, now));

		dataStore.getPlayerRegions(user.getUniqueId()).ifPresent(loadedRegions -> loadedRegions.forEach(region -> {
			if (region.getType() == LoadedRegion.ChunkType.PERSONAL)
				region.unForceChunks();
		}));

		// Update the user in case it's an existing user
		dataStore.getUser(player).ifPresent(usr -> usr.setLastSeen(now).update());
		database.saveUserData(user);
	}

	@Listener
	public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		Player player = (Player) event.player;
		Date now = (Date) java.util.Date.from(Instant.now());
		User user = dataStore.getUser(player).orElse(new User(player.getUniqueId(), 0, now, now));

		dataStore.getPlayerRegions(user.getUniqueId()).ifPresent(loadedRegions -> loadedRegions.forEach(region -> {
			if (region.getType() == LoadedRegion.ChunkType.PERSONAL)
				region.forceChunks();
		}));

		// Update the user in case it's an existing user
		dataStore.getUser(player).ifPresent(usr -> usr.setLastSeen(now).update());
		database.saveUserData(user);
	}
}
