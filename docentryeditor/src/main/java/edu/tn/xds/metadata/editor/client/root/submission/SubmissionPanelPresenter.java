package edu.tn.xds.metadata.editor.client.root.submission;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import edu.tn.xds.metadata.editor.client.MetadataEditorRequestFactory;
import edu.tn.xds.metadata.editor.client.editor.EditorPlace;
import edu.tn.xds.metadata.editor.client.event.*;
import edu.tn.xds.metadata.editor.client.generics.abstracts.AbstractPresenter;
import edu.tn.xds.metadata.editor.client.parse.PreParse;
import edu.tn.xds.metadata.editor.client.parse.XdsParser;
import edu.tn.xds.metadata.editor.client.resources.AppResources;
import edu.tn.xds.metadata.editor.shared.model.String256;
import edu.tn.xds.metadata.editor.shared.model.XdsDocumentEntry;

import javax.inject.Inject;

/**
 * Created by onh2 on 7/11/2014.
 */
public class SubmissionPanelPresenter extends AbstractPresenter<SubmissionPanelView> {
    @Inject
    PlaceController placeController;
    @Inject
    XdsParser xdsParser;
    @Inject
    MetadataEditorRequestFactory requestFactory;

    private SubmissionMenuData currentlyEdited;
//    private final static TextResource prefillData = AppResources.INSTANCE.xdsPrefill();
    private int nextIndex = 1;
    private XdsDocumentEntry prefilledDocEntry;

    @Override
    public void init() {
        bind();
        requestFactory.initialize(eventBus);
    }

    private void bind() {
        ((MetadataEditorEventBus) getEventBus()).addFileLoadedHandler(new NewFileLoadedEvent.NewFileLoadedHandler() {
            @Override
            public void onNewFileLoaded(NewFileLoadedEvent event) {
                currentlyEdited = new SubmissionMenuData("DocEntry" + nextIndex, "Document Entry " + nextIndex, event.getDocument());
                nextIndex++;
                view.getTreeStore().add(view.getTreeStore().getRootItems().get(0), currentlyEdited);
                view.getTree().expandAll();
                view.getTree().getSelectionModel().select(currentlyEdited, false);
                getEventBus().fireEvent(new StartEditXdsDocumentEvent(currentlyEdited.getModel()));
            }
        });
        ((MetadataEditorEventBus) getEventBus()).addEditNewEventHandler(new EditNewEvent.EditNewHandler() {
            @Override
            public void onEditNew(EditNewEvent event) {
                if (view.getTree().getStore().getChildCount(view.getSubmissionSetTreeNode()) < 1) {
                    createNewDocumentEntry();
                }
            }
        });
    }

    /**
     * This method creates a new Document Entry and adds it to the submissionSet tree.
     */
    public void createNewDocumentEntry() {
        logger.info("Create new document entry");
        currentlyEdited = new SubmissionMenuData("DocEntry" + nextIndex, "Document Entry " + nextIndex, new XdsDocumentEntry());
        nextIndex++;
        view.getTreeStore().add(view.getTreeStore().getRootItems().get(0), currentlyEdited);
        view.getTree().expandAll();
        view.getTree().getSelectionModel().select(currentlyEdited, false);
        if (!(placeController.getWhere() instanceof EditorPlace)) {
            placeController.goTo(new EditorPlace());
        }
        ((MetadataEditorEventBus) eventBus).fireStartEditXdsDocumentEvent(new StartEditXdsDocumentEvent(currentlyEdited.getModel()));
    }

    /**
     * This method loads the editor interface with a selected document entry from the submission set tree.
     *
     * @param selectedItem tree selected node
     */
    public void loadDocumentEntry(SubmissionMenuData selectedItem) {
        if (!selectedItem.equals(view.getSubmissionSetTreeNode())) {
            currentlyEdited = selectedItem;
            ((MetadataEditorEventBus) eventBus).fireStartEditXdsDocumentEvent(new StartEditXdsDocumentEvent(currentlyEdited.getModel()));
        }
    }

    public void createPrefilledDocumentEntry() {
        if (prefilledDocEntry==null) {
            prefilledDocEntry = xdsParser.parse(PreParse.getInstance().doPreParse(AppResources.INSTANCE.xdsPrefill().getText()));
            prefilledDocEntry.setFileName(new String256("new-doc-entry"));
        }
        //------------------------------------------- MIGHT CHANGE
        logger.info("Create new document entry");
        currentlyEdited = new SubmissionMenuData("DocEntry" + nextIndex, "Document Entry " + nextIndex, prefilledDocEntry);
        nextIndex++;
        view.getTreeStore().add(view.getTreeStore().getRootItems().get(0), currentlyEdited);
        view.getTree().expandAll();
        view.getTree().getSelectionModel().select(currentlyEdited, false);
        if (!(placeController.getWhere() instanceof EditorPlace)) {
            placeController.goTo(new EditorPlace());
        }
        ((MetadataEditorEventBus) eventBus).fireStartEditXdsDocumentEvent(new StartEditXdsDocumentEvent(currentlyEdited.getModel()));
    }

    public void doSave() {
        ((MetadataEditorEventBus) eventBus).fireSaveFileEvent(new SaveFileEvent());
        ((MetadataEditorEventBus) eventBus).addSaveCurrentlyEditedDocumentHandler(new SaveCurrentlyEditedDocumentEvent.SaveCurrentlyEditedDocumentEventHandler() {
            @Override
            public void onSaveCurrentlyEditedDocumentEvent(SaveCurrentlyEditedDocumentEvent event) {
                currentlyEdited.setModel(event.getDocumentEntry());
                save();
            }
        });
    }

    private void save() {
//        String xmlFileContent=new String();
//        for (SubmissionMenuData submissionMenuData:view.getTreeStore().getAll()){
//            if (submissionMenuData.getValue().startsWith("Document")){
//                xmlFileContent+=submissionMenuData.getModel().toXML()+"\n";
//                logger.info(xmlFileContent);
//            }
//        }
//        xmlFileContent.replaceAll("<\\?xml version=\"1\\.0\" encoding=\"UTF-8\"\\?>"," ");
//        logger.info(xmlFileContent);
//        if (!xmlFileContent.isEmpty()) {
            requestFactory.saveFileRequestContext().saveAsXMLFile(currentlyEdited.getModel().getFileName().toString(), currentlyEdited.getModel().toXML()).fire(new Receiver<String>() {

                @Override
                public void onSuccess(String response) {
                    logger.info("saveAsXMLFile call succeed");
                    logger.info("Generated filename sent to the client \n" + response);
                    logger.info("File's URL: " + GWT.getHostPageBaseURL() + "files/" + response);
                    Window.open(GWT.getHostPageBaseURL() + "files/" + response, response + " Metadata File", "enabled");
                    Dialog d = new Dialog();
                    HTMLPanel htmlP = new HTMLPanel("<a href='" + GWT.getHostPageBaseURL() + "files/" + response + "'>"
                            + GWT.getHostPageBaseURL() + "files/" + response + "</a>");
                    VerticalLayoutContainer vp = new VerticalLayoutContainer();
                    vp.add(new Label("Your download is in progress, please allow this application to open popups with your browser..."),
                            new VerticalLayoutContainer.VerticalLayoutData(1, 1, new Margins(10, 5, 10, 5)));
                    vp.add(htmlP, new VerticalLayoutContainer.VerticalLayoutData(1, 1, new Margins(10, 5, 10, 5)));
                    d.add(vp);

                    d.setPredefinedButtons(Dialog.PredefinedButton.OK);
                    d.setButtonAlign(BoxLayoutContainer.BoxLayoutPack.CENTER);
                    d.setHideOnButtonClick(true);
                    d.setHeadingText("XML Metadata File Download");
                    d.show();
                }
            });
//        }
    }
}
