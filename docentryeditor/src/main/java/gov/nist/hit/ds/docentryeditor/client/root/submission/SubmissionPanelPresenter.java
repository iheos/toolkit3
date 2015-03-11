package gov.nist.hit.ds.docentryeditor.client.root.submission;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.hit.ds.docentryeditor.client.editor.submissionSetEditor.SubmissionSetEditorPlace;
import gov.nist.hit.ds.docentryeditor.client.parser.XdsParserServices;
import gov.nist.hit.ds.docentryeditor.client.parser.XdsParserServicesAsync;
import gov.nist.hit.ds.docentryeditor.client.utils.MetadataEditorRequestFactory;
import gov.nist.hit.ds.docentryeditor.client.editor.documentEntryEditor.DocEntryEditorPlace;
import gov.nist.hit.ds.docentryeditor.client.event.*;
import gov.nist.hit.ds.docentryeditor.client.generics.abstracts.AbstractPresenter;
import gov.nist.hit.ds.docentryeditor.client.home.WelcomePlace;
import gov.nist.hit.ds.docentryeditor.client.parser.PreParse;
import gov.nist.hit.ds.docentryeditor.client.parser.XdsParser;
import gov.nist.hit.ds.docentryeditor.client.resources.AppResources;
import gov.nist.hit.ds.docentryeditor.server.XdsMetadataParserServicesImpl;
import gov.nist.hit.ds.docentryeditor.shared.model.*;

import javax.inject.Inject;

/**
 * This class presents the submission panel. It handles the mechanic of the submission set tree of the SubmissionPanelView.
 *
 * @see gov.nist.hit.ds.docentryeditor.client.root.submission.SubmissionPanelView
 * @see gov.nist.hit.ds.docentryeditor.client.root.submission.SubmissionMenuData
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
    private int nextIndex = 1;
    private XdsDocumentEntry prefilledDocEntry;

    // RPC services declaration
    private final static XdsParserServicesAsync xdsParserServices = GWT
            .create(XdsParserServices.class);

    @Override
    public void init() {
        bind();
        requestFactory.initialize(eventBus);
    }

    /**
     * Method that handles the different event fired in the event bus.
     */
    private void bind() {
        // this event catches handle the navigation back to the home page.
        ((MetadataEditorEventBus) getEventBus()).addBackToHomePageEventHandler(new BackToHomePageEvent.BackToHomePageEventHandler() {
            @Override
            public void onBackToHomePage(BackToHomePageEvent event) {
                getView().getTree().getSelectionModel().deselectAll();
                placeController.goTo(new WelcomePlace());
            }
        });
        // this event catches that a Document entry has been loaded from the user's file system.
        ((MetadataEditorEventBus) getEventBus()).addNewFileLoadedHandler(new NewFileLoadedEvent.NewFileLoadedHandler() {
            @Override
            public void onNewFileLoaded(NewFileLoadedEvent event) {
                view.getTreeStore().getRootItems().get(0).setModel(event.getMetadata().getSubmissionSet());
                currentlyEdited = new SubmissionMenuData("DocEntry" + nextIndex, "Document Entry " + nextIndex, event.getMetadata().getDocumentEntries().get(0));
                nextIndex++;
                view.getTreeStore().add(view.getTreeStore().getRootItems().get(0), currentlyEdited);
                view.getTree().expandAll();
                view.getTree().getSelectionModel().select(currentlyEdited, false);
            }
        });
        // this event catches that a Document entry has been loaded from the user's file system.
        ((MetadataEditorEventBus) getEventBus()).addCreateNewDocEntryEventHandler(new CreateNewDocEntryEvent.CreateNewDocEntryEventHandler() {
            @Override
            public void onCreateNewDocumentEntry(CreateNewDocEntryEvent event) {
                currentlyEdited = new SubmissionMenuData("DocEntry" + nextIndex, "Document Entry " + nextIndex, event.getDocument());
                nextIndex++;
                view.getTreeStore().add(view.getTreeStore().getRootItems().get(0), currentlyEdited);
                view.getTree().expandAll();
                view.getTree().getSelectionModel().select(currentlyEdited, false);
            }
        });
        // this catches that the XDS Document entry editor view has loaded.
        ((MetadataEditorEventBus) getEventBus()).addXdsEditorLoadedEventtHandler(new XdsEditorLoadedEvent.XdsEditorLoadedEventHandler() {
            @Override
            public void onXdsEditorLoaded(XdsEditorLoadedEvent event) {
                logger.info("... receive Doc. Entry Editor loaded event.");
                if (currentlyEdited != null) {
                    // if a doc. entry is currently under edition, an event is fired to transfer it to the editor.
                    logger.info("A document is already selected. Loading it...");
                    ((MetadataEditorEventBus) getEventBus()).fireStartEditXdsDocumentEvent((XdsDocumentEntry) currentlyEdited.getModel());
                } else {
                    // if no doc. entry is currently under edition, it means the app (editor view) has been loaded from
                    // by its URL from the browser navigation bar (external link).
                    logger.info("No Document Entry in Submission Set");
                    // a new doc. entry is create in the submission tree.
                    createNewDocumentEntry();
                }
            }
        });
        // this catches that a new pre-filled doc. entry creation has been required
        // from another place than the submission panel.
        ((MetadataEditorEventBus) getEventBus()).addLoadPreFilledDocEntryEventHandler(new LoadPrefilledDocEntryEvent.LoadPrefilledDocEntryEventHandler() {
            @Override
            public void onLoadPrefilledDocEntryHandler(LoadPrefilledDocEntryEvent event) {
                createPreFilledDocumentEntry();
            }
        });
        ((MetadataEditorEventBus) eventBus).addSaveCurrentlyEditedMetadataHandler(new SaveCurrentlyEditedMetadataEvent.SaveCurrentlyEditedMetadataEventHandler() {
            @Override
            public void onSaveCurrentlyEditedDocumentEvent(SaveCurrentlyEditedMetadataEvent event) {
//                currentlyEdited.setModel(event.getDocumentEntry());
                //  save(); // not done yet
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
    }

    /**
     * This method loads the adequate editor interface with the selected entry
     * from the submission tree. It can be a XdsSubmissionSet, a XdsDocumentEntry
     * or a XdsAssociation.
     *
     * @param selectedItem selected tree node
     */
    public void loadSelectedEntryEditor(SubmissionMenuData selectedItem) {
        currentlyEdited = selectedItem;
        startEditing();
    }

    /**
     * This method load the editor user interface with a pre-filled document entry which is added to the submission set tree.
     */
    public void createPreFilledDocumentEntry() {
        if (prefilledDocEntry==null) {
            prefilledDocEntry = xdsParser.parse(PreParse.getInstance().doPreParse(AppResources.INSTANCE.xdsPrefill().getText()));
            prefilledDocEntry.setFileName(new String256("new-doc-entry"));
        }
        //------------------------------------------- MIGHT CHANGE
        logger.info("Create new pre-filled document entry");
        XdsDocumentEntry newDoc=prefilledDocEntry.copy();
        currentlyEdited = new SubmissionMenuData("DocEntry" + nextIndex, "Document Entry " + nextIndex, newDoc);
        nextIndex++;
        view.getTreeStore().add(view.getTreeStore().getRootItems().get(0), currentlyEdited);
        view.getTree().expandAll();
        view.getTree().getSelectionModel().select(currentlyEdited, false);
    }

    public void doSave() {
//        ((MetadataEditorEventBus) eventBus).fireSaveFileEvent();
        XdsMetadata m=new XdsMetadata();
        m.setSubmissionSet((XdsSubmissionSet) view.getSubmissionSetTreeNode().getModel());
        for (SubmissionMenuData subData:view.getTreeStore().getChildren(view.getSubmissionSetTreeNode())){
            if (subData.getModel() instanceof XdsDocumentEntry) {
                m.getDocumentEntries().add((XdsDocumentEntry) subData.getModel());
            }
        }
        xdsParserServices.toEbRim(m, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.warning(throwable.getMessage());
            }

            @Override
            public void onSuccess(String s) {
                // TODO
            }
        });
    }

    /**
     * This method loads a document entry into the editor user interface, which is loaded if not already.
     */
    private void startEditing() {
        if(currentlyEdited.getModel() instanceof XdsDocumentEntry) {
            if (!(placeController.getWhere() instanceof DocEntryEditorPlace)) {
                placeController.goTo(new DocEntryEditorPlace());
            }
            logger.info("Fire Start Edit selected (" + currentlyEdited.getValue() + ") document entry event...");
            ((MetadataEditorEventBus) getEventBus()).fireStartEditXdsDocumentEvent((XdsDocumentEntry) currentlyEdited.getModel());
        }else if(currentlyEdited.getModel() instanceof XdsSubmissionSet){
            if(!(placeController.getWhere() instanceof SubmissionSetEditorPlace)){
                placeController.goTo(new SubmissionSetEditorPlace());
            }
            logger.info("Fire Start Edit selected (" + currentlyEdited.getValue() + ") submission set event...");
            logger.info(currentlyEdited.getModel().toString());
            ((MetadataEditorEventBus) getEventBus()).fireStartEditXdsSubmissionSetEvent((XdsSubmissionSet) currentlyEdited.getModel());
        }
    }

    /**
     * Getter that return the entity currently under edition.
     * @return SubmissionMenuData.
     */
    public SubmissionMenuData getCurrentlyEdited(){
        return currentlyEdited;
    }


}
