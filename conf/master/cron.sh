# in fact it could be wlp0s20f0u5u1u3 how find that?
LAN=`/usr/bin/dmesg | /usr/bin/grep rt2800usb | /usr/bin/grep renamed | /usr/bin/tail -1 |  /usr/bin/awk ' { print $4 } ' | /usr/bin/sed 's;:;;'`
/usr/sbin/iw $LAN info
if [ $? -ne 0 ]; then
  /usr/bin/echo "failed $LAN not found"
  exit 1
fi
/usr/sbin/ifconfig $LAN | /usr/bin/grep  10.0.0.201
INTER=`sudo grep ^interface= /etc/hostapd/hostapd.conf |  /usr/bin/awk -F= ' { print $2 } '`
if [ $INTER != $LAN ]; then
  /usr/bin/echo " Please use $INTER as interface in /etc/hostapd/hostapd.conf"
  exit 1
fi

# check rfkill
ID=`/usr/sbin/iw $LAN info | /usr/bin/grep wiphy |  /usr/bin/awk ' { print $2 } '`
RFID=`/usr/sbin/rfkill | /usr/bin/grep wlan | /usr/bin/grep phy | /usr/bin/grep $ID | /usr/bin/awk ' { print $1 } '`
/usr/sbin/rfkill list $RFID | /usr/bin/grep blocked | /usr/bin/grep yes
if [ $? -eq 0 ]; then
  /usr/bin/echo "Try: rfkill unblock $RFID and retry"
  exit 1
fi
if [ $? -ne 0 ]; then
   /usr/bin/sudo /usr/sbin/ifconfig $LAN 10.0.0.201 netmask 255.255.255.0
   /usr/bin/sleep 3
   # /usr/bin/sudo /usr/bin/systemctl restart isc-dhcp-server
   /usr/bin/sudo /usr/bin/systemctl restart dhcpd.service
   /usr/bin/sudo /usr/bin/systemctl restart hostapd.service
fi
