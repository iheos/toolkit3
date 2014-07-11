package edu.tn.xds.metadata.editor.client.editor.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.form.Validator;
import edu.tn.xds.metadata.editor.client.editor.properties.String256Properties;
import edu.tn.xds.metadata.editor.client.generics.GenericEditableListView;
import edu.tn.xds.metadata.editor.client.widgets.BoundedTextField;
import edu.tn.xds.metadata.editor.shared.model.NameValueString256;
import edu.tn.xds.metadata.editor.shared.model.String256;

/**
 * <p>
 * <b>This class represents the widget which matches NameValueString256 model
 * type</b>
 * </p>
 */
public class NameValueString256EditorWidget extends GenericEditableListView<String256, String> implements Editor<NameValueString256> {
    //    private static Logger logger = Logger.getLogger(NameValueString256EditorWidget.class.getName());
    private final static String256Properties props = GWT.create(String256Properties.class);

    ListStoreEditor<String256> values;
    @Ignore
    BoundedTextField tf = new BoundedTextField();

    public NameValueString256EditorWidget(String widgetTitle) {
        super(String256.class, widgetTitle, new ListStore<String256>(props.key()), props.string());

        tf.setAllowBlank(false);
        tf.setToolTip("This value is required and must unique.");
        tf.setEmptyText("ex: 58642j65s^^^5.8.4");
        addEditorConfig(tf);

        values = new ListStoreEditor<String256>(getStore());

        // init namevaluestring256 with its widgets container
//        initWidget(listView.asWidget());

    }

    public void addFieldValidator(Validator validator) {
        tf.addValidator(validator);
    }


    /**
     * Sets the widget's tool tip with the given config
     *
     * @param toolTip
     */
    public void setEditingFieldToolTip(String toolTip) {
        tf.setToolTip(toolTip);
    }

    /**
     * This method sets the default text to display in an empty field (defaults
     * to null). It is done to help and guide the user during his input.
     *
     * @param emptyText Default text displayed in an empty value field when editing
     */
    public void setEmptyTexts(String emptyText) {
        tf.setEmptyText(emptyText);
    }

    public void setListMaxSize(int maxSize) {
        setStoreMaxLength(maxSize);
    }


    //	/**
//	 * Sets whether a field is valid when its value length = 0 (default to
//	 * true). This will warn the user through the editor widget if he didn't
//	 * input anything in field which does not allow blank.
//	 *
//	 * @param nameAllowsBlank
//	 *            true to allow blank to the name field, false otherwise
//	 * @param valueAllowsBlank
//	 *            true to allow blank to the value field, false otherwise
//	 *
//	 */
//	public void setAllowBlanks(boolean nameAllowsBlank, boolean valueAllowsBlank) {
//		name.setAllowBlank(nameAllowsBlank);
//		value.setAllowBlank(valueAllowsBlank);
//	}


}
