#!/bin/sh
# $Id: jspider.sh,v 1.9 2003/04/22 16:43:33 vanrogu Exp $
###
### modify by changshu.li  2015.07.23
### mail: lcs.005@163.com
###
echo ------------------------------------------------------------
echo JSpider startup script

if [ $# -lt 2 ]; then
  echo "must have 2 parame: url and config !!"
  echo ------------------------------------------------------------
  echo "360 => [http://zhushou.360.cn/list/index/cid/1 360download]"
  echo "dangbei => [http://www.dangbei.com/app/ dangbei]"
  echo ------------------------------------------------------------
  exit 1
fi

FILEPATH=$(cd "$(dirname "$0")"; pwd)

if [ ! -d "$JSPIDER_HOME" ]; then
  echo JSPIDER_HOME does not exist as a valid directory : $JSPIDER_HOME
  echo Defaulting to current directory
  JSPIDER_HOME=$FILEPATH/..
fi

jlog=$JSPIDER_HOME/logs/$2

echo JSPIDER_HOME=$JSPIDER_HOME
echo ------------------------------------------------------------

JSPIDER_OPTS=
JSPIDER_OPTS="$JSPIDER_OPTS -Djspider.home=$JSPIDER_HOME"
JSPIDER_OPTS="$JSPIDER_OPTS -Djspider.log=$jlog"
JSPIDER_OPTS="$JSPIDER_OPTS -Djava.util.logging.config.file=$JSPIDER_HOME/common/conf/logging/logging.properties"
JSPIDER_OPTS="$JSPIDER_OPTS -Dlog4j.configuration=conf/logging/log4j.xml"

JSPIDER_CLASSPATH=$JSPIDER_HOME
cpath=$JSPIDER_HOME/lib
for i in `ls $cpath/*.jar`
do
  JSPIDER_CLASSPATH=$JSPIDER_CLASSPATH:$i;
done

JSPIDER_CLASSPATH="$JSPIDER_CLASSPATH:$JSPIDER_HOME/common"
JSPIDER_CLASSPATH="$JSPIDER_CLASSPATH:$CLASSPATH"

run=`ps aux|grep java|grep "$2" |grep -v grep`
#echo $run
if [ "" != "$run" ]; then
	echo "Error !! This task is running, waiting for it over"
 exit 1
fi

#debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=36088"
jcls="net.javacoding.jspider.JSpider"
mkdir -p $jlog
fdate=$(date +%Y%m%d)
java $debug -cp $JSPIDER_CLASSPATH:$CLASSPATH $JSPIDER_OPTS $jcls $1 $2 1>$jlog/console.$fdate.log 2>$jlog/error.$fdate.log &
echo "Success !! Spricder is start run!"

