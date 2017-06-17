package rocks.devonthe.stickychunk.database;

import org.spongepowered.asm.mixin.Implements;

public interface IAccessObject {
	public void save(IStorable entity);
	public <I Implements IStorable> load();
}
