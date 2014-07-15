package gov.nist.toolkit.xdstools3.client.tabs;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import gov.nist.toolkit.xdstools3.client.customWidgets.design.Formatter;
import gov.nist.toolkit.xdstools3.client.customWidgets.design.IconLabel;

public class GenericTab extends Tab implements TabInterface {
    private VLayout panel = new VLayout(10);

    public GenericTab(String s){
        setTitle(s);
    }

    public VLayout getPanel() {
        return panel;
    }

    // main header
    public void setHeader(String s){
        Label l = new Label();
        l.setContents(s);
        l.setStyleName("h3");
        panel.addMember(l);
        setPane(panel);
    }

    public IconLabel createSubtitle1(String s){
       return Formatter.createSubtitle1(s); // TODO May need to be transformed into direct call inside each tab
    }


    /**
     * Sets a tab contents
     * @param pane The tab contents
     */
    public void setContents(Canvas pane){
        panel.addMember(pane);
        setPane(panel);
        getPane().setAlign(Alignment.CENTER);
    }


}