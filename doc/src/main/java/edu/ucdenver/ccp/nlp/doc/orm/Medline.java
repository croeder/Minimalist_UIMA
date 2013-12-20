/*
 Copyright (c) 2013, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 * Neither the name of the University of Colorado nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
