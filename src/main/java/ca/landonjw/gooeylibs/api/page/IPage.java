package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.template.ITemplate;
import ca.landonjw.gooeylibs.internal.updates.ContainerUpdater;

public interface IPage {

	ITemplate getTemplate();

	String getTitle();

	void onOpen(PageAction action);

	void onClose(PageAction action);

	IPage clone();

	default void update() {
		ContainerUpdater.update(this);
	}

}