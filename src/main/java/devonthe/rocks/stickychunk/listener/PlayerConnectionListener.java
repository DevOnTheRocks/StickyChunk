package devonthe.rocks.stickychunk.listener;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.spongepowered.api.event.Listener;

/**
 * Created by Cossacksman on 25/01/2017.
 */
public class PlayerConnectionListener {
	@Listener
	public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;


	}
}
