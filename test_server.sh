var_db_file="D:\PWR\mgr\PracaMagisterska\Workspace\blockchainanalyze\database\test\data"
var_user=$(whoami)
var_hsql="C:/Users/$var_user/.m2/repository/org/hsqldb/hsqldb/2.4.0/hsqldb-2.4.0.jar"
echo "The current working directory $var_db_file"
echo "The current Hsql jar $var_hsql"

java -cp "$var_hsql" org.hsqldb.server.Server --database.0 file:$var_db_file -dbname.0 blockchainIndex
