
-- stored proc. to create the batches of pii ids
-- load like this: bash> psql -d medline -f create_elsevierart5_batches.sql
-- run like this: medline=# select * from create_elsevierart5_batches();

CREATE OR REPLACE FUNCTION create_elsevierart5_batches() RETURNS integer AS $$
DECLARE
	pii_record RECORD;
	batch_num integer := 0;
	count integer := 0;
BEGIN
	RAISE NOTICE 'creating batches';	
	EXECUTE 'TRUNCATE elsevierart5_batches';

	FOR pii_record in 
		SELECT max(id) as max_id, pii FROM elsevierart5_records
			WHERE dtd_version = '5.0.2'
		GROUP BY pii 
	LOOP
		EXECUTE 'INSERT INTO  elsevierart5_batches VALUES (' ||  batch_num || ', ' ||  pii_record.max_id || ')';
		count := count + 1;
		IF (count % 1000 = 0) THEN
			batch_num := batch_num + 1;
		END IF;
	END LOOP;

	RAISE NOTICE 'done creating batches';	
	RETURN 1;
END;
$$ LANGUAGE plpgsql;
	

