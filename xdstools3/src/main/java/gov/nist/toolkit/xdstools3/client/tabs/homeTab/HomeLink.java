package gov.nist.toolkit.xdstools3.client.tabs.homeTab;

import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import gov.nist.toolkit.xdstools3.client.eventBusUtils.OpenTabEvent;
import gov.nist.toolkit.xdstools3.client.util.TabNamesUtil;
import gov.nist.toolkit.xdstools3.client.util.Util;

/**
 * Created by dazais on 7/8/2014.
 */
public class HomeLink extends LinkItem {

    public HomeLink(String _title){
        final String title = _title;
        setTitle(title);

        addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (title == "Find Documents" )  Util.EVENT_BUS.fireEvent(new OpenTabEvent(TabNamesUtil.getInstance().getFindDocumentsTabCode()));
                else if (title == "MPQ Find Documents" )  Util.EVENT_BUS.fireEvent(new OpenTabEvent(TabNamesUtil.getInstance().getMpqFindDocumentsTabCode()));
                else SC.say("A link is missing. Please contact the support team.");
            }
        });

    }
}
