package devonthe.rocks.stickychunk.data;

import java.sql.Date;

/**
 * Created by Cossacksman on 25/01/2017.
 */
public class User {
	private int credits;
	private Date joined;
	private Date seen;

	public User(int credits, Date joined, Date seen) {
		this.credits = credits;
		this.joined = joined;
		this.seen = seen;
	}
}
