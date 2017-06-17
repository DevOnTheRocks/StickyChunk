package rocks.devonthe.stickychunk.database;

import java.util.HashMap;
import java.util.Map;

public class EntityManager {
	private Map<Class, IAccessObject> accessObjects;

	public EntityManager() {
		accessObjects = new HashMap<>();

	}

	public<C implements IAccessObject> void addAccessObject(C accessObject, Class clazz) {
		accessObjects.put(clazz, accessObject);
	}
}
