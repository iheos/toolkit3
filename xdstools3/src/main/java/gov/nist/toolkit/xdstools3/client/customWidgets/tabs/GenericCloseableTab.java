package gov.nist.toolkit.xdstools3.client.customWidgets.tabs;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class GenericCloseableTab extends Tab implements TabInterface {
	private VLayout panel = new VLayout(10);
	
	public GenericCloseableTab(String s){
		 setCanClose(true); 
		 setTitle(s);
	}

	public VLayout getPanel() {
		return panel;
	}
	
	// main header
	public void setHeader(String s){
		Label l = new Label();
		l.setContents(s);
        l.setStyleName("h4");
		panel.addMember(l);
		setPane(panel);
	}
	
	// subtitle
	public void setSubtitle(String s){
		Label l = new Label();
		l.setContents(s);
        l.setStyleName("h5");
		panel.addMember(l);
		setPane(panel);
	}

    /**
     * Sets a tab contents
     * @param pane The tab contents
     */
    public void setContents(Canvas pane){
        panel.addMember(pane);
        setPane(panel);
    }
	
	
}