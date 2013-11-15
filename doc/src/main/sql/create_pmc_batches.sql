
-- stored proc. to create the batches of pmid ids
-- load like this: psql> \i create_pmc_batches.sql
-- run like this: medline=# select * from create_Medline_batches();

CREATE OR REPLACE FUNCTION create_pmc_batches() RETURNS integer AS $$
DECLARE
	mysections RECORD;
	batch_num integer := 0;
	count integer := 0;
BEGIN
	RAISE NOTICE 'creating batches';	
	EXECUTE 'TRUNCATE pmcoa.pmc_batches';
	FOR mysections in SELECT distinct pmid FROM  pmcoa.pmc  ORDER BY pmid  LOOP
		EXECUTE 'INSERT INTO  pmcoa.pmc_batches VALUES (' ||  batch_num || ', ' ||  mysections.pmid || ')';
		count := count + 1;
		IF (count % 1000 = 0) THEN
			batch_num := batch_num + 1;
		END IF;
	 END LOOP;
	RAISE NOTICE 'done creating batches';	
	RETURN 1;
END;
$$ LANGUAGE plpgsql;
	

