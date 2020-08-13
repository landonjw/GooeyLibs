package ca.landonjw.gooeylibs.api.page;

import ca.landonjw.gooeylibs.api.template.ITemplate;

public interface IPage {

	ITemplate getTemplate();

	String getTitle();

	void onOpen(PageAction action);

	void onClose(PageAction action);

}