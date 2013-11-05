
/**
Medline (records)
*pmid*:BIGINT, _status_:ENUM(state), _journa_l:VARCHAR(256), _pub_date_:VARCHAR(256), issue:VARCHAR(256), pagination:VARCHAR(256), _created_:DATE, completed:DATE, revised:DATE, modified:DATE
Section (sections)
*pmid*:FK(Medline), *seq*:SMALLINT, _name_:ENUM(section), label:VARCHAR(256), _content_:TEXT
Author (authors)
*pmid*:FK(Medline), *pos*:SMALLINT, _name_:TEXT, initials:VARCHAR(128), forename:VARCHAR(128), suffix:VARCHAR(128),
PublicationType (publication_types)
*pmid*:FK(Medline), *value*:VARCHAR(256)
Descriptor (descriptors)
*pmid*:FK(Medline), *num*:SMALLINT, major:BOOL, _name_:TEXT
Qualifier (qualifiers)
*pmid*:FK(Descriptor), *num*:FK(Descriptor), sub:SMALLINT, major:BOOL, _name_:TEXT
Identifier (identifiers)
*pmid*:FK(Medline), *namespace*:VARCHAR(32), _value_:VARCHAR(256)
Database (databases)
*pmid*:FK(Medline), *name*:VARCHAR(32), *accession*:VARCHAR(256)
Chemical (chemicals)
*pmid*:FK(Medline), *idx*:VARCHAR(32), uid:VARCHAR(256), _name_:VARCHAR(256)
Keyword (keywords)
*pmid*:FK(Medline), *owner*:ENUM(owner), *cnt*:SMALLINT, major:BOOL, _value_:TEXT
bold (Composite) Primary Key
_italic_ NOT NULL (Strings that may not be NULL are also never empty.)
*/
