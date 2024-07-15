/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

import forestry.Forestry;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.IForestryModule;
import forestry.api.modules.IModuleManager;
import forestry.core.utils.ModUtil;

import org.objectweb.asm.Type;

public class ForestryModuleManager implements IModuleManager {
	private final LinkedHashMap<ResourceLocation, IForestryModule> loadedModules = new LinkedHashMap<>();
	private final LinkedHashMap<String, List<IForestryModule>> loadedModulesByMod = new LinkedHashMap<>();

	@Override
	public Collection<IForestryModule> getLoadedModules() {
		return Collections.unmodifiableCollection(this.loadedModules.values());
	}

	@Override
	public boolean isModuleLoaded(ResourceLocation id) {
		return this.loadedModules.containsKey(id);
	}

	@Override
	public List<IForestryModule> getModulesForMod(String modId) {
		return Collections.unmodifiableList(this.loadedModulesByMod.get(modId));
	}

	private void loadModules() {
		LinkedHashMap<String, List<IForestryModule>> discoveredModules = discoverModules();
		HashSet<ResourceLocation> discoveredIds = new HashSet<>();
		LinkedList<IForestryModule> modulesToLoad = new LinkedList<>();

		for (List<IForestryModule> modModules : discoveredModules.values()) {
			for (IForestryModule module : modModules) {
				discoveredIds.add(module.getId());
				modulesToLoad.add(module);
			}
		}

		// check dependencies and skip loading of modules whose dependencies are missing
		Iterator<IForestryModule> iterator;
		boolean changed;
		do {
			changed = false;
			iterator = modulesToLoad.iterator();

			while (iterator.hasNext()) {
				IForestryModule module = iterator.next();
				List<ResourceLocation> dependencies = module.getModuleDependencies();
				List<String> modDependencies = module.getModDependencies();

				if (discoveredIds.containsAll(dependencies)) {
					for (String modId : modDependencies) {
						if (!ModList.get().isLoaded(modId)) {
							Forestry.LOGGER.warn("Module {} is missing mod dependencies: {}", module.getId(), modDependencies);
						}
					}
					// if all dependency mods are loaded, skip removal code
					continue;
				} else {
					Forestry.LOGGER.warn("Module {} is missing dependencies: {}", module.getId(), dependencies);
				}

				// remove from loaded modules
				iterator.remove();
				changed = true;
				discoveredIds.remove(module.getId());
			}
		} while (changed);

		// sort modules in LOAD order
		do {
			changed = false;
			iterator = modulesToLoad.iterator();
			while (iterator.hasNext()) {
				IForestryModule module = iterator.next();

				if (this.loadedModules.keySet().containsAll(module.getModuleDependencies())) {
					iterator.remove();
					this.loadedModules.put(module.getId(), module);
					this.loadedModulesByMod.computeIfAbsent(module.getId().getNamespace(), modId -> new ArrayList<>()).add(module);
					changed = true;
					break;
				}
			}
		} while (changed);
	}

	// Called during mod construction by Forestry
	public void init() {
		loadModules();


		for (Map.Entry<ResourceLocation, IForestryModule> entry : this.loadedModules.entrySet()) {
			IEventBus modBus = modBuses.computeIfAbsent(entry.getKey().getNamespace(), modid -> ModuleUtil.getModBus(modid));
			IForestryModule module = entry.getValue();

			module.registerEvents(modBus);

			if (FMLEnvironment.dist == Dist.CLIENT) {
				module.registerClientHandler(handler -> handler.registerEvents(modBus));
			}
		}
	}

	// Returns a map of mod IDs to modules, with core modules first in each mod list
	private static LinkedHashMap<String, List<IForestryModule>> discoverModules() {
		LinkedHashMap<String, List<IForestryModule>> modules = new LinkedHashMap<>();

		ModuleUtil.forEachAnnotated(Type.getType(ForestryModule.class), klass -> {
			IForestryModule module;
			try {
				module = klass.asSubclass(IForestryModule.class).getConstructor().newInstance();
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Failed to instantiate module class " + klass.getName(), e);
			}
			String modId = module.getId().getNamespace();
			// Namespace of the id must be a modid
			if (!ModUtil.isModLoaded(modId)) {
				throw new RuntimeException("Module " + module.getClass() + " returned '" + module.getId() + "' for its ID namespace, but no mod with ID '" + modId + "' is loaded");
			}
			List<IForestryModule> modModules = modules.computeIfAbsent(modId, k -> new ArrayList<>());
			// Core modules load first
			if (module.isCore()) {
				modModules.add(0, module);
			} else {
				modModules.add(module);
			}
		});

		return modules;
	}
}
