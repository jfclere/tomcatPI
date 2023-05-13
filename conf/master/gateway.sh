#!/bin/bash 

#service firewalld stop

a=`cat /proc/sys/net/ipv4/ip_forward`
if [ $a == "1" ]; then
  # already done
  echo "Done..."
  exit 0
fi

echo 1 > /proc/sys/net/ipv4/ip_forward

#iptables-restore gateway.ok.saintismier
#iptables-restore gateway.ok
/usr/sbin/ifconfig wlan1 | grep 10.0.0.201
if [ $? -eq 0 ]; then
  # wlan1 is configurated to forward wifi.
  /usr/sbin/iptables -P FORWARD ACCEPT
  /usr/sbin/iptables --table nat -A POSTROUTING -o wlan0 -j MASQUERADE

  /usr/sbin/iptables -A FORWARD -i wlan0 -o wlan1 -m state --state RELATED,ESTABLISHED -j ACCEPT
  /usr/sbin/iptables -A FORWARD -i wlan1 -o wlan0 -j ACCEPT

else
  /usr/sbin/iptables -P FORWARD ACCEPT
  /usr/sbin/iptables --table nat -A POSTROUTING -o wlan1 -j MASQUERADE

  /usr/sbin/iptables -A FORWARD -i wlan1 -o wlan0 -m state --state RELATED,ESTABLISHED -j ACCEPT
  /usr/sbin/iptables -A FORWARD -i wlan0 -o wlan1 -j ACCEPT

fi 
#something like: (Configure wlan1 to be a WiFi Client)
#iptables -t nat -A POSTROUTING -o wlan0 -j MASQUERADE
#iptables -A FORWARD -i wlan0 -o wlan1 -m state --state RELATED,ESTABLISHED -j ACCEPT
#iptables -A FORWARD -i wlan1 -o wlan0 -j ACCEPT
