/usr/sbin/ifconfig wlan0 | /usr/bin/grep  10.0.0.201
if [ $? -ne 0 ]; then
   /usr/sbin/ifconfig wlan0 10.0.0.201 netmask 255.255.255.0
   /usr/bin/sleep 3
   /usr/bin/systemctl start dhcpd
fi

/usr/bin/cat /proc/sys/net/ipv4/ip_forward | /usr/bin/grep 0 1>/dev/null
if [ $? -eq 0 ]; then
  /usr/sbin/iwconfig 2>/dev/null | grep Livebox-C640 > /dev/null
  if [ $? -eq 0 ]; then
    /usr/sbin/service firewalld stop
    /usr/bin/sleep 1
    /usr/bin/echo 1 > /proc/sys/net/ipv4/ip_forward
    /usr/sbin/iptables-restore /root/gateway.ok.saintismier
    /usr/bin/date >> /root/log.txt
    /usr/bin/echo "using Livebox-C640" >> /root/log.txt
  fi
  # devolo-bcf2afb
  # devolo-edc
  /usr/sbin/iwconfig 2>/dev/null | grep devolo > /dev/null
  if [ $? -eq 0 ]; then
    /usr/sbin/service firewalld stop
    /usr/bin/sleep 1
    /usr/bin/echo 1 > /proc/sys/net/ipv4/ip_forward
    /usr/sbin/iptables-restore /root/gateway.ok.saintismier
    /usr/bin/date >> /root/log.txt
    /usr/bin/echo "using devolo" >> /root/log.txt
  fi
  /usr/sbin/iwconfig 2>/dev/null | grep 3307X0354 > /dev/null
  if [ $? -eq 0 ]; then
    /usr/sbin/service firewalld stop
    /usr/bin/sleep 1
    /usr/bin/echo 1 > /proc/sys/net/ipv4/ip_forward
    /usr/sbin/iptables-restore /root/gateway.ok.saintismier
    /usr/bin/date >> /root/log.txt
    /usr/bin/echo "using 3307X0354" >> /root/log.txt
  fi
fi
