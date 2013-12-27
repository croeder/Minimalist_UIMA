create table primitive_result (
	id serial,
	ontology_name varchar(100),
	ontology_id  varchar(100),
	span_start integer,
	span_end integer,
	sentence_num integer,
	doc_id varchar(150),
	id_type varchar(10),
	pmid bigint,
	primary key(id) );
	
