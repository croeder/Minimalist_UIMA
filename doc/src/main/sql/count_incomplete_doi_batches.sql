select count(records.pmid), medline_batches.id
from records left join medline_doi on records.pmid = medline_doi.pmid
join medline_batches on records.pmid = medline_batches.pmid
where medline_doi.doi is null  
group by medline_batches.id  
having count(records.pmid) <1000;

