create tablespace 
   blockindex_db
datafile   
  'D:\PWR\mgr\PracaMagisterska\Workspace\oracle_files\blockindex_db.dbf'
size 16G AUTOEXTEND on
maxsize unlimited flashback off;
   
alter tablespace blockindex_db add datafile 'D:\PWR\mgr\PracaMagisterska\Workspace\oracle_files\blockindex_db2.dbf'
size 16G AUTOEXTEND on
maxsize unlimited;
   
--SYSAUX 
SELECT DBMS_METADATA.GET_DDL('USER','BLOCKINDEX') from dual;
 ALTER TABLESPACE blockindex_db drop datafile 'D:\PWR\mgr\PracaMagisterska\Workspace\oracle_files\blockindex_db2.dbf';
 
 purge recyclebin;
 