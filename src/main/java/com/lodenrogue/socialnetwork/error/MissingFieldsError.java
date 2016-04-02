package com.lodenrogue.socialnetwork.error;

import java.util.List;

public class MissingFieldsError {
	private List<String> missingFields;

	public MissingFieldsError(List<String> fields) {
		this.missingFields = fields;
	}

	public List<String> getMissingFields() {
		return missingFields;
	}

	public void setMissingFields(List<String> fields) {
		this.missingFields = fields;
	}

}
