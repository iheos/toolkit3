package gov.nist.toolkit.xdstools3.client.tabs.queryRetrieveTabs;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.events.BlurEvent;
import com.smartgwt.client.widgets.form.fields.events.BlurHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VStack;
import gov.nist.toolkit.xdstools3.client.customWidgets.GenericTextItemWithTooltipWidget;
import gov.nist.toolkit.xdstools3.client.customWidgets.TLSAndSAML.TLSAndSAMLForm;
import gov.nist.toolkit.xdstools3.client.customWidgets.endpoints.select.EndpointWidget;
import gov.nist.toolkit.xdstools3.client.util.TabNamesUtil;

public class GetFoldersTab extends gov.nist.toolkit.xdstools3.client.tabs.GenericCloseableTab {
    private static final String header="Get Folders";
    protected EndpointWidget sites;
    protected Button runBtn;
    protected GenericTextItemWithTooltipWidget folderUUID;

    public GetFoldersTab() {
        super(header);
    }

    @Override
    protected Widget createContents() {
        VStack vStack=new VStack();

        Label l1=createSubtitle1("1. Enter Folder UUID or UID");
        DynamicForm folderUUIDForm = new DynamicForm();
        folderUUID = new GenericTextItemWithTooltipWidget();
        folderUUID.setTitle("Folder UUID or UID");
        folderUUID.setWidth(400);
        folderUUIDForm.setFields(folderUUID);
        folderUUIDForm.setCellPadding(10);

        Label l2=createSubtitle1("2. Select site");
        sites = new EndpointWidget();
//        sites.isEndpointValueSelected()

        Label l3=createSubtitle1("3. Select SAML and TLS options");
        TLSAndSAMLForm tlsAndSAMLForm=new TLSAndSAMLForm();

        runBtn=new Button("Run");
        runBtn.disable();

        vStack.addMembers(l1,folderUUIDForm,l2, sites,l3,tlsAndSAMLForm,runBtn);

        bindUI();

        return vStack;
    }

    @Override
    protected String setTabName() {
        return TabNamesUtil.getInstance().getGetFoldersTabCode();
    }

    private void bindUI() {
        folderUUID.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent blurEvent) {
                if (!folderUUID.getValueAsString().isEmpty() && sites.isEndpointValueSelected()) {
                    runBtn.enable();
                } else {
                    runBtn.disable();
                }
            }
        });
        sites.addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent selectionEvent) {
                if (folderUUID.getValue() != null && sites.isEndpointValueSelected()) {
                    runBtn.enable();
                } else {
                    runBtn.disable();
                }
            }
        });
        runBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                // TODO
            }
        });
    }
}
