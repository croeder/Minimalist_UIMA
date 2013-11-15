package edu.ucdenver.ccp.nlp.doc.orm;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Basic;
import javax.persistence.GeneratedValue;

@Entity
@Table(schema="pmcoa", name="pmc_batches")
public class PmcBatch {

	@Basic
	int pmid;
	public int getPmid() { return pmid; }
	public void setPmid(int pmid) { this.pmid = pmid; }

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
	int id;
	public int getId() { return id; }
	public void setSeq(int id) { this.id = id; }

	public PmcBatch() {
		super();
		pmid=0;
		id=0;
	}

	public PmcBatch(int pmid, int id) {
		super();
		this.pmid=pmid;
		this.id=id;
	}

}
