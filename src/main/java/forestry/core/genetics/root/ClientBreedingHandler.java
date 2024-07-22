package forestry.core.genetics.root;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IBreedingTrackerHandler;

public class ClientBreedingHandler extends ServerBreedingHandler {
	private final Map<String, IBreedingTracker> trackerByUID = new LinkedHashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBreedingTracker> T getTracker(String rootUID, LevelAccessor level, @Nullable GameProfile profile) {
		if (level instanceof ServerLevel) {
			return super.getTracker(rootUID, level, profile);
		}
		IBreedingTrackerHandler handler = BreedingTrackerManager.factories.get(rootUID);
		T tracker = (T) trackerByUID.computeIfAbsent(rootUID, (key) -> handler.createTracker());
		handler.populateTracker(tracker, Minecraft.getInstance().level, profile);
		return tracker;
	}
}
