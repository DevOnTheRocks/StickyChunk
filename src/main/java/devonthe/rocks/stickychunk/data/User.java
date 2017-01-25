package devonthe.rocks.stickychunk.data;

import java.sql.Date;
import java.util.UUID;

/**
 * Created by Cossacksman on 25/01/2017.
 */
public class User {
	UUID player;
	private Date seen;
	private Date joined;
	private int credits;

	public User(UUID id, int credits, Date joined, Date seen) {
		this.player = id;
		this.seen = seen;
		this.joined = joined;
		this.credits = credits;
	}

	public UUID getUniqueId() {
		return player;
	}

	public Date getLastSeen() {
		return seen;
	}

	public void setLastSeen(Date seen) {
		this.seen = seen;
	}

	public Date getUserJoined() {
		return joined;
	}

	public int getCredits() {
		return credits;
	}
}
