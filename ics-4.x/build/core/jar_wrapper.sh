pushd . 1>/dev/null 2>&1
currPath=`pwd`
opt=$1
shift
jarfile=$1
classPath=$2
shift
shift
cd $classPath
jar $opt $currPath/$jarfile $@ -C . `find . -type f |sort | tr "\n" " " `
popd 1>/dev/null 2>&1

