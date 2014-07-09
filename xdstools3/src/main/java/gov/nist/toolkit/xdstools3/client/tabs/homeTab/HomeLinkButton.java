package gov.nist.toolkit.xdstools3.client.tabs.homeTab;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import gov.nist.toolkit.xdstools3.client.eventBusUtils.OpenTabEvent;
import gov.nist.toolkit.xdstools3.client.util.TabNamesUtil;
import gov.nist.toolkit.xdstools3.client.util.Util;

/**
 * Created by dazais on 7/8/2014.
 */
public class HomeLinkButton extends IButton {

    public HomeLinkButton(String _title){
        final String title = _title;
        setStyleName("homelinkbutton"); // TODO doesn't seem to work with Xdstools3.css, why?
        setTitle(title);
        setAlign(Alignment.LEFT);
        setBorder("0");
        setWidth(300);
        setHeight(40);

        addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (title == "Find Documents")
                    Util.EVENT_BUS.fireEvent(new OpenTabEvent(TabNamesUtil.getInstance().getFindDocumentsTabCode()));
                else if (title == "MPQ Find Documents")
                    Util.EVENT_BUS.fireEvent(new OpenTabEvent(TabNamesUtil.getInstance().getMpqFindDocumentsTabCode()));
                else SC.say("A link is missing. Please contact the support team.");
            }
        });

    }
}
