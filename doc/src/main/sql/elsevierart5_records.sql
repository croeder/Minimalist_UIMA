create table elsevierart5_records ( 
	path_pii varchar(150), 
	doi varchar(150), 
	pii varchar(150), 
	path varchar(150), 
	title varchar(250), 
	authors varchar(250), 
	dtd_version varchar(20), 
	dtd_file varchar(20), 
	abstract_only boolean, 
	has_abstract boolean, 
	id serial, 
	primary key(id));")
