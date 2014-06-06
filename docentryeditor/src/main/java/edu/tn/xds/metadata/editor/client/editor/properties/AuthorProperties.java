package edu.tn.xds.metadata.editor.client.editor.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.tn.xds.metadata.editor.shared.model.Author;

/**
 * Property Access interface for Author entity. It handles the access to
 * author's attributes for GXT Stores. It handles the access to a key for the
 * author object as well as the author person value.
 * 
 * This Property access only handles the Author's attribute authorPerson.
 * 
 * @see Author
 * 
 * @author Olivier
 * 
 */
public interface AuthorProperties extends PropertyAccess<Author> {
	/**
	 * Returns the KeyProvider for Author. It is consider authorPerson will be
	 * the key of the entity.
	 * 
	 * @return a KeyProvider for Author
	 */
	@Path("authorPerson")
	ModelKeyProvider<Author> key();

	/**
	 * Returns ValueProvider for Author. It handles the access to the
	 * authorPerson Author's attribute.
	 * 
	 * @return Author ValueProvier for authorPerson
	 */
	@Path("authorPerson.string")
	ValueProvider<Author, String> authorPerson();
}
