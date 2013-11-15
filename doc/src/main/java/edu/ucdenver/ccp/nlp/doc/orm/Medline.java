package edu.ucdenver.ccp.nlp.doc.orm;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Basic;
import javax.persistence.GeneratedValue;

/***
          Table "public.sections"
 Column  |          Type          | Modifiers 
---------+------------------------+-----------
 pmid    | bigint                 | not null
 seq     | smallint               | not null
 name    | character varying(64)  | not null
 label   | character varying(256) | 
 content | text                   | not null
Indexes:
    "sections_pkey" PRIMARY KEY, btree (pmid, seq)
***/

@Entity
@Table(name="sections")
public class Medline {

	@Basic
	int pmid;
	public int getPmid() { return pmid; }
	public void setPmid(int pmid) { this.pmid = pmid; }

	@Id 
	int seq;
	public int getSeq() { return seq; }
	public void setSeq(int seq) { this.seq = seq; }

	@Basic
	String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	@Basic 
	String label;
	public String getLabel() { return label; }
	public void setLabel(String label) { this.label = label; }

	@Basic
	String content;	
	public String getContentt() { return content; }
	public void setContent(String content) { this.content = content; }
	

	public Medline() {
		super();
		this.pmid=0;
		this.seq=0;
		this.name="";
		this.label="";
		this.content="";
	}

	public Medline(int pmid, int seq, String name, String label, String content) {
		super();
		this.pmid=pmid;
		this.seq=seq;
		this.name=name;
		this.label=label;
		this.content=content;
	}

}
