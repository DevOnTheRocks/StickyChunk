package rocks.devonthe.stickychunk.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import rocks.devonthe.stickychunk.StickyChunk;

import java.io.IOException;

/**
 * Created by Cossacksman on 22/01/2017.
 */
public class ConfigManager {
	private final StickyChunk PLUGIN = StickyChunk.getInstance();
	private Logger logger = PLUGIN.getLogger();

	private ObjectMapper<StickyChunkConfig>.BoundInstance configMapper;
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> loader) {
		this.loader = loader;
		try {
			this.configMapper = ObjectMapper.forObject(PLUGIN.getConfig());
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}

		this.load();
	}

	/***
	 * Saves the serialized config to file
	 */
	public void save() {
		try {
			SimpleCommentedConfigurationNode out = SimpleCommentedConfigurationNode.root();
			this.configMapper.serialize(out);
			this.loader.save(out);
		} catch (ObjectMappingException | IOException e) {
			logger.error(String.format("Failed to save config.\r\n %s", e.getMessage()));
		}
	}

	/***
	 * Loads the configs into serialized objects, for the configMapper
	 */
	public void load() {
		try {
			this.configMapper.populate(this.loader.load());
		} catch (ObjectMappingException | IOException e) {
			logger.error(String.format("Failed to load config.\r\n %s", e.getMessage()));
		}
	}
}