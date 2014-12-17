package gov.nist.hit.ds.repository.ui.client.widgets;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DefaultCellTableBuilder;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.EventBus;
import fr.mikrosimage.gwt.client.IndexedColumn;
import fr.mikrosimage.gwt.client.ResizableDataGrid;
import gov.nist.hit.ds.repository.rpc.search.client.RepositoryService;
import gov.nist.hit.ds.repository.rpc.search.client.RepositoryServiceAsync;
import gov.nist.hit.ds.repository.rpc.search.client.exception.RepositoryConfigException;
import gov.nist.hit.ds.repository.shared.aggregation.AssertionAggregation;
import gov.nist.hit.ds.repository.shared.data.AssetNode;
import gov.nist.hit.ds.repository.shared.data.CSVRow;
import gov.nist.hit.ds.repository.shared.id.AssetId;
import gov.nist.hit.ds.repository.shared.id.RepositoryId;
import gov.nist.hit.ds.repository.shared.id.SimpleTypeId;
import gov.nist.hit.ds.repository.ui.client.CsvTableFactory;
import gov.nist.hit.ds.repository.ui.client.event.asset.InContextAssetClickedEvent;
import gov.nist.hit.ds.repository.ui.client.event.asset.OutOfContextAssetClickedEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This widget builds an aggregate view of the messages, which are embedded in CSV formatted content files, logged by toolkit components.
 * The following asset structure is required:
 *
 *  (Events)
 *      - 'Date' - Event Id type=event (required)
 *          - 'Validations' type=validators (required)
 *             - Assertions type=assertions (required) -- This is where the widget scans for the CSV files.
 */
public class EventAggregatorWidget extends Composite {

    public static final int FIXED_HEADER_LAST_IDX = 1;
    public static final int START_ROW_ZERO = 0;
    public static final int PAGE_SIZE = 4096;
    private static Logger logger = Logger.getLogger(EventAggregatorWidget.class.getName());

    private SplitLayoutPanel contentPanel = new SplitLayoutPanel(0);
    private EventBus eventBus;
    private ASSET_CLICK_EVENT assetClickEvent;
    private AssetId eventAssetId;
    private SimpleTypeId assetType;
    private RepositoryId externalRepositoryId;
    private String[] displayColumns;
    private String[] reformattedDisplayColumns;
    final private RepositoryServiceAsync reposService = GWT.create(RepositoryService.class);

    public static enum ASSET_CLICK_EVENT {
        IN_CONTEXT,
        OUT_OF_CONTEXT
    };


    //    private CellTable<List<SafeHtml>> table = new CellTable<List<SafeHtml>>(300); // new CsvTableFactory().createCellTable(rows.toArray(new String[rows.size()][]));

    private CsvTableFactory csvFactory = new CsvTableFactory();
    private List<String[]> rows = new ArrayList<String[]>();

//    private DataGrid<List<EventMessageCell>> table = new DataGrid<List<EventMessageCell>>(PAGE_SIZE);

    private ResizableDataGrid<List<EventMessageCell>> table = new ResizableDataGrid<List<EventMessageCell>>(PAGE_SIZE);

    private ListDataProvider<List<EventMessageCell>> dataProvider  = new ListDataProvider<List<EventMessageCell>>();
    private List<List<EventMessageCell>> dataRows = new ArrayList<List<EventMessageCell>>();

    private class EventMessageCell {
        private AssetNode an;
        private SafeHtml cellValue;
        private int rowNumber;

        private EventMessageCell() {
            super();
        }

        private EventMessageCell(AssetNode an, SafeHtml cellValue, int rowNumber) {
            this.an = an;
            this.cellValue = cellValue;
            this.rowNumber = rowNumber;
        }

        private EventMessageCell(SafeHtml cellValue) {
            this.cellValue = cellValue;
        }

        public AssetNode getAn() {
            return an;
        }

        public void setAn(AssetNode an) {
            this.an = an;
        }

        public SafeHtml getCellValue() {
            return cellValue;
        }

        public void setCellValue(SafeHtml cellValue) {
            this.cellValue = cellValue;
        }

        public int getRowNumber() {
            return rowNumber;
        }

        public void setRowNumber(int rowNumber) {
            this.rowNumber = rowNumber;
        }

    }

    /**
     * Builds a tabular data display based on an aggregate view of the event messages. Important: If the host container of this widget does not implement ProvidesResize or RequiresResize interfaces (this widget will not be rendered or completely empty in that case), then the size of this widget must be set explicitly. The size value must be in pixels or something other metric but not a percentage. See {@link gov.nist.hit.ds.repository.ui.client.widgets.EventAggregatorWidget#setSize(String, String)}.
     * @param eventBus The event bus to receive asset click event (to display in the log browser for instance).
     * @param assetClickEvent The type of event to fire.
     * @param externalRepositoryId The Id of the external repository containing the asset Id.
     * @param eventAssetId The Id of the upper-level Events asset.
     * @param assetType The type of assets to aggregate under the upper-level Events asset.
     * @param csvColumnNames The column row values to aggregate and display in the table. Column names are case-sensitive.
     */
   public EventAggregatorWidget(EventBus eventBus, ASSET_CLICK_EVENT assetClickEvent, String externalRepositoryId, String eventAssetId, String assetType, String[] csvColumnNames) throws RepositoryConfigException {
       try {
           setEventBus(eventBus);
           setAssetClickEvent(assetClickEvent);
           setExternalRepositoryId(externalRepositoryId);
           setEventAssetId(eventAssetId);
           setAssetType(assetType);
           setDisplayColumns(csvColumnNames);


           reposService.isRepositoryConfigured(new AsyncCallback<Boolean>() {
               public void onFailure(Throwable arg0) {
                   Window.alert("EventMessageAggregatorWidget: The repository system configuration is not available: " + arg0.toString());
               }

               public void onSuccess(Boolean rs) {
                   setupLayout();
               }
           });

           // All composites must call initWidget() in their constructors.
           initWidget(getContentPanel());

       } catch (Throwable t) {
           t.printStackTrace();
           Window.alert("EventAggregatorWidget constructor failed: " + t.toString());
       }


   }

    protected Widget setupLayout() {
        logger.log(Level.FINE, "In setupLayout of the event aggregator widget...");
//        setSize("600px", "400px"); // Default size, must be in pixels or something other metric but not a percentage


        logger.fine("Aggregated rows: " +table.getRowCount());

        table.setTableBuilder(new DefaultCellTableBuilder<List<EventMessageCell>>(table));

        //txTable.setWidth("100%", true);
//        table.setWidth("50%");
//        table.setHeight("50%");
//        table.setSkipRowHoverCheck(true);
//        table.setSkipRowHoverFloatElementCheck(true);
//        table.setSkipRowHoverStyleUpdate(true);
//        txTable.setStyleName("txDataGridNoTableSpacing");

        table.setAutoHeaderRefreshDisabled(false);
        table.getElement().getStyle().setProperty("wordWrap","break-word");
        table.setFocus(true);


        // Create table columns
        String[] cols = getReformattedDisplayColumns();
        for (int c = 0; c < cols.length; c++) {
//            table.addColumn(new IndexedColumn(c),
//                    new TextHeader(cols[c]));

            table.addColumn(new IndexedColumn(c), table.new DataGridResizableHeader(cols[c],new fr.mikrosimage.gwt.client.IndexedColumn(c)));
        }

        rows.add(cols); // Header row

        logger.fine("updating rows:" + dataRows.size());
        dataProvider.setList(dataRows);
        dataProvider.addDataDisplay(table);

        SimplePager pager = CsvTableFactory.getPager();
        pager.setDisplay(table);
        getContentPanel().addSouth(pager, 26);
        getContentPanel().add(table);

        try {
            reposService.aggregateAssertions(getExternalRepositoryId(), getEventAssetId(), getAssetType(), new SimpleTypeId("assertionGroup"),
                    null, // NOTE: Maybe it is a good idea to add a drop down of all distinct status codes for on-demand filtering of results
                    getDisplayColumns()
                    , new AsyncCallback<AssertionAggregation>() {
                @Override
                public void onFailure(Throwable t) {
                    Window.alert("reposService.aggregateAssertions error: " + t.toString());
                    t.printStackTrace();
                }

                @Override
                public void onSuccess(AssertionAggregation result) {

                    logger.info("got result size: " +  result.getRows().size() + " an map size: " + result.getAssetNodeMap().size());

                    List<CSVRow> rows = result.getRows();
                    int columns = getReformattedDisplayColumns().length;

                    String previousSection = "";

                    String linkColValue = "";
                    for (CSVRow row : rows) {
                        AssetNode an = result.getAssetNodeMap().get(row.getAssetId());
                        int rowNumber = row.getRowNumber();

                        List<EventMessageCell> htmlRow = new ArrayList<EventMessageCell>(columns);

//                        logger.info("processing: " + row.getAssetId() + " has object: " + (an!=null));

                        // add user friendly pointer to the asset node
                        if (!previousSection.equals(an.getDisplayName())) {
                            Anchor anchor = new Anchor(); // result.getDisplayName(),"javascript:void(0)"
                            String colorStr = "";

                            if (an.getColor()!=null && !"".equals(an.getColor())) {
                                colorStr = "style=\"color:" + an.getColor() + "\"";
                            }

                            SafeHtmlBuilder nodeSafeHtml =  new SafeHtmlBuilder();
                            nodeSafeHtml.appendHtmlConstant("<span "+ colorStr + " >"
                                    + an.getDisplayName() + "</span>");
                            anchor.setHref("javascript:void(0)");
                            anchor.setHTML(nodeSafeHtml.toSafeHtml());

                            linkColValue = anchor.getElement().getString();
                        } else {
                            linkColValue = "";
                        }
                        previousSection = an.getDisplayName();
                        htmlRow.add(new EventMessageCell(an,csvFactory.makeSafeHtml(linkColValue, true),rowNumber));

                        // add data from the csv aggregation
                        int csvColumns = result.getHeader().getColumns().length;
                        for (int cx=0; cx<csvColumns; cx++) {
                            String val =  row.getColumns()[cx];
                            htmlRow.add(new EventMessageCell(an,csvFactory.makeSafeHtml(val, false),rowNumber));

                        }
                        dataProvider.getList().add(htmlRow);

                    }
                    dataProvider.refresh();
                    table.redraw();

                }
            }
            );

        } catch (Throwable t) {
            Window.alert("reposService.aggregateAssertions call failed: " + t.toString());
        }
        //

        return getContentPanel();
    }

    private class IndexedColumn extends Column<List<EventMessageCell>, SafeHtml> { // Column<List<SafeHtml>, SafeHtml>
        private final int index;
        public IndexedColumn(int index) {
            // For use with String:
            // super(new TextCell());

            super(new SafeHtmlCell() {
                @Override
                public Set<String> getConsumedEvents() {
//                    return super.getConsumedEvents();
                    HashSet<String> events = new HashSet<String>();
                    events.add(ClickEvent.getType().getName());
                    return events;
                }
            });
            this.index = index;
        }
        @Override
        public SafeHtml getValue(List<EventMessageCell> object) {
            return object.get(this.index).getCellValue();
        }

        /**
         * Handle a browser event that took place within the column.
         *
         * @param context the cellValue context
         * @param elem    the parent Element
         * @param object  the base object to be updated
         * @param event   the native browser event
         */
        @Override
        public void onBrowserEvent(Cell.Context context, Element elem, List<EventMessageCell> object, NativeEvent event) {
            super.onBrowserEvent(context, elem, object, event);
//            Window.alert(ClickEvent.getType().getName() + " " + this.index);
            if (this.index==0 && ClickEvent.getType().getName().equals(event.getType()) ) {
                AssetNode an = object.get(this.index).getAn();
                int rowNumber = object.get(this.index).getRowNumber();

//                Window.alert(an.getLocation() + ">>" + elem.getTagName()  + " asset: " + an.getAssetId() +  " row num:" + rowNumber);
                if (an!=null) {
                    switch (getAssetClickEvent()) {
                        case IN_CONTEXT: eventBus.fireEvent(new InContextAssetClickedEvent(an,rowNumber));
                            break;
                        case OUT_OF_CONTEXT: eventBus.fireEvent(new OutOfContextAssetClickedEvent(an,rowNumber));
                            break;
                    }
                }


            }
        }
    }


    public void setError(String msg) {
        getContentPanel().clear();
        getContentPanel().add(new HTML(msg));

    }

    public void setContent(Widget w) {
        contentPanel.clear();
        contentPanel.add(w);
    }

    public void setSize(String width, String height) {
        this.contentPanel.setSize(width, height);
    }

    public void setPixelSize(int width, int height) {
        this.contentPanel.setPixelSize(width, height);
    }




    public SimpleTypeId getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType)  {
        this.assetType = new SimpleTypeId(assetType);
    }



    public AssetId getEventAssetId() {
        return eventAssetId;
    }

    public void setEventAssetId(String eventAssetId) {
        this.eventAssetId = new AssetId(eventAssetId);

    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public RepositoryId getExternalRepositoryId() {
        return externalRepositoryId;
    }

    public void setExternalRepositoryId(String externalRepositoryId) {
        this.externalRepositoryId = new RepositoryId(externalRepositoryId);
    }



    public String[] getDisplayColumns() {
        return displayColumns;
    }

    public String[] getDisplayColumnsWithAdditionalUserColumns() {
        return displayColumns;
    }

    public void setDisplayColumns(String[] displayColumns) {

        this.displayColumns = displayColumns;

        setReformattedDisplayColumns(displayColumns);
    }

    public String[] getReformattedDisplayColumns() {
        return reformattedDisplayColumns;
    }

    public void setReformattedDisplayColumns(String[] displayColumns) {

//        Window.alert("Is displayColumns null?: " + (displayColumns==null) + " -- " + "Is reformattedDisplayColumns null?: " + (reformattedDisplayColumns==null));

        reformattedDisplayColumns = new String[FIXED_HEADER_LAST_IDX+displayColumns.length];

        reformattedDisplayColumns[0] = "Assertion Name";
        for (int cx = 0; cx < displayColumns.length; cx++) {
            this.reformattedDisplayColumns[FIXED_HEADER_LAST_IDX+cx] = displayColumns[cx];
        }
    }


    public SplitLayoutPanel getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(SplitLayoutPanel contentPanel) {
        this.contentPanel = contentPanel;
    }
    public DataGrid<List<EventMessageCell>> getTable() {
        return table;
    }


    public ASSET_CLICK_EVENT getAssetClickEvent() {
        return assetClickEvent;
    }

    public void setAssetClickEvent(ASSET_CLICK_EVENT assetClickEvent) {
        this.assetClickEvent = assetClickEvent;
    }

}