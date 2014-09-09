package gov.nist.toolkit.xdstools3.client.customWidgets.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IconButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.toolbar.RibbonBar;
import com.smartgwt.client.widgets.toolbar.RibbonGroup;
import gov.nist.toolkit.xdstools3.client.customWidgets.loginDialog.LoginDialogWidget;
import gov.nist.toolkit.xdstools3.client.eventBusUtils.OpenTabEvent;
import gov.nist.toolkit.xdstools3.client.util.TabNamesUtil;
import gov.nist.toolkit.xdstools3.client.util.Util;

public class Toolbar extends RibbonBar {

    public Toolbar() {

        setMembersMargin(20);
        setAlign(Alignment.CENTER);
        setStyleName("navbar");

        // Menu group: Session
        RibbonGroup sessionGroup = createRibbonGroup("Session");

        SelectItem listBox = new SelectItem();
        listBox.setShowTitle(false);
        listBox.setShowOptionsFromDataSource(false);
        listBox.setWidth("150");
        listBox.setDefaultToFirstOption(true);

        // populate the environment drop-down
        EnvironmentsMap env = new EnvironmentsMap();
        listBox.setValueMap(env.getValueMap());
        listBox.setValueIcons(env.getIconsMap());

        // sets paths for flag icons
        listBox.setImageURLPrefix(env.getIconURLPrefix());
        listBox.setImageURLSuffix(env.getIconURLSuffix());

        ComboBoxItem cbItem = new ComboBoxItem();
        cbItem.setShowTitle(false);
        cbItem.setDefaultToFirstOption(true);
        cbItem.setWidth(480);
        cbItem.setType("comboBox");
        cbItem.setValueMap("Select test session", "Test session 1", "Add new test session...");

        // create form
        DynamicForm form = new DynamicForm();
        form.setFields(listBox, cbItem);
        form.setCellPadding(10);

        sessionGroup.addControls(form);

        // Menu group: Site / Actors
        IconButton endpointButton = getIconButton("View / Configure Endpoints", "icons/user_24x24.png", true);

        // Menu group: Admin
        // Behavior: Clicking on any of the buttons in the admin group opens a dialog to allow the user to log in as admin,
        // IF not logged in yet. Then follows to the link initially requested.
        IconButton adminButton = getIconButton("Admin Settings", "icons/glyphicons/glyphicons_136_cogwheel.png", true);


        // Listeners
        endpointButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                    // Display the Endpoint Settings tab
                    Util.EVENT_BUS.fireEvent(new OpenTabEvent(TabNamesUtil.getInstance().getEndpointsTabCode()));
        }});


        adminButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // if not logged in
                if (!Util.getInstance().getLoggedAsAdminStatus()) {
                    // ask user to log in
                    LoginDialogWidget dialog = new LoginDialogWidget(TabNamesUtil.getInstance().getAdminTabCode());
                    dialog.show();
                } else {
                    // Display the Admin Settings tab if logged in
                    Util.EVENT_BUS.fireEvent(new OpenTabEvent(TabNamesUtil.getInstance().getAdminTabCode()));
                }
            }
        });



    // Add menu groups to menu bar
    this.addMembers(sessionGroup, endpointButton, adminButton);
    draw();
}


    /**
     * Calls to the backend for session parameters
     */
    protected void retrieveSessionParameters(){
        String[] environments;
        String[] sessions;

        ToolbarServiceAsync intf = GWT.create(ToolbarService.class);
        AsyncCallback<String[]> envCallback = new AsyncCallback<String[]>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
            }
            @Override
            public void onSuccess(String[] result) {
                // setEnvironments(result);
            }
        };
        AsyncCallback<String[]> sessionCallback = new AsyncCallback<String[]>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
            }
            @Override
            public void onSuccess(String[] result) {
                // setSessions(result);
            }
        };
        intf.retrieveEnvironments(envCallback);
        intf.retrieveTestSessions(sessionCallback);
    }

    /**
     * Creates an icon button.
     * @param title
     * @param iconName
     * @return
     */
    private IconButton getIconButton(String title, String iconName, boolean vertical) {
        IconButton button = new IconButton(title);
        button.setIcon(iconName);
        if (vertical) button.setOrientation("vertical");
        return button;
    }

    private RibbonGroup createRibbonGroup(String title){
        RibbonGroup group = new RibbonGroup();
        group.setTitle(title);
        group.setNumRows(1);
        return group;
    }
}
