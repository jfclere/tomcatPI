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
