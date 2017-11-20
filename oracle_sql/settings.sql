show parameter target;

--NAME                                               TYPE        VALUE                                                                                                
---------------------------------------------------- ----------- ---------------------------------------------------------------------------------------------------- 
--archive_lag_target                                 integer     0                                                                                                    
--db_big_table_cache_percent_target                  string      0                                                                                                    
--db_flashback_retention_target                      integer     1440                                                                                                 
--fast_start_io_target                               integer     0                                                                                                    
--fast_start_mttr_target                             integer     0                                                                                                    
--memory_max_target                                  big integer 0                                                                                                    
--memory_target                                      big integer 0                                                                                                    
--parallel_servers_target                            integer     64                                                                                                   
--pga_aggregate_target                               big integer 3240M                                                                                                
--sga_target                                         big integer 0    

alter system set sga_target = 0;
alter system set memory_target = 1648M;

-- backup
alter system set sga_target = 0M;
alter system set memory_target = 0M;

alter system set sga_target = 0;
alter system set sga_target = 1300M;
alter system set db_cache_size = 1024M;
