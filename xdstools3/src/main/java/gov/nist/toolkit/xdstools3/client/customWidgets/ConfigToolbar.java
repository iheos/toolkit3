package gov.nist.toolkit.xdstools3.client.customWidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import gov.nist.toolkit.xdstools3.client.InterfaceClientServer;
import gov.nist.toolkit.xdstools3.client.InterfaceClientServerAsync;
import gov.nist.toolkit.xdstools3.client.clientServerUtils.CustomCallback;
import gov.nist.toolkit.xdstools3.client.clientServerUtils.TransientArrayList;
import gov.nist.toolkit.xdstools3.client.customWidgets.loginDialog.LoginDialogWidget;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.ListBox;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IconButton;
import com.smartgwt.client.widgets.WidgetCanvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.menu.IconMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.toolbar.RibbonBar;
import com.smartgwt.client.widgets.toolbar.RibbonGroup;
import gov.nist.toolkit.xdstools3.client.events.OpenTabEvent;

import java.util.ArrayList;

public class ConfigToolbar extends RibbonBar {
	private SimpleEventBus bus;

	public ConfigToolbar(SimpleEventBus _bus) {
		bus = _bus;

		setMembersMargin(2); 
		setBorder("0px");
		setAlign(Alignment.CENTER);

		// Menu group: Session
		RibbonGroup sessionGroup = createRibbonGroup("Session");

        // Environment selection
		final ListBox listBox = new ListBox();
		listBox.setWidth("290px");
		listBox.addItem("Select environment");
		listBox.addItem("Env1");
		listBox.addItem("Env2");
		listBox.setVisibleItemCount(1);

//        listBox.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                int selectedIndex = listBox.getSelectedIndex();
//                String env = listBox.getItemText(selectedIndex);
//                if (env.equals("")) {
//                    //
//                }
//            }
//        });



		WidgetCanvas widgetCanvas_2 = new WidgetCanvas(listBox);
		ListBox listBox_1 = new ListBox();
		listBox_1.setWidth("290px");
		listBox_1.addItem("Select test session");
		listBox_1.addItem("Test session 1");
		listBox_1.addItem("Add new test session...");
		listBox_1.setVisibleItemCount(1);
		WidgetCanvas widgetCanvas_3 = new WidgetCanvas(listBox_1);
		sessionGroup.addControls(widgetCanvas_2, widgetCanvas_3);


		//       Menu configMenu = new Menu();
		//       configMenu.addItem(new MenuItem("Endpoint Configuration", "icon_gear.png", "Ctrl+D"));
		//       configMenu.addItem(new MenuItem("List of Endpoints", "icon_gear.png", "Ctrl+P"));
		//		IconMenuButton endpoints = getIconMenuButton("Endpoints","icon_gear.png", configMenu, true);

		// Menu group: Site / Actors
		RibbonGroup actorsGroup = createRibbonGroup("Endpoints");  
		IconButton configEndpoints = getIconButton("View / Configure", "icon_gear.png"); configEndpoints.setWidth("80px");
		actorsGroup.addControls(configEndpoints);

		// Menu group: Admin 
		// Behavior: Clicking on any of the buttons in the admin group opens a dialog to allow the user to log in as admin,
		// IF not logged in yet. Then follows to the link initially requested.
		RibbonGroup adminGroup = createRibbonGroup("Admin Panel");
        adminGroup.setWidth("350px");
		IconButton button = getIconButton("Settings", "icon_gear.png") ;
		button.addClickHandler(new ClickHandler() {  
			@Override
			public void onClick(ClickEvent event) {
				//if (!LoginManager.getInstance().isLoggedAsAdmin()) missing rpc call and user management
				showLoginWindow();
			}

            // Opens login dialog, which then displays the Admin Settings tab if login is successful
			private void showLoginWindow() {
                // TODO The Login window is missing check of credentials in backend
				LoginDialogWidget dialog = new LoginDialogWidget(bus);
				dialog.show();
			}  
        });  
		adminGroup.addControl(button);

		// Add menu groups to menu bar  
		this.addMembers(sessionGroup, actorsGroup, adminGroup);
		this.setHeight(sessionGroup.getHeight());
		this.setWidth100();
	}


    /**
     * Calls to the backend for session parameters
     */
    protected void retrieveSessionParameters(){
        String[] environments;
        String[] sessions;

        InterfaceClientServerAsync intf = GWT.create(InterfaceClientServer.class);
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
	private IconButton getIconButton(String title, String iconName) {  
		IconButton button = new IconButton(title);  
		button.setTitle(title);  
		if (iconName == null) iconName = "defaulticon";  
		button.setIcon(iconName); 
		return button;  
	}  

	/**
	 * Creates a drop-down menu with an icon.
	 * @param title
	 * @param iconName
	 * @param menu
	 * @param vertical
	 * @return
	 */
	private IconMenuButton getIconMenuButton(String title, String iconName, Menu menu, boolean vertical) {  
		IconMenuButton button = new IconMenuButton(title);  
		button.setTitle(title);   
		if (iconName == null) iconName = "defaulticon";  
		button.setIcon(iconName); 
		if (menu != null) button.setMenu(menu); 
		if (vertical == true) button.setOrientation("vertical"); 
		return button;  
	}  

	private RibbonGroup createRibbonGroup(String title){
		RibbonGroup group = new RibbonGroup(); 
		group.setTitle(title);  
		group.setNumRows(1); 
		return group;
	}
}
