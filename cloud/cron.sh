#!/bin/bash
/usr/bin/dmesg | /usr/bin/grep brcm
if [ $? -ne 0 ]; then
  /usr/sbin/reboot
fi

/usr/sbin/ifconfig | /usr/bin/grep 172.16.42.1
if [ $? -ne 0 ]; then
  /usr/sbin/ifconfig wlan0 172.16.42.1 netmask 255.255.255.0
  /usr/bin/sleep 5
  /usr/bin/systemctl restart dhcpd
fi

/usr/bin/cat /proc/sys/net/ipv4/ip_forward | /usr/bin/grep 1
if [ $? -ne 0 ]; then
  /usr/bin/echo 1 > /proc/sys/net/ipv4/ip_forward

  /usr/sbin/iptables -X
  /usr/sbin/iptables -F
  /usr/sbin/iptables -A FORWARD -i eth0 -o eth0 -s 172.16.42.0/24 -m state --state NEW -j ACCEPT
  /usr/sbin/iptables -A FORWARD -m state --state ESTABLISHED,RELATED -j ACCEPT
  /usr/sbin/iptables -A FORWARD -i eth0 -o wlan0 -j ACCEPT
  /usr/sbin/iptables -A POSTROUTING -t nat -j MASQUERADE

fi
