package gov.nist.hit.ds.utilities.csv;

import java.util.ArrayList;
import java.util.List;

public class CSVTable {
	List<CSVEntry> entries = new ArrayList<CSVEntry>();

	
	public List<CSVEntry> entries() {
		return entries;
	}
	
	public CSVTable add(CSVEntry entry) {
		entries.add(entry);
		return this;
	}
	
	public int size() {
		return entries.size();
	}
	
	public CSVEntry get(int entry) {
		return entries.get(entry);
	}
	
	public String get(int entry, int field) {
		try {
			return entries.get(entry).get(field).trim();
		} catch (Exception e) {
			
		}
		return "";
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (CSVEntry entry : entries) {
			buf.append(entry.toString());
			buf.append("\n");
		}
		
		return buf.toString();
	}
}
