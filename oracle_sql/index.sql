drop Index ix_out_address;
CREATE INDEX 
   ix_out_address
ON 
   transactionoutput (address) 
TABLESPACE 
   BLOCKINDEX_DB;
   
drop Index ix_in_address;
CREATE INDEX 
   ix_in_address
ON 
   transactioninput (address) 
TABLESPACE 
   BLOCKINDEX_DB;
   
drop Index ix_block_gen_date;
CREATE INDEX 
   ix_block_gen_date
ON 
   blockindex (GENERATEDDATE) 
TABLESPACE 
   BLOCKINDEX_DB;