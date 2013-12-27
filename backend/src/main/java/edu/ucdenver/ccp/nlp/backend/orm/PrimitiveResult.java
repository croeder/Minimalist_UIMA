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
package edu.ucdenver.ccp.nlp.backend.orm;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;

/***
          Table "public.primitive_result"
    Column     |          Type          | Modifiers 
---------------+------------------------+-----------
 id            | serial                 | not null
 ontology_name | character varying(100) | 
 ontology_id   | character varying(100) | 
 span_start    | integer                | 
 span_end      | integer                | 
 sentence_num  | integer                | 
 doc_id        | character varying(150) | 
 id_type       | character varying(10)  | 
 pmid          | bigint                 | 
Indexes:
    "primitive_result_pkey" PRIMARY KEY, btree (id)
***/

@Entity
@Table(name="primitive_result")
public class PrimitiveResult {

	@Id
	@SequenceGenerator(name="primitive_result_id_seq",
					sequenceName="primitive_result_id_seq",
					allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE,
					generator="primitive_result_id_seq")
	@Column(name="id", updatable=false)
	int id;
	//public int getId() { return id; }
	//public void setId(int id) { this.id = id; }

	@Basic
	@Column(name="ontology_name")
	String ontologyName;
	public String getOntologyName() { return ontologyName; }
	public void setId(String ontologyName) { this.ontologyName = ontologyName; }

	@Basic
	@Column(name="ontology_id")
	String ontologyId;	
	public String getOntologyId() { return ontologyId; }
	public void setOntologyId(String ontologyId) { this.ontologyId = ontologyId; }

	@Basic 
	int spanStart;
	@Column(name="span_start")
	public int getSpanStart() { return spanStart; }
	public void setSpanStart(int spanStart) { this.spanStart = spanStart; }

	@Basic 
	@Column(name="span_end")
	int spanEnd;
	public int getSpanEnd() { return spanEnd; }
	public void setSpanEnd(int spanEnd) { this.spanEnd = spanEnd; }

	@Basic 
	@Column(name="sentence_num")
	int sentenceNum;
	public int getSentenceNum() { return sentenceNum; }
	public void setSentenceNum(int sentenceNum) { this.sentenceNum = sentenceNum; }

	@Basic
	@Column(name="doc_id")
	String docId;
	public String getDocId() { return docId; }
	public void setDocId(String docId) { this.docId = docId; }
	
	@Basic
	@Column(name="id_type")
	String idType;
	public String getIdType() { return idType; }
	public void setIdType(String idType) { this.idType = idType; }

	//public PrimitiveResult(int id, String ontologyName, String ontologyId, int spanStart, int spanEnd, int sentenceNum, String docId, String idType) {
	public PrimitiveResult(String ontologyName, String ontologyId, int spanStart, int spanEnd, int sentenceNum, String docId, String idType) {
		//this.id = id;
		this.ontologyName = ontologyName;
		this.ontologyId = ontologyId;
		this.spanStart = spanStart;
		this.spanEnd = spanEnd;
		this.sentenceNum = sentenceNum;
		this.docId = docId;
		this.idType = idType;
	}

	public PrimitiveResult() {
		//this.id = 0;
		this.ontologyName = "";
		this.ontologyId = ""; 
		this.spanStart = -1;
		this.spanEnd = -1;
		this.sentenceNum = -1;
		this.docId = "";
		this.idType = "";
	}

	public String toString() {
		return id + " " + ontologyName + ":" + ontologyId;
	}
}
	
