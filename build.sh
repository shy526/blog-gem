#!/bin/sh
APP_NAME="blog_gem"
# 移动至目标目录
RUN_PATH="./"
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
    if [ ! -d "$1" ]; then
        warn_log "$1 inexistence"
    else
        pid=`cat "$1"`
       `kill -9 "$pid"`
    fi
}

echo "$APP_NAME"
if [ !"$RUN_PATH" ];then
  error_log "is null"
fi
info_log "开始移动目标文件至 $RUN_PATH"
`cp "./blog-schedule/target/blog-schedule-1.0-SNAPSHOT.jar" "$RUN_PATH"`
`cp "./blog-github/target/blog-github-1.0-SNAPSHOT.jar" "$RUN_PATH"`

info_log "杀死旧进程"
kill_pid "blog-schedule.pid"
kill_pid "blog-github.pid"
info_log "启动项目"
`nohup java -jar "$RUN_PATH""blog-schedule-1.0-SNAPSHOT.jar" > "$RUN_PATH""logs/blog-schedule.out" &`
 echo $! > blog-schedule.pid
`nohup java -jar "$RUN_PATH""blog-github-1.0-SNAPSHOT.jar" > "$RUN_PATH""logs/blog-github.out" &`
 echo $! > blog-github.pid

