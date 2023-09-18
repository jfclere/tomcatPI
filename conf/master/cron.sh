LAN=wlp0s20f0u1
/usr/sbin/iw $LAN info
if [ $? -ne 0 ]; then
  LAN=wlan0
fi
/usr/sbin/ifconfig $LAN | /usr/bin/grep  10.0.0.201
if [ $? -ne 0 ]; then
   /usr/bin/sudo /usr/sbin/ifconfig $LAN 10.0.0.201 netmask 255.255.255.0
   /usr/bin/sleep 3
   # /usr/bin/sudo /usr/bin/systemctl restart isc-dhcp-server
   /usr/bin/sudo /usr/bin/systemctl restart dhcpd.service
   /usr/bin/sudo /usr/bin/systemctl restart hostapd.service
fi

/usr/bin/cat /proc/sys/net/ipv4/ip_forward | /usr/bin/grep 0 1>/dev/null
if [ $? -eq 0 ]; then
  ESSID=`/usr/sbin/iwconfig 2>/dev/null | /usr/bin/grep ESSID | /usr/bin/awk -F : ' { print $2 } '`
  /usr/bin/date >> /home/pi/log.txt
  /usr/bin/echo "using $ESSID" >> /home/pi/log.txt
  /usr/bin/sudo /usr/sbin/service firewalld stop
  /usr/bin/sleep 1
  /usr/bin/sudo /usr/bin/bash -c '/usr/bin/echo 1 > /proc/sys/net/ipv4/ip_forward'
  /usr/bin/sudo /usr/sbin/iptables-restore /home/pi/tomcatPI/conf/master/gateway.ok.saintismier
fi
