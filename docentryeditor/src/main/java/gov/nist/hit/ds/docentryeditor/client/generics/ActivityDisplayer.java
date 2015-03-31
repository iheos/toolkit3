package gov.nist.hit.ds.docentryeditor.client.generics;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import gov.nist.hit.ds.docentryeditor.client.root.MetadataEditorAppView;

public interface ActivityDisplayer {
	public void display(Widget w, AcceptsOneWidget p, EventBus b);

	public class MetadataEditorAppDisplayer implements ActivityDisplayer {
		@Inject
		MetadataEditorAppView appView;

		@Override
		public void display(Widget w, AcceptsOneWidget p, EventBus b) {
			appView.setCenterDisplay(w);
		}
	}
}