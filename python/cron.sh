#!/usr/bin/bash

/usr/bin/ps -ef | /usr/bin/grep sendtemprecvmess | /usr/bin/grep -v grep 2>&1 >/dev/null
if [ $? -ne 0 ]; then
  /usr/bin/nohup /usr/bin/python /root/tomcatPI/python/sendtemprecvmess.py &
fi

