package edu.tn.xds.metadata.editor.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
import edu.tn.xds.metadata.editor.client.editor.properties.AuthorProperties;
import edu.tn.xds.metadata.editor.client.editor.widgets.*;
import edu.tn.xds.metadata.editor.client.generics.abstracts.AbstractView;
import edu.tn.xds.metadata.editor.client.widgets.ConfirmDeleteDialog;
import edu.tn.xds.metadata.editor.shared.model.Author;
import edu.tn.xds.metadata.editor.shared.model.CodedTerm;
import edu.tn.xds.metadata.editor.shared.model.DocumentModel;
import edu.tn.xds.metadata.editor.shared.model.InternationalString;

public class DocumentModelEditorView extends AbstractView<DocumentModelEditorPresenter> implements Editor<DocumentModel> {
	private final AuthorProperties authorprops = GWT.create(AuthorProperties.class);

	private final VerticalLayoutContainer form = new VerticalLayoutContainer();

	VerticalLayoutContainer requiredFields = new VerticalLayoutContainer();
	VerticalLayoutContainer optionalFields = new VerticalLayoutContainer();

	/* simple fields declaration */
	String256EditorWidget id = new String256EditorWidget();
	String256EditorWidget hash = new String256EditorWidget();
	LanguageCodeComboBox languageCode = new LanguageCodeComboBox();
	String256EditorWidget mimeType = new String256EditorWidget();
	String256EditorWidget uri = new String256EditorWidget();
	OIDEditorWidget repoUId = new OIDEditorWidget(false);

	/* coded terms declaration */
	CodedTermEditorWidget classCode = new CodedTermEditorWidget();
	CodedTermEditorWidget formatCode = new CodedTermEditorWidget();
	CodedTermEditorWidget healthcareFacilityType = new CodedTermEditorWidget();
	CodedTermEditorWidget practiceSettingCode = new CodedTermEditorWidget();
	CodedTermEditorWidget typeCode = new CodedTermEditorWidget();

	/* Identifiers declaration */
	IdentifierOIDEditorWidget uniqueId = new IdentifierOIDEditorWidget();
	IdentifierString256EditorWidget patientID = new IdentifierString256EditorWidget();

	/* name values declaration */
	NameValueString256EditorWidget legalAuthenticator = new NameValueString256EditorWidget("Legal Authenticator");
	NameValueString256EditorWidget sourcePatientId = new NameValueString256EditorWidget("Source Patient ID");
	NameValueString256EditorWidget sourcePatientInfo = new NameValueString256EditorWidget("Source Patient Info");
	NameValueDTMEditorWidget creationTime = new NameValueDTMEditorWidget("Creation Time");
	NameValueDTMEditorWidget serviceStartTime = new NameValueDTMEditorWidget("Service Start Time");
	NameValueDTMEditorWidget serviceStopTime = new NameValueDTMEditorWidget("Service Stop Time");
	NameValueIntegerEditorWidget size = new NameValueIntegerEditorWidget("File Size");

	// ///////////////////////////////////////////////////////////////////////
	// ---- AUTHORS WIDGETS
	// ///////////////////////////////////////////////////////////////////////
	ListStoreEditor<Author> authors;
	@Ignore
	ListView<Author, String> listViewAuthors;
	@Ignore
	AuthorEditorWidget author = new AuthorEditorWidget();
	@Ignore
	TextButton newAuthorWidget = new TextButton("New entry");
	@Ignore
	TextButton editAuthorWidget = new TextButton("Edit entry");
	@Ignore
	TextButton saveAuthorWidget = new TextButton("Save entry");
	@Ignore
	TextButton cancelAuthorWidget = new TextButton("Cancel");
	@Ignore
	TextButton deleteAuthorWidget = new TextButton("Delete entry");

	// ///////////////////////////////////////////////////////////////////////
	// ---- Titles WIDGETS
	// ///////////////////////////////////////////////////////////////////////
/*
	ListStoreEditor<InternationalString> titles;
	// @Ignore
	// ListView<InternationalString, String> listViewTitles;
	@Ignore
	Grid<InternationalString> gridTitles;
	@Ignore
	InternationalStringEditorWidget title = new InternationalStringEditorWidget();
	@Ignore
	TextButton newTitleWidget = new TextButton("New entry");
	@Ignore
	TextButton editTitleWidget = new TextButton("Edit entry");
	@Ignore
	TextButton saveTitleWidget = new TextButton("Save entry");
	@Ignore
	TextButton cancelTitleWidget = new TextButton("Cancel");
	@Ignore
	TextButton deleteTitleWidget = new TextButton("Delete entry");
*/
    ListStoreEditor<InternationalString> titles;
    InternationalStringEditableGrid titlesGrid;
	// ///////////////////////////////////////////////////////////////////////
	// ---- Comments WIDGETS
	// ///////////////////////////////////////////////////////////////////////
//	ListStoreEditor<InternationalString> comments;
//	// @Ignore
//	// ListView<InternationalString, String> listViewComments;
//	@Ignore
//	Grid<InternationalString> gridComments;
//	@Ignore
//	InternationalStringEditorWidget comment = new InternationalStringEditorWidget();
//	@Ignore
//	TextButton newCommentWidget = new TextButton("New entry");
//	@Ignore
//	TextButton editCommentWidget = new TextButton("Edit entry");
//	@Ignore
//	TextButton saveCommentWidget = new TextButton("Save entry");
//	@Ignore
//	TextButton cancelCommentWidget = new TextButton("Cancel");
//	@Ignore
//	TextButton deleteCommentWidget = new TextButton("Delete entry");
    ListStoreEditor<InternationalString> comments;
    InternationalStringEditableGrid commentsGrid;

	// ///////////////////////////////////////////////////////////////////////
	// ---- ConfidentialityCodes WIDGETS
	// ///////////////////////////////////////////////////////////////////////
//	ListStoreEditor<CodedTerm> confidentialityCodes;
//	@Ignore
//	Grid<CodedTerm> gridConfidentialityCode;
//	// @Ignore
//	// ListView<CodedTerm, String> listViewConfidentialityCode;
//	@Ignore
//	CodedTermEditorWidget confidentialityCode = new CodedTermEditorWidget();
//	@Ignore
//	TextButton newConfidentialityCodeWidget = new TextButton("New entry");
//	@Ignore
//	TextButton editConfidentialityCodeWidget = new TextButton("Edit entry");
//	@Ignore
//	TextButton saveConfidentialityCodeWidget = new TextButton("Save entry");
//	@Ignore
//	TextButton cancelConfidentialityCodeWidget = new TextButton("Cancel");
//	@Ignore
//	TextButton deleteConfidentialityCodeWidget = new TextButton("Delete entry");
    ListStoreEditor<CodedTerm> confidentialityCodes;
    CodedTermsEditableGridWidget confidentialityCodesGrid;

    // ///////////////////////////////////////////////////////////////////////
    // ---- EventCodes WIDGETS
    // ///////////////////////////////////////////////////////////////////////
    ListStoreEditor<CodedTerm> eventCode;
    CodedTermsEditableGridWidget eventCodesGrid;

	// test button
	// @Ignore
	// TextButton btnSave = new TextButton("Save File");

	@Override
	protected Widget buildUI() {
		final HorizontalLayoutContainer container = new HorizontalLayoutContainer();
		container.getElement().setMargins(10);
		container.setBorders(false);

		FramedPanel fp1 = new FramedPanel();
		FramedPanel fp2 = new FramedPanel();

		fp1.add(requiredFields);
		fp2.add(optionalFields);
		fp1.setHeadingText("Required Fields");
		fp2.setHeadingText("Optional Fields");

		// Adding required and optional fields panels to the main container of
		// editor view
		container.add(fp1, new HorizontalLayoutData(0.5, -1, new Margins(10, 10, 10, 10)));
		container.add(fp2, new HorizontalLayoutData(0.5, -1, new Margins(10, 10, 10, 10)));

		// //////////////////////////////////////
		// Simple fields label and options (init)
		// /////////////////////////////////////
		// ID Field (required)
		FieldLabel idLabel = new FieldLabel(id, "Entry UUID");
		idLabel.setLabelWidth(125);
		id.setAllowBlank(false);
        id.addValidator(new RegExValidator("[1-9][0-9]+","Value is not correct. It is supposed to be a number."));

		// Hash Field (required)
		FieldLabel hashLabel = new FieldLabel(hash, "Hash");
		hashLabel.setLabelWidth(125);
		hash.setAllowBlank(true);
        hash.addValidator(new RegExValidator("[0-9a-fA-F]","Value is not correct. It is supposed to be a hexadecimal value."));

		// Language Code Field (required)
		FieldLabel languageCodeLabel = new FieldLabel(languageCode, "Language Code");
		languageCodeLabel.setLabelWidth(125);
		languageCode.setAllowBlank(false);

		// Mime Type Field (required)
		FieldLabel mimeTypeLabel = new FieldLabel(mimeType, "Mime Type");
		mimeTypeLabel.setLabelWidth(125);
		mimeType.setAllowBlank(false);

		// Repository Unique ID Field (optional)
		FieldLabel repositoryLabel = new FieldLabel(repoUId, "Repository Unique ID");
		repositoryLabel.setLabelWidth(125);
		repoUId.setAllowBlank(true);
        repoUId.addValidator(new RegExValidator("^[1-9][0-9]*(\\.[1-9][0-9]+)+$","Value is not correct. A repository unique ID is supposed to be a suite of numbers separated by periods."));

		// URI Field (optional)
		FieldLabel uriLabel = new FieldLabel(uri, "URI");
		uriLabel.setLabelWidth(125);
		uri.setAllowBlank(true);

		// ////////////////////////////////////////////////////
		// --- Adding REQUIRED simple fields labels to containers
		// ////////////////////////////////////////////////////
		VerticalLayoutContainer simpleRequiredFieldsContainer = new VerticalLayoutContainer();
		simpleRequiredFieldsContainer.add(idLabel, new VerticalLayoutData(1, -1));
		simpleRequiredFieldsContainer.add(languageCodeLabel, new VerticalLayoutData(1, -1));
		simpleRequiredFieldsContainer.add(mimeTypeLabel, new VerticalLayoutData(1, -1));

		/* REQUIRED container added to a fieldset */
		FieldSet fieldSet_general_fields_required = new FieldSet();
		fieldSet_general_fields_required.setHeadingText("General required");
		fieldSet_general_fields_required.setCollapsible(true);
		fieldSet_general_fields_required.add(simpleRequiredFieldsContainer);

		// //////////////////////////////////////////////////////////
		// --- Adding OPTIONAL simple fields labels to containers
		// //////////////////////////////////////////////////////////
		VerticalLayoutContainer filePropertiesFieldsContainer = new VerticalLayoutContainer();
		filePropertiesFieldsContainer.add(hashLabel, new VerticalLayoutData(1, -1));
        filePropertiesFieldsContainer.add(size.asWidget(), new VerticalLayoutData(1, -1, new Margins(0, 0, 10, 0)));

        VerticalLayoutContainer repositoryAttributesFieldsContainer = new VerticalLayoutContainer();
        repositoryAttributesFieldsContainer.add(uriLabel, new VerticalLayoutData(1, -1));
        repositoryAttributesFieldsContainer.add(repositoryLabel, new VerticalLayoutData(1, -1));

		/* OPTIONAL container added to a fieldset */
		FieldSet fieldSet_fileProperties = new FieldSet();
		fieldSet_fileProperties.setHeadingText("Files properties");
		fieldSet_fileProperties.setCollapsible(true);
		// fieldSet_simple_fields.setWidth("auto");
		fieldSet_fileProperties.add(filePropertiesFieldsContainer);

        FieldSet fieldSet_repoAttributes = new FieldSet();
        fieldSet_repoAttributes.setHeadingText("Repository attributes");
        fieldSet_repoAttributes.setCollapsible(true);
        // fieldSet_simple_fields.setWidth("auto");
        fieldSet_repoAttributes.add(repositoryAttributesFieldsContainer);

		// //////////////////////////////////////////////////////
		// Other fields and options (init)
		// //////////////////////////////////////////////////////

		/* ********************************* */
		/* identifiers options and fieldset */
		/* ********************************* */
		// Patient ID Fields (required)
		FieldSet fieldSet_identifier_patient = new FieldSet();
		fieldSet_identifier_patient.setHeadingText("Patient ID");
		fieldSet_identifier_patient.setCollapsible(true);
		fieldSet_identifier_patient.add(patientID);
		patientID.setAllowBlanks(false, false);
        patientID.addValueFieldValidator(new RegExValidator("^[1-9][0-9a-z]+\\^{3}&[1-9](\\.[1-9][0-9]*)+(&ISO)$","Value's format is not a correct. \nIt should be like this: 6578946^^^&1.3.6.1.4.1.21367.2005.3.7&ISO."));

		// Unique ID Fieds (required)
		FieldSet fieldSet_identifier_unique = new FieldSet();
		fieldSet_identifier_unique.setHeadingText("Unique ID");
		fieldSet_identifier_unique.setCollapsible(true);
		fieldSet_identifier_unique.add(uniqueId);
		uniqueId.setAllowBlanks(false, false);
        uniqueId.addValueFieldValidator(new RegExValidator("^[1-9](\\.[1-9][0-9]*)+\\^[1-9][0-9]+$","Value's format is not a correct. It is supposed to be a suite of figures separated by periods."));

		/* ********************************** */
		/* coded terms options and fieldset */
		/* ********************************** */
		// Class Code (required)
		FieldSet fieldSet_codedTerm_classCode = new FieldSet();
		fieldSet_codedTerm_classCode.setHeadingText("Class Code");
		fieldSet_codedTerm_classCode.setCollapsible(true);
		fieldSet_codedTerm_classCode.add(classCode);
		classCode.setAllowBlanks(false, false, false);

		// Format Code (required)
		FieldSet fieldSet_codedTerm_formatCode = new FieldSet();
		fieldSet_codedTerm_formatCode.setHeadingText("Format Code");
		fieldSet_codedTerm_formatCode.setCollapsible(true);
		fieldSet_codedTerm_formatCode.add(formatCode);
		formatCode.setAllowBlanks(false, false, false);

		// Healthcare Facility Type (required)
		FieldSet fieldSet_codedTerm_healthcareFacility = new FieldSet();
		fieldSet_codedTerm_healthcareFacility.setHeadingText("Healthcare Facility Type");
		fieldSet_codedTerm_healthcareFacility.setCollapsible(true);
		fieldSet_codedTerm_healthcareFacility.add(healthcareFacilityType);
		healthcareFacilityType.setAllowBlanks(false, false, false);

		// Practice Setting Code (required)
		FieldSet fieldSet_codedTerm_practiceSettingCode = new FieldSet();
		fieldSet_codedTerm_practiceSettingCode.setHeadingText("Practice Setting Code");
		fieldSet_codedTerm_practiceSettingCode.setCollapsible(true);
		fieldSet_codedTerm_practiceSettingCode.add(practiceSettingCode);
		practiceSettingCode.setAllowBlanks(false, false, false);

		// Type Code (required)
		FieldSet fieldSet_codedTerm_typeCode = new FieldSet();
		fieldSet_codedTerm_typeCode.setHeadingText("Type Code");
		fieldSet_codedTerm_typeCode.setCollapsible(true);
		fieldSet_codedTerm_typeCode.add(typeCode);
		typeCode.setAllowBlanks(false, false, false);

		/* ****************************** */
		/* name values options and fields */
		/* ****************************** */
		// Legal Authenticator (optional)
        legalAuthenticator.setListMaxSize(1);
//        legalAuthenticator.disableListToolbar();

		// Source Patient ID (optional)
        sourcePatientId.addFieldValidator(new RegExValidator("^[A-Za-z]*[1-9][0-9]+\\^{3}&[1-9][0-9]*(\\.[0-9][1-9]*)+(&ISO)$"));
        sourcePatientInfo.addFieldValidator(new RegExValidator("^PID-[1-9][0-9]{0,1}\\|"));

		// Service Start Time (optional)
        serviceStartTime.setListMaxSize(1);
//        serviceStartTime.disableEditing();

		// Service Stop Time (optional)
        serviceStopTime.setListMaxSize(1);
//        serviceStopTime.disableEditing();

		// Size (optional)
        size.disableEditing();
//		size.setAllowBlanks(true, true);

		// Creation Time (required)
//        creationTime.disableEditing();
        creationTime.disableListToolbar();

		// AUTHORS (Optional)
		FieldSet fieldSet_authors = new FieldSet();
		fieldSet_authors.setHeadingText("Authors");
		fieldSet_authors.setCollapsible(true);
		fieldSet_authors.add(buildAuthorsEditorWidget());

		// TITLES (Optional)
        titlesGrid=new InternationalStringEditableGrid("Titles");
        titles = new ListStoreEditor<InternationalString>(titlesGrid.getGrid().getStore());


		// COMMENTS (Optional)
        commentsGrid=new InternationalStringEditableGrid("Comments");
        comments = new ListStoreEditor<InternationalString>(commentsGrid.getGrid().getStore());

		// CONFIDENTIALITY CODE (Optional)
        confidentialityCodesGrid=new CodedTermsEditableGridWidget("Confidentiality Codes");
        confidentialityCodes=new ListStoreEditor<CodedTerm>(confidentialityCodesGrid.getGrid().getStore());

        // CONFIDENTIALITY CODE (Optional)
        eventCodesGrid=new CodedTermsEditableGridWidget("Event Codes");
        eventCode=new ListStoreEditor<CodedTerm>(eventCodesGrid.getGrid().getStore());

		// /////////////////////////////////////////////////////////
		// Adding and ordering fieldsets in REQUIRED panel
		// /////////////////////////////////////////////////////////
		/* simple required fields added to FramedPanel container */
		requiredFields.add(fieldSet_general_fields_required);
		requiredFields.add(fieldSet_codedTerm_classCode);
		requiredFields.add(creationTime.asWidget(),new VerticalLayoutData(1,-1,new Margins(0,0,10,0)));
		requiredFields.add(fieldSet_codedTerm_formatCode);
		requiredFields.add(fieldSet_codedTerm_healthcareFacility);
		requiredFields.add(fieldSet_identifier_patient);
		requiredFields.add(fieldSet_codedTerm_practiceSettingCode);
		requiredFields.add(fieldSet_codedTerm_typeCode);
		requiredFields.add(fieldSet_identifier_unique);

		// /////////////////////////////////////////////////////////
		// Adding and ordering fieldsets in OPTIONAL panel
		// /////////////////////////////////////////////////////////
		/* simple optional fields added to FramedPanel container */
		optionalFields.add(fieldSet_fileProperties);
		optionalFields.add(fieldSet_repoAttributes);
		optionalFields.add(fieldSet_authors);
		optionalFields.add(titlesGrid.asWidget(),new VerticalLayoutData(1,-1,new Margins(0,0,10,0)));
		optionalFields.add(commentsGrid.asWidget(),new VerticalLayoutData(1,-1,new Margins(0,0,10,0)));
		optionalFields.add(sourcePatientId.asWidget(),new VerticalLayoutData(1,-1,new Margins(0,0,10,0)));
		optionalFields.add(sourcePatientInfo.asWidget(),new VerticalLayoutData(1,-1,new Margins(0,0,10,0)));
		optionalFields.add(legalAuthenticator.asWidget(),new VerticalLayoutData(1,-1,new Margins(0,0,10,0)));
		optionalFields.add(confidentialityCodesGrid.asWidget(),new VerticalLayoutData(1,-1,new Margins(0,0,10,0)));
		optionalFields.add(eventCodesGrid.asWidget(),new VerticalLayoutData(1,-1,new Margins(0,0,10,0)));
		optionalFields.add(serviceStartTime.asWidget(),new VerticalLayoutData(1,-1,new Margins(0,0,10,0)));
		optionalFields.add(serviceStopTime.asWidget(),new VerticalLayoutData(1,-1,new Margins(0,0,10,0)));

		setWidgetsInfo();

		form.setScrollMode(ScrollMode.AUTOY);
		form.add(container);

		return form;
	}

	@Override
	protected void bindUI() {
		// ///////////////////////////////////////////////////
		// --- AUTHORS WIDGETS UI BINDING
		// ///////////////////////////////////////////////////
		listViewAuthors.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<Author>() {

			@Override
			public void onSelectionChanged(SelectionChangedEvent<Author> event) {
				if (listViewAuthors.getSelectionModel().getSelectedItem() != null) {
					author.setEditionMode(EditionMode.DISPLAY);
					author.edit(listViewAuthors.getSelectionModel().getSelectedItem());
					enableAuthorButtonWidgets(EditionMode.DISPLAY);
				} else {
					author.setEditionMode(EditionMode.NODATA);
					enableAuthorButtonWidgets(EditionMode.NODATA);
				}
			}
		});
		newAuthorWidget.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				listViewAuthors.getSelectionModel().deselectAll();
				author.setEditionMode(EditionMode.NEW);
				author.editNew();
				enableAuthorButtonWidgets(EditionMode.NEW);
			}
		});
		editAuthorWidget.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				author.setEditionMode(EditionMode.EDIT);
				// author.edit(author);
				enableAuthorButtonWidgets(EditionMode.EDIT);
			}
		});
		cancelAuthorWidget.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				author.setEditionMode(EditionMode.NODATA);
				enableAuthorButtonWidgets(EditionMode.NODATA);
				listViewAuthors.getSelectionModel().deselectAll();
				author.editNew();
			}
		});
		deleteAuthorWidget.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				final ConfirmDeleteDialog cdd = new ConfirmDeleteDialog(listViewAuthors.getSelectionModel().getSelectedItem().getAuthorPerson()
						.getString());
				cdd.show();
				cdd.addHideHandler(new HideHandler() {

					@Override
					public void onHide(HideEvent event) {
						if (cdd.getHideButton() == cdd.getButtonById(PredefinedButton.YES.name())) {
							// perform YES action
							authors.getStore().remove(listViewAuthors.getSelectionModel().getSelectedItem());
							author.editNew();
						}
					}
				});
			}
		});
		saveAuthorWidget.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				author.save();
				if (author.getEditionMode().equals(EditionMode.NEW)) {
					boolean isAlreadyThere = false;
					for (Author a : authors.getStore().getAll()) {
						if (a.getAuthorPerson().getString().equals(author.getModel().getAuthorPerson().getString())) {
							isAlreadyThere = true;
							break;
						}
					}
					if (isAlreadyThere) {
						Info.display("Impossible to add this value",
								"This author person already exists for this document. You can not add him again.");
					} else if (author.hasErrors()){
                        Info.display("Impossible to add this value","There still are errors in the editor.");
                    }else {
						authors.getStore().add(author.getModel());
						author.editNew();
						listViewAuthors.getSelectionModel().deselectAll();
						author.setEditionMode(EditionMode.NODATA);
						enableAuthorButtonWidgets(EditionMode.NODATA);
					}

				} else if (author.getEditionMode().equals(EditionMode.EDIT)) {
					authors.getStore().update(author.getModel());
					author.setEditionMode(EditionMode.DISPLAY);
					enableAuthorButtonWidgets(EditionMode.DISPLAY);
				}
			}
		});
	}

    private Widget buildAuthorsEditorWidget() {
        listViewAuthors = new ListView<Author, String>(new ListStore<Author>(authorprops.key()), authorprops.authorPerson());
        listViewAuthors.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        authors = new ListStoreEditor<Author>(listViewAuthors.getStore());

        FieldSet authorFS = new FieldSet();
        authorFS.setHeadingText("Author Entry");
        authorFS.add(author);
        author.setEditionMode(EditionMode.NODATA);

        ContentPanel authorCP = new ContentPanel();
        authorCP.setHeaderVisible(false);
        authorCP.setBorders(false);
        authorCP.setBodyBorder(false);
        enableAuthorButtonWidgets(EditionMode.NODATA);
        authorCP.addButton(newAuthorWidget);
        authorCP.addButton(editAuthorWidget);
        authorCP.addButton(saveAuthorWidget);
        authorCP.addButton(cancelAuthorWidget);
        authorCP.addButton(deleteAuthorWidget);
        authorCP.setButtonAlign(BoxLayoutPack.END);

        VerticalLayoutContainer vcon = new VerticalLayoutContainer();
        vcon.add(listViewAuthors, new VerticalLayoutData(1, 150));
        vcon.add(authorFS, new VerticalLayoutData(0.999, -1, new Margins(10, 0, 0, 0)));
        vcon.add(authorCP, new VerticalLayoutData(-1, 30));
        return vcon;
    }

    private void enableAuthorButtonWidgets(EditionMode editionMode) {
        if (editionMode.equals(EditionMode.NODATA) || editionMode.equals(EditionMode.DISPLAY)) {
            newAuthorWidget.setEnabled(true);
            saveAuthorWidget.setEnabled(false);
            cancelAuthorWidget.setEnabled(false);
        }
        if (editionMode.equals(EditionMode.NODATA)) {
            editAuthorWidget.setEnabled(false);
            deleteAuthorWidget.setEnabled(false);
        }
        if (editionMode.equals(EditionMode.DISPLAY)) {
            deleteAuthorWidget.setEnabled(true);
            editAuthorWidget.setEnabled(true);
        }
        if (editionMode.equals(EditionMode.NEW) || editionMode.equals(EditionMode.EDIT)) {
            editAuthorWidget.setEnabled(false);
            saveAuthorWidget.setEnabled(true);
            cancelAuthorWidget.setEnabled(true);
            deleteAuthorWidget.setEnabled(true);
        }
        if (editionMode.equals(EditionMode.NEW)) {
            newAuthorWidget.setEnabled(false);
        }
        if (editionMode.equals(EditionMode.EDIT)) {
            newAuthorWidget.setEnabled(true);
        }
    }

    /**
	 * Sets information about each widgets to guide the user.
	 */
	public void setWidgetsInfo() {
		/* simple fields info */
		id.setEmptyText("ex: 123456789");
		id.setToolTipConfig(new ToolTipConfig("ID is a string", "It should contain less than 256 characters"));
		hash.setEmptyText("ex: Hex456");
		hash.setToolTipConfig(new ToolTipConfig("Hash is a string", "It should contain less than 256 characters"));
		languageCode.setEmptyText("ex: en-GB");
		languageCode.setToolTipConfig(new ToolTipConfig("LanguageCode from RFC3066", "Language code format is \"[a-z](2)-[A-Z](2)\""));
		mimeType.setEmptyText("ex: mimetype");
		mimeType.setToolTipConfig(new ToolTipConfig("Mime Type is a string", "It should contain less than 256 characters"));
		repoUId.setEmptyText("ex: 1.2.7.0.3.2.37768.2007.2.2");
		repoUId.setToolTipConfig(new ToolTipConfig("Repository Unique ID is an OID",
				"As defined in the HL7 implementation for OID (http://www.hl7.org/implement/standards/product_brief.cfm?product_id=210)" + "\n"
						+ "OID format is \"[1-9](\\.[0-9]+)*]\""));
		uri.setEmptyText("ex: uriO");
		uri.setToolTipConfig(new ToolTipConfig("URI is a string", "It should contain less than 256 characters"));

		/* identifiers info */
		patientID.setEmptyTexts("ex: 76cc^^1.3.6367.2005.3.7&ISO", "ex: urn:uuid:6b5aea1a-625s-5631-v4se-96a0a7b38446");
		patientID.setToolTipConfigs(new ToolTipConfig("Value is a string", "It should contain less than 256 characters"), new ToolTipConfig(
				"idType is a string256 in HL7 CX format", "The required format is:\n" +
                "IDNumber^^^&OIDofAssigningAuthority&ISO"));
		uniqueId.setEmptyTexts("ex: 76cc^^1.3.6367.2005.3.7&ISO", "ex: 2008.8.1.35447");
		uniqueId.setToolTipConfigs(
				new ToolTipConfig("Value is a string", "76cc^^1.3.6367.2005.3.7&ISO"),
				new ToolTipConfig(
						"idType is an OID",
						"As defined in the HL7 implementation for OID (http://www.hl7.org/implement/standards/product_brief.cfm?product_id=210)\nOID format is \"[1-9](\\.[0-9]+)*]\""));

		/* name values info */
		legalAuthenticator.setEmptyTexts("ex: 11375^Welby^Marcus^J^Jr. MD^Dr^^^&1.2.840.113619.6.197&ISO");
		legalAuthenticator.setEditingFieldToolTip("A legal authenticator is a string256 in XCN format. It should be formatted as follow: \n<b>Identifier^LastName^FirstName[^SecondName[^FurtherGivenNames]][^Suffix][^Prefix]^AssigningAuthority</b>.");
        legalAuthenticator.addFieldValidator(new RegExValidator("^[0-9]+\\^(([A-Za-z]+\\.{0,1}\\s{0,1})+\\^){3,7}\\^{2}&[0-9]+(\\.[0-9]+)*(&ISO)$"));
//		sourcePatientId.setEmptyTexts("", "ex: 58642j65s^^^5.8.4");
//		sourcePatientId.setToolTipConfigs(null, new ToolTipConfig("Value is a String", "It shouldd contain les than 256 characters"));
        sourcePatientInfo.setToolTipConfig(new ToolTipConfig("Help on Source Patient Info?","These elements should contain demographics information of the patient to\n" +
                "whose medical record this document belongs. It is made several values and should be formatted as follow:<br/>" +
                "<b>PID-3</b> should include the source patient identifier.<br/>" +
                "<b>PID-5</b> should include the patient name.<br/>" +
                "<b>PID-8</b> should code the patient gender as <br/>" +
                "<center><i>M Male - F Female - O Other - U Unknown</i></center>" +
                "<b>PID-7</b> should include the patient date of birth.<br/>" +
                "<b>PID-11</b> should include the patient address.<br/>" +
                "PID-2, PID-4, PID-12 and PID-19 should not be used.<br/>"));
    }

}
