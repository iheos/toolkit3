package gov.nist.toolkit.xdstools3.client.customWidgets.endpoints.configure;

import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * SmartGWT datasource for accessing entities over http in a RESTful manner.
 * Defines a RESTDataSource fields, operations and REST service URLs.
 */
public class EndpointConfigDS extends RestDataSource {

    private static EndpointConfigDS instance = null;

    public static EndpointConfigDS getInstance() {
        if (instance == null) {
            instance = new EndpointConfigDS();
        }
        return instance;
    }

	private EndpointConfigDS() {
        setID("supplyCategoryDS");
        setRecordXPath("/List/supplyCategory");

        // Set fields
		DataSourceTextField endpointId = new DataSourceTextField("id");
		endpointId.setPrimaryKey(true);
		endpointId.setCanEdit(false);
		DataSourceTextField endpointName = new DataSourceTextField("name");
        DataSourceTextField endpointType = new DataSourceTextField("type");
        DataSourceTextField categoryName = new DataSourceTextField("categoryName", "Category", 128, true);
        categoryName.setPrimaryKey(true);

        DataSourceTextField parentField = new DataSourceTextField("parentID", null);
        parentField.setHidden(true);
        parentField.setRequired(true);
        parentField.setRootValue("root");
        parentField.setForeignKey("supplyCategoryDS.categoryName");


        setFields(categoryName, parentField);

        setDataURL("resources/datasources/categories.data.xml");

        // Define REST URLs
		//setFetchDataURL("rest/endpointconfig/read");
		//setAddDataURL("rest/endpointconfig/add");
		//setUpdateDataURL("rest/endpointconfig/update");
		//setRemoveDataURL("rest/endpointconfig/remove");
	}
}
