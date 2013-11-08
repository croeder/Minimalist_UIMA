package  com.croeder.relational_document_index.orm;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Basic;
import javax.persistence.GeneratedValue;

@Entity
@Table(schema="pmcoa", name="pmc")
public class Pmc {

	@Basic
	int pmid;
	public int getPmid() { return pmid; }
	public void setPmid(int pmid) { this.pmid = pmid; }

	@Basic
	String path;
	public String getPath() { return path; }
	public void setPath(String path) { this.path = path; }

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
	int seq;
	public int getSeq() { return seq; }
	public void setSeq(int seq) { this.seq = seq; }

	public Pmc() {
		super();
		pmid=0;
		seq=0;
		path="";
	}

	public Pmc(int pmid, int seq, String path) {
		super();
		this.pmid=pmid;
		this.seq=seq;
		this.path=path;
	}

}
