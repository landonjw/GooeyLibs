package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.internal.updates.ContainerUpdater;

public interface IPage {

	Template getTemplate();

	String getTitle();

	void onOpen(PageAction action);

	void onClose(PageAction action);

	default void update() {
		ContainerUpdater.update(this);
	}

}