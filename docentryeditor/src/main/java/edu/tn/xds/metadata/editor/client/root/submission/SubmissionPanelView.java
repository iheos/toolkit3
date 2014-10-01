package edu.tn.xds.metadata.editor.client.root.submission;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree;
import edu.tn.xds.metadata.editor.client.event.MetadataEditorEventBus;
import edu.tn.xds.metadata.editor.client.generics.abstracts.AbstractView;
import edu.tn.xds.metadata.editor.client.resources.AppImages;
import edu.tn.xds.metadata.editor.client.widgets.uploader.FileUploadDialog;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by onh2 on 7/11/2014.
 */
public class SubmissionPanelView extends AbstractView<SubmissionPanelPresenter> {
    private final static TreeStore<SubmissionMenuData> treeStore = new TreeStore<SubmissionMenuData>(SubmissionMenuData.props.key());
    private final static Tree<SubmissionMenuData, String> tree = new Tree<SubmissionMenuData, String>(treeStore, SubmissionMenuData.props.value());
    private final static SubmissionMenuData submissionSetTreeNode = new SubmissionMenuData("subSet", "Submission set");
    private final ToolBar toolbar = new ToolBar();

    private final Menu addMenu = new Menu();
    private final MenuItem addEmptyDocEntry = new MenuItem("Create an empty document entry");
    private final MenuItem addPrefilledDocEntry = new MenuItem("Create a pre-filled document entry");
    private final MenuItem loadDocEntry = new MenuItem("Load a document entry from xml file");

    private final TextButton addDocEntryButton = new TextButton();
    private final TextButton removeDocEntryButton = new TextButton();
    private final TextButton clearDocEntriesButton = new TextButton();
    private final TextButton saveDocEntriesButton = new TextButton();
    private final TextButton helpButton = new TextButton();

    @Inject
    MetadataEditorEventBus eventBus;
    @Inject
    FileUploadDialog fileUploadDialog;

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        Map<String, Widget> map = new HashMap<String, Widget>();
        return map;
    }

    @Override
    protected Widget buildUI() {
        ContentPanel cp = new ContentPanel();
        cp.setHeadingText("Submission");
        cp.setHeaderVisible(true);
        cp.setBorders(false);

        VerticalLayoutContainer vlc = new VerticalLayoutContainer();

        addDocEntryButton.setIcon(AppImages.INSTANCE.add());
        addDocEntryButton.setToolTip("Create a new Document Entry");
        addMenu.add(addEmptyDocEntry);
        addMenu.add(addPrefilledDocEntry);
        addMenu.add(loadDocEntry);
        addDocEntryButton.setMenu(addMenu);
        removeDocEntryButton.setIcon(AppImages.INSTANCE.delete());
        removeDocEntryButton.setToolTip("Remove this Document Entry");
        clearDocEntriesButton.setIcon(AppImages.INSTANCE.clear());
        clearDocEntriesButton.setToolTip("Clear submission set from all document entries");
        saveDocEntriesButton.setIcon(AppImages.INSTANCE.save());
        saveDocEntriesButton.setToolTip("Download xml file with document entries");
        helpButton.setIcon(AppImages.INSTANCE.help());
        helpButton.setToolTip("Help");
        toolbar.add(addDocEntryButton);
        toolbar.add(removeDocEntryButton);
        toolbar.add(clearDocEntriesButton);
        toolbar.add(saveDocEntriesButton);
        toolbar.add(helpButton);
        vlc.add(toolbar);
        buildTreeStore();
        tree.getStyle().setLeafIcon(AppImages.INSTANCE.file());
        tree.expandAll();
        tree.setAutoExpand(true);
        vlc.add(tree);

        cp.setWidget(vlc);

        return cp;
    }

    private void buildTreeStore() {
        if (treeStore.getAll().isEmpty()) {
            treeStore.add(submissionSetTreeNode);
        }
    }

    @Override
    protected void bindUI() {
        addEmptyDocEntry.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                presenter.createNewDocumentEntry();
            }
        });
        removeDocEntryButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                tree.getStore().remove(tree.getSelectionModel().getSelectedItem());
                tree.getSelectionModel().select(tree.getStore().getFirstChild(getSubmissionSetTreeNode()), false);
//                presenter.loadDocumentEntry(tree.getStore().getFirstChild(getSubmissionSetTreeNode()));
            }
        });
        clearDocEntriesButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                tree.getStore().clear();
                buildTreeStore();
            }
        });
        tree.getSelectionModel().addSelectionHandler(new SelectionHandler<SubmissionMenuData>() {
            @Override
            public void onSelection(SelectionEvent<SubmissionMenuData> event) {
                if (!tree.getSelectionModel().getSelectedItem().equals(submissionSetTreeNode)) {
                    presenter.loadDocumentEntry(event.getSelectedItem());
                }
            }
        });
        addPrefilledDocEntry.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> itemSelectionEvent) {
                getPresenter().createPrefilledDocumentEntry();
            }
        });
        loadDocEntry.addSelectionHandler(new SelectionHandler<Item>() {

            @Override
            public void onSelection(SelectionEvent<Item> event) {
                fileUploadDialog.init();
                fileUploadDialog.show();
            }

        });
        saveDocEntriesButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                presenter.doSave();
            }
        });

    }

    /**
     * This method returns the submission set Tree widget.
     *
     * @return submission set tree
     */
    public Tree<SubmissionMenuData, String> getTree() {
        return tree;
    }

    /**
     * This method returns the submission set TreeStore, which contains all the data.
     *
     * @return submission set tree store
     */
    public TreeStore<SubmissionMenuData> getTreeStore() {
        return treeStore;
    }

    /**
     * This method returns the root node of the submissions set tree.
     * It is actually the Submission Set Node.
     *
     * @return Submission Set tree node
     */
    public SubmissionMenuData getSubmissionSetTreeNode() {
        return submissionSetTreeNode;
    }

}
