package com.exadel.eom.officemap.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "officemaps")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Officemap {

	@Id
	private String name;

	private Date lastSeen;

    private List<EmployeeLocation> employeeLocationList;

	@Length(min = 0, max = 20_000)
	private String note;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

    public List<EmployeeLocation> getEmployeeLocationList() {
        return employeeLocationList;
    }

    public void setEmployeeLocationList(List<EmployeeLocation> employeeLocationList) {
        this.employeeLocationList = employeeLocationList;
    }
}
