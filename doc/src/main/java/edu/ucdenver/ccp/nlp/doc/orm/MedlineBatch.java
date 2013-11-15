package edu.ucdenver.ccp.nlp.doc.orm;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Basic;
import javax.persistence.GeneratedValue;

@Entity
@Table(name="medline_batches")
public class MedlineBatch {

	@Basic
	int pmid;
	public int getPmid() { return pmid; }
	public void setPmid(int pmid) { this.pmid = pmid; }

	@Id 
	int id;
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public MedlineBatch() {
		super();
		this.pmid=0;
		this.id=0;
	}

	public MedlineBatch(int pmid, int id) {
		super();
		this.pmid=pmid;
		this.id=id;
	}

}
