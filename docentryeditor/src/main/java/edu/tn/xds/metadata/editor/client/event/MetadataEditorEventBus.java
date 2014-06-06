package edu.tn.xds.metadata.editor.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

import edu.tn.xds.metadata.editor.client.event.NewFileLoadedEvent.NewFileLoadedHandler;
import edu.tn.xds.metadata.editor.client.event.SaveFileEvent.SaveFileEventHandler;

public class MetadataEditorEventBus extends SimpleEventBus {
	// TODO personalized handlers
	public HandlerRegistration addFileLoadedHandler(NewFileLoadedHandler handler) {
		return addHandler(NewFileLoadedEvent.TYPE, handler);
	}

	public void fireNewFileLoadedEvent(NewFileLoadedEvent event) {
		fireEvent(event);
	}

	public HandlerRegistration addSaveFileEventHandler(SaveFileEventHandler handler) {
		return addHandler(SaveFileEvent.TYPE, handler);
	}

	public void fireSaveFileEvent(SaveFileEvent event) {
		fireEvent(event);
	}
}
