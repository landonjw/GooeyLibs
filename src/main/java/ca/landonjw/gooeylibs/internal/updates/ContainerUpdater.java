package ca.landonjw.gooeylibs.internal.updates;

import ca.landonjw.gooeylibs.api.page.IPage;
import ca.landonjw.gooeylibs.internal.inventory.GooeyContainer;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class ContainerUpdater {

	private static Map<IPage, Set<GooeyContainer>> pageObservers = Maps.newHashMap();

	public static void register(IPage page, GooeyContainer container) {
		if(!pageObservers.containsKey(page)) {
			pageObservers.put(page, Sets.newHashSet());
		}

		if(pageObservers.get(page).contains(container)) return;

		pageObservers.get(page).add(container);
	}

	public static void unregister(IPage page, GooeyContainer container) {
		if(!pageObservers.containsKey(page)) return;

		pageObservers.get(page).remove(container);
		if(pageObservers.get(page).isEmpty()) {
			pageObservers.remove(page);
		}
	}

	public static void update(IPage page) {
		if(!pageObservers.containsKey(page)) return;

		for(GooeyContainer container : pageObservers.get(page)) {
			container.render();
		}
	}

}
