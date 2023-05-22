#!/bin/bash 

ORGNET=eth0
DESTNET=eth0

#service firewalld stop

a=`cat /proc/sys/net/ipv4/ip_forward`
if [ $a == "1" ]; then
  # already done
  exit 0
fi

# find the network that is connected to the internet
/usr/sbin/ifconfig wlan0 | grep 192.168
if [ $? -eq 0 ]; then
  ORGNET=wlan0
fi
/usr/sbin/ifconfig wlan1 | grep 192.168
if [ $? -eq 0 ]; then
  ORGNET=wlan1
fi
/usr/sbin/ifconfig alf0 | grep 192.168
if [ $? -eq 0 ]; then
  ORGNET=alf0
fi

/usr/sbin/ifconfig wlan1 | grep 10.0.0.201
if [ $? -eq 0 ]; then
  DESTNET=wlan1
fi
/usr/sbin/ifconfig wlan0 | grep 10.0.0.201
if [ $? -eq 0 ]; then
  DESTNET=wlan0
fi
/usr/sbin/ifconfig alf0 | grep 10.0.0.201
if [ $? -eq 0 ]; then
  DESTNET=alf0
fi

if [ $DESTNET == ${ORGNET} ]; then
  echo "Something wrong... $DESTNET"
  exit 1
fi

echo 1 > /proc/sys/net/ipv4/ip_forward

echo "$DESTNET to ${ORGNET}"
/usr/sbin/iptables -P FORWARD ACCEPT
/usr/sbin/iptables --table nat -A POSTROUTING -o ${ORGNET} -j MASQUERADE

/usr/sbin/iptables -A FORWARD -i ${ORGNET} -o ${DESTNET} -m state --state RELATED,ESTABLISHED -j ACCEPT
/usr/sbin/iptables -A FORWARD -i ${DESTNET} -o ${ORGNET} -j ACCEPT

#something like: (Configure wlan1 to be a WiFi Client)
#iptables -t nat -A POSTROUTING -o wlan0 -j MASQUERADE
#iptables -A FORWARD -i wlan0 -o wlan1 -m state --state RELATED,ESTABLISHED -j ACCEPT
#iptables -A FORWARD -i wlan1 -o wlan0 -j ACCEPT
