package rocks.devonthe.stickychunk.data;

import rocks.devonthe.stickychunk.StickyChunk;

import java.sql.Date;
import java.util.UUID;

/**
 * Created by Cossacksman on 25/01/2017.
 */
public class User {
	UUID player;
	private Date seen;
	private Date joined;
	private double balance;

	public User(UUID id, double balance, Date joined, Date seen) {
		this.player = id;
		this.seen = seen;
		this.joined = joined;
		this.balance = balance;
	}

	public UUID getUniqueId() {
		return player;
	}

	public Date getLastSeen() {
		return seen;
	}

	public User setLastSeen(Date seen) {
		this.seen = seen;
		return this;
	}

	public Date getUserJoined() {
		return joined;
	}

	public double getBalance() {
		return balance;
	}

	public void update() {
		StickyChunk.getInstance().getDataStore().updateUser(this);
	}
}
