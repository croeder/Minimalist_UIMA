
-- stored proc. to create the batches of pmid ids
-- run like this: medline=# select * from create_Medline_batches();

CREATE OR REPLACE FUNCTION create_medline_batches() RETURNS integer AS $$
DECLARE
	mysections RECORD;
	batch_num integer := 0;
	count integer := 0;
BEGIN
	RAISE NOTICE 'creating batches';	
	EXECUTE 'TRUNCATE medline_batches';
	FOR mysections in SELECT pmid FROM  sections WHERE name = 'Abstract' ORDER BY pmid  LOOP
		EXECUTE 'INSERT INTO  medline_batches VALUES (' ||  batch_num || ', ' ||  mysections.pmid || ')';
		count := count + 1;
		IF (count % 1000 = 0) THEN
			batch_num := batch_num + 1;
		END IF;
	 END LOOP;
	RAISE NOTICE 'done creating batches';	
	RETURN 1;
END;
$$ LANGUAGE plpgsql;
	

