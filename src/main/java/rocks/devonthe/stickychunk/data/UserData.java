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
package rocks.devonthe.stickychunk.data;

import org.spongepowered.api.service.economy.account.UniqueAccount;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.economy.EconomyManager;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;
import java.util.UUID;

public class UserData {
	UUID player;
	private Date seen;
	private Date joined;
	private UniqueAccount account;

	private EconomyManager economyManager = StickyChunk.getInstance().getEconomyManager();

	public UserData(UUID id, BigDecimal balance, Date joined, Date seen) {
		this.player = id;
		this.seen = seen;
		this.joined = joined;

		Optional<UniqueAccount> oAccount = economyManager.getOrCreateAccount(id);
		if (oAccount.isPresent()) {
			this.account = oAccount.get();
		} else {
			// WAT DO!?
		}
	}

	public UUID getUniqueId() {
		return player;
	}

	public Date getLastSeen() {
		return seen;
	}

	public UserData setLastSeen(Date seen) {
		this.seen = seen;
		return this;
	}

	public Date getUserJoined() {
		return joined;
	}

	public BigDecimal getBalance() {
		return account.getBalance(economyManager.getCurrency());
	}

	public void update() {
		StickyChunk.getInstance().getDataStore().updateUser(this);
	}
}
