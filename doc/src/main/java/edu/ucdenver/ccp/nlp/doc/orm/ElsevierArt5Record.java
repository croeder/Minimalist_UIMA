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
@Table(name="elsevierart5_records")
public class ElsevierArt5Record {

	@Id 
	int id;
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	@Basic
	String pathPii;
	public String getPathPii() { return pathPii; }
	public void setPathPii(String pmid) { this.pathPii = pathPii; }

	@Basic
	String pii;
	public String getPii() { return pii; }
	public void setPii(String pii) { this.pii = pii; }

	@Basic
	String path;
	public String getPath() { return path; }
	public void setPath(String pmid) { this.path = path; }

	@Basic
	String title;
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	@Basic
	String authors;
	public String getAuthors() { return authors; }
	public void setAuthors(String authors) { this.authors = authors; }

	@Basic
	String dtdVersion;
	public String getDtdVersion() { return dtdVersion; }
	public void setDtdVersion(String dtdVersion) { this.dtdVersion = dtdVersion; }

	@Basic
	String dtdFile;
	public String getDtdFile() { return dtdFile; }
	public void setDtdFile(String dtdFile) { this.dtdFile = dtdFile; }

	/**
	 * really poorly named, should be hasSections
     * TODO FIX change column name
	 */
	@Basic
	boolean abstractOnly;
	public boolean getHasSections() { return abstractOnly; }
	public void setHashSections(boolean hasSections) { this.abstractOnly = hasSections; }

	@Basic
	boolean hasAbstract;
	public boolean getHasAbstract() { return hasAbstract; }
	public void setHasAbstract(boolean hasAbstract) { this.hasAbstract = hasAbstract; }

	public ElsevierArt5Record() {
		super();

		this.id=0;
		this.pathPii="";
		this.pii="";
		this.path="";
		this.title="";
		this.authors="";
		this.dtdVersion="";
		this.dtdFile="";
		this.abstractOnly=false;
		this.hasAbstract=true;
	}

	public ElsevierArt5Record(int id, String pathPii, String pii, String path,
		String title, String authors, String dtdVersion, String dtdFile, 
		boolean hasSections, boolean hasAbstract ){

		super();

		this.id=id;
		this.pathPii=pathPii;
		this.pii=pii;
		this.path=path;
		this.title=title;
		this.authors=authors;
		this.dtdVersion=dtdVersion;
		this.dtdFile=dtdFile;
		this.abstractOnly=hasSections;
		this.hasAbstract=hasAbstract;
	}

}
