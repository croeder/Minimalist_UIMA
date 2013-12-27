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
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Basic;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;

/***
Table "public.compound_result"
  Column     |  Type   | Modifiers 
-------------+---------+-----------
 id          | serial  | not null
 agent_id    | integer | 
 theme_id    | integer | 
 location_id | integer | 
Indexes:
    "compound_result_pkey" PRIMARY KEY, btree (id)
***/

@Entity
@Table(name="compound_result")
public class CompoundResult {

	@Id
	@SequenceGenerator(name="compound_result_id_seq",
					sequenceName="compound_result_id_seq",
					allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE,
					generator="compound_result_id_seq")
	@Column(name="id", updatable=false)
	private Integer id;
	//public int getId() { return id; }
	//public void setId(int id) { this.id = id; }

	@OneToOne
	@JoinColumn(name="agent_id")
	PrimitiveResult agent;
	public PrimitiveResult getAgent() { return agent; }

	@OneToOne
	@JoinColumn(name="theme_id")
	PrimitiveResult theme;
	public PrimitiveResult getTheme() { return theme; }

	@OneToOne
	@JoinColumn(name="location_id")
	PrimitiveResult location;
	public PrimitiveResult getLocation() { return location; }

	public CompoundResult() {
	}

	public CompoundResult(PrimitiveResult agent, PrimitiveResult theme, PrimitiveResult location) {
		this.agent = agent;
		this.theme = theme;
		this.location = location;
	}

	public String toString() {
		return "agent:(" + agent.toString() + "), theme:(" + theme.toString() + "), location:(" +  location.toString() + ")";
	}

}
	
