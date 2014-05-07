package gov.nist.hit.ds.logBrowser.client.widgets;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.hit.ds.logBrowser.client.event.NewTxMessageEvent;
import gov.nist.hit.ds.logBrowser.client.event.NewTxMessageEventHandler;
import gov.nist.hit.ds.repository.api.PropertyKey;
import gov.nist.hit.ds.repository.simple.search.client.AssetNode;
import gov.nist.hit.ds.repository.simple.search.client.RepositoryService;
import gov.nist.hit.ds.repository.simple.search.client.RepositoryServiceAsync;
import gov.nist.hit.ds.repository.simple.search.client.SearchCriteria;
import gov.nist.hit.ds.repository.simple.search.client.SearchTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TransactionMonitorFilterWidget extends Composite {
    public static final double HEADER_SIZE = 2.5;

    /**
	 *
	 * @author Sunil.Bhaskarla
	 */
	private static Logger logger = Logger.getLogger(TransactionMonitorFilterWidget.class.getName());

	final public RepositoryServiceAsync reposService = GWT.create(RepositoryService.class);
	private SimpleEventBus eventBus;
    private SearchWidget searchWidget = null;


    private TransactionMonitorWidget txMonitorLive;
    private TransactionMonitorWidget txFilter;
    private Widget southStackPanel;
    private HTML liveCounterTxt = new HTML("");
    private int filterHitCounter = 0;
    private HTML filterCounterTxt = new HTML("");

    /*
    Sample 2-way Exchange pattern txDetail:
    20140326160812,"RESPONSE", "","500","localhost","localhost:8080","localhost:8001^ProxyRuleMappingName: localcap","text/html","","65","0"
     */




    public TransactionMonitorFilterWidget(SimpleEventBus eventBus)  {
	    this.eventBus = eventBus;

        // All composites must call initWidget() in their constructors.
	     initWidget(setupMainPanel());

    }



    protected Widget setupMainPanel() {
        SplitLayoutPanel mainSplitPanel = new SplitLayoutPanel(3);


        setSouthStackPanel(createFilteredMonitorPanel());
        //// getSouthStackPanel().setVisible(Boolean.FALSE);
        mainSplitPanel.addSouth(getSouthStackPanel(), Math.round(.5 * Window.getClientHeight()));

        mainSplitPanel.add(setupFilterBasedResultsPanel()); // Filter selection and results stack panel

        return mainSplitPanel;
    }

    private Widget createFilteredMonitorPanel() {
        TransactionMonitorWidget txMonitor = new TransactionMonitorWidget(eventBus,false,true);
        txMonitor.getElement().getStyle()
                .setProperty("border", "none");

        setTxFilter(txMonitor);

        StackLayoutPanel stackPanel = new StackLayoutPanel(Style.Unit.EM);
        //stackPanel.setHeight("100%");
        stackPanel.setWidth("100%");

        stackPanel.add(txMonitor, createPanelHeader("Filtered Messages",createFilterHeaderOptions(),null), HEADER_SIZE);

        return stackPanel;

    }




    protected Widget setupFilterBasedResultsPanel() {

        // SplitLayoutPanel filterSplitPanel = new SplitLayoutPanel(2);

        // Create a new stack layout panel.
        StackLayoutPanel stackPanel = new StackLayoutPanel(Style.Unit.EM);
        //stackPanel.setHeight("100%");
        stackPanel.setWidth("100%");

        // Add filter section
        stackPanel.add(createFilterWidget(), createPanelHeader("Message Filter", null, null), HEADER_SIZE); // Not the filter monitor but the filter selection

        // filterSplitPanel.addWest(stackPanel, Math.round(.3 * Window.getClientWidth()));
        // stackPanel = new StackLayoutPanel(Style.Unit.EM);
        // stackPanel.setWidth("100%");

        // Add live monitor section
        setTxMonitorLive(createLiveTxMonitorWidget());

        eventBus.addHandler(NewTxMessageEvent.TYPE, new NewTxMessageEventHandler() {
            @Override
            public void onNewTxMessage(NewTxMessageEvent event) {

                // no longer needed with built-in pager control
                // getLiveCounterTxt().setText("("+ (event.getMessageCount()) + ")");
                filter(event.getAnMap());
            }
        });

        stackPanel.add(getTxMonitorLive(), createPanelHeader("Proxy Monitor", createMonitorHeaderOptions(), null), HEADER_SIZE);

        // filterSplitPanel.add(stackPanel);

        return stackPanel;
    }

    private List<Widget> createFilterHeaderOptions() {
        List<Widget> options = new ArrayList<Widget>();

        options.add(getTxFilter().getPager());
        options.add(getFilterCounterTxt());

        HTML optShowDetail = new HTML("Message Details");
        optShowDetail.setStyleName("roundedButton1");
        optShowDetail.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getTxFilter().setShowTxDetail(!getTxFilter().getShowTxDetail());
            }
        });

        options.add(optShowDetail);

        HTML optClear = new HTML("Clear");
        optClear.setStyleName("roundedButton1");
        optClear.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                filterHitCounter = 0;
                getFilterCounterTxt().setText("");
                getTxFilter().clear();
            }
        });
        options.add(optClear);


        return options;
    }

    private List<Widget> createMonitorHeaderOptions() {
        List<Widget> options = new ArrayList<Widget>();

        options.add(getTxMonitorLive().getPager());

        options.add(getLiveCounterTxt());

        HTML optShowDetail = new HTML("Message Details");
        optShowDetail.setStyleName("roundedButton1");
        optShowDetail.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getTxMonitorLive().setShowTxDetail(!getTxMonitorLive().getShowTxDetail());
            }
        });

        options.add(optShowDetail);

        HTML optClear = new HTML("Clear");
        optClear.setStyleName("roundedButton1");
        optClear.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getLiveCounterTxt().setText("");
                getTxMonitorLive().clear();
            }
        });
        options.add(optClear);


        return options;
    }


    private Widget createFilterWidget() {
        ScrollPanel filterPanel = new ScrollPanel(); 				// Search parameters

        searchWidget = new SearchWidget(eventBus, new SearchWidget.Option[]{
                SearchWidget.Option.QUICK_SEARCH
                ,SearchWidget.Option.TWO_SEARCH_TERMS_PER_ROW
                ,SearchWidget.Option.APPLY_CRITERIA_WITHOUT_RUNNING
                // SearchWidget.Option.SEARCH_CRITERIA_REPOSITORIES,
                // SearchWidget.Option.CRITERIA_BUILDER_MODE
        });


        searchWidget.getElement().getStyle()
                .setProperty("border", "none");
        searchWidget.getElement().getStyle()
                .setPaddingLeft(3, Style.Unit.PX);

        filterPanel.add(searchWidget);

        return filterPanel;

    }



    private TransactionMonitorWidget createLiveTxMonitorWidget() {
        TransactionMonitorWidget txMonitor = new TransactionMonitorWidget(eventBus,true /*enableListener*/,false/*showDetail*/);
        txMonitor.getElement().getStyle()
                .setProperty("border", "none");

        return txMonitor;
    }

    private Widget createPanelHeader(String text, List<Widget> options, ImageResource icon) {

        // Add the image and text to a horizontal panel
        HorizontalPanel hPanel = new HorizontalPanel();
        //hPanel.setWidth("100%");
        hPanel.setSpacing(0);
        hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

        if (icon!=null) {
            hPanel.add(new Image(icon));
        }

        HTML headerText = new HTML(text + "&nbsp;");

        headerText.setStyleName("cw-StackPanelHeader");
        headerText.getElement().getStyle().setWidth(TransactionMonitorWidget.MESSAGE_LEFT_MARGIN, Style.Unit.PX);
        hPanel.add(headerText);

        if (options!=null)
            for (Widget w: options) {
                hPanel.add(w);
                hPanel.add(new HTML("&nbsp;")); // Spacer
            }


        return new SimplePanel(hPanel); // This wrapper will span out fully to 100% width

    }

    protected void filter(final Map<String,AssetNode> anMap) {
        // Get criteria and append the location and check the searchresultiterator to see if it exists/count match
        // call method with assetNode list to populate the filtered list

        String location = anMap.get("parentLoc").getLocation();

        if (location==null) {
            logger.severe("Asset location is missing!");
            return;
        }

        int itemCt = searchWidget.getReposRight().getItemCount();

        final VerticalPanel resultPanel = searchWidget.getResultPanel();

        resultPanel.clear();

        if (itemCt==0) {
            setFilterError("No filter has been applied or the repository selection is missing!");
            return;
        } else {
            if (!getSouthStackPanel().isVisible()) {
                //// getSouthStackPanel().setVisible(Boolean.TRUE);
            }

            //resultPanel.add(new HTML("&nbsp;"));
            //resultPanel.add(new HTML("Searching..."));


            // Wrap into new sc
            SearchCriteria subCriteria = new SearchCriteria(SearchCriteria.Criteria.AND);
            subCriteria.append(new SearchTerm(PropertyKey.LOCATION,SearchTerm.Operator.EQUALTO,location));

            SearchCriteria criteria = new SearchCriteria(SearchCriteria.Criteria.AND);
            criteria.append(searchWidget.getSc());
            criteria.append(subCriteria);

            String[][] selectedRepos = searchWidget.getSelectedRepos(itemCt);

            reposService.searchHit(selectedRepos, criteria, Boolean.TRUE, new AsyncCallback<Boolean>() {

                public void onFailure(Throwable arg0) {
                    setFilterError("Filter error: " + arg0.getMessage());
                }

                public void onSuccess(Boolean hit) {
                    if (hit) {
                        //resultPanel.add(new HTML("hit "+idx));
                        //getTxFilter().setTxRowParentId(getTxMonitorLive().getTxRowParentId());
                        //getTxFilter().setTxRowAssetNode(getTxMonitorLive().getTxRowAssetNode());
                        //Map<String,AssetNode> anMap = getTxMonitorLive().getTxRowAssetNode().get(new Integer(idx));
                        //resultPanel.add(new HTML("---"+ (anList==null) + " " + getTxMonitorLive().getTxRowAssetNode().size()));
                        if (getTxFilter().popTx(anMap)) {
                            // no longer needed with built-in pager control
                            // getFilterCounterTxt().setText("("+ (++filterHitCounter) + ")");
                        }
                    }
                }
            });


            // ((ListBox)event.getSource()).setEnabled(false);
        }

    }



    private void setFilterError(String msg) {
        searchWidget.getResultPanel().clear();
        searchWidget.getResultPanel().add(new HTML("<font color='red'>" + msg + "</font>"));
    }



    public TransactionMonitorWidget getTxMonitorLive() {
        return txMonitorLive;
    }

    public void setTxMonitorLive(TransactionMonitorWidget txMonitorLive) {
        this.txMonitorLive = txMonitorLive;
    }

    public TransactionMonitorWidget getTxFilter() {
        return txFilter;
    }

    public void setTxFilter(TransactionMonitorWidget txFilter) {
        this.txFilter = txFilter;
    }

    public HTML getFilterCounterTxt() {
        return filterCounterTxt;
    }

    public void setFilterCounterTxt(HTML filterCounterTxt) {
        this.filterCounterTxt = filterCounterTxt;
    }

    public HTML getLiveCounterTxt() {
        return liveCounterTxt;
    }

    public void setLiveCounterTxt(HTML liveCounterTxt) {
        this.liveCounterTxt = liveCounterTxt;
    }
    public Widget getSouthStackPanel() {
        return southStackPanel;
    }

    public void setSouthStackPanel(Widget southStackPanel) {
        this.southStackPanel = southStackPanel;
    }

}
