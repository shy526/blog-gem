#!/bin/sh
# jenkins 上 防止 进程kill
#BUILD_ID=DONTKILLME
APP_NAME="blog_gem"
# 移动至目标目录
RUN_PATH="/home/project/blog-gem/"
project1="blog-github/"
project2="blog-gem-api/"
 log(){
    echo `date "+%Y-%m-%d %H:%M:%S  ----[$1]----$APP_NAME:$2"`
}
error_log(){
   log "ERROR" "$1"
}
info_log(){
   log "info" "$1"
}
warn_log(){
    log "warn" "$1"
}
kill_pid(){
    if [ ! -f $1 ]; then
        warn_log "$1 inexistence"
    else
        pid=`cat "$1"`
       `kill -9 "$pid"`
    fi
}

assert(){
if [ $? -eq 0 ]
then
    info_log "$1:succeed"
else
    info_log "$1:failure"
    return 12
fi
}

info_log "$APP_NAME"

if [ ! $RUN_PATH ];then
  error_log "is null"
fi
# 这里jenkis 不需要 __start
info_log "拉取更新"
git pull
info_log "清理"
mvn clean
info_log "重新打包"
mvn package
# 这里jenkis 不需要 __end
info_log "杀死旧进程"
#kill_pid "$RUN_PATH""blog-schedule.pid"
kill_pid "$RUN_PATH""$project1""blog-github.pid"
kill_pid "$RUN_PATH""$project2""blog-gem-api.pid"

info_log "删除旧文件"
`rm -rf "$RUN_PATH"blog-*`

info_log "开始移动目标文件至$RUN_PATH"
#`cp "./blog-schedule/target/blog-schedule-1.0-SNAPSHOT.jar" "$RUN_PATH"`
#assert "blog-schedule"
`cp "./blog-github/target/blog-github-1.0-SNAPSHOT.jar" "$RUN_PATH""$project1"`
assert "blog-github"

`cp "./blog-github/target/blog-gem-api-1.0-SNAPSHOT.jar" "$RUN_PATH""$project2"`
assert "blog-gem-api"

info_log "启动项目"
#`nohup java -jar "$RUN_PATH""blog-schedule-1.0-SNAPSHOT.jar" > "$RUN_PATH""logs/blog-schedule.out" 2>&1 & echo $! > "$RUN_PATH"blog-schedule.pid`
#assert "blog-schedule"
`nohup java -jar "$RUN_PATH""$project1""blog-github-1.0-SNAPSHOT.jar" > "$RUN_PATH""$project1""logs/blog-github.out" 2>&1 & echo $! > "$RUN_PATH""$project1"blog-github.pid`
assert "blog-github"
`nohup java -jar "$RUN_PATH""$project2""blog-gem-api-1.0-SNAPSHOT.jar" > "$RUN_PATH""$project2""logs/blog-gem-api.out" 2>&1 & echo $! > "$RUN_PATH""$project2"blog-gem-api.pid`
assert "blog-gem-api"




