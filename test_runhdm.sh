var_user=$(whoami)
var_hsql="C:/Users/$var_user/.m2/repository/org/hsqldb/hsqldb/2.4.0/hsqldb-2.4.0.jar"
echo "The current Hsql jar $var_hsql"

java -cp $var_hsql org.hsqldb.util.DatabaseManager  