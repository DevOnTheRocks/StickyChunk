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
package rocks.devonthe.stickychunk.permission;

import org.spongepowered.api.command.CommandSource;

public class Permissions {
	// User permissions
	public static final String COMMAND_HELP = "stickychunk.command.help";
	public static final String COMMAND_LIST = "stickychunk.command.list";
	public static final String COMMAND_CREATE = "stickychunk.command.create";
	public static final String COMMAND_CREATE_PERSONAL = "stickychunk.command.create.personal";
	public static final String COMMAND_CREATE_WORLD = "stickychunk.command.create.world";
	public static final String COMMAND_DELETE = "stickychunk.command.delete";
	public static final String COMMAND_DELETE_PERSONAL = "stickychunk.command.delete.personal";
	public static final String COMMAND_DELETE_WORLD = "stickychunk.command.delete.world";
	public static final String COMMAND_DELETE_ALL = "stickychunk.command.deleteall";
	public static final String COMMAND_RELEASE = "stickychunk.command.release";
	public static final String PLAYER_SELECT_REGION = "stickychunk.command.select";

	// Admin Commands
	public static final String COMMAND_LIST_OTHERS = "stickychunk.admin.list";
	public static final String COMMAND_LIST_ALL = "stickchunk.admin.listall";
	public static final String COMMAND_REGION_TELEPORT = "stickychunk.admin.teleport";

	public static boolean hasPermission(CommandSource src, String permission) {
		return src.hasPermission(permission);
	}
}
