#!/bin/bash
# Here we used the AWUS036ACH a pain due to driver...

# first install wifi information from /home/pi/wpa_supplicant.conf
# convert wpa_supplicant.conf in nmcli commands
getpass() {
sid=$1
has_ssid=false
has_psk=false
has_key_mgmt=false
while IFS= read -r line; do
  case "$line" in
     *ssid=*)
       ssid=`/usr/bin/echo $line | awk -F = ' { print $2 } '`
       has_ssid=true
       ;;
     *psk=*)
       psk=`/usr/bin/echo $line | awk -F = ' { print $2 } '`
       has_psk=true
       ;;
     *key_mgmt=*)
       key_mgmt=`/usr/bin/echo $line | awk -F = ' { print $2 } '`
       has_key_mgmt=true
       ;;
  esac
  if $has_ssid && $has_psk && $has_key_mgmt; then
    has_ssid=false
    has_psk=false
    has_key_mgmt=false
    name=`/usr/bin/echo $ssid | awk -F \" ' { print $2 } '`
    if [ "$sid" = "$name" ]; then
      pass=`/usr/bin/echo $psk | awk -F \" ' { print $2 } '`
      /usr/bin/echo "$pass"
    fi
  fi
done < /home/pi/wpa_supplicant.conf
}

#
# Check for wifi
checkstartwifi()
{
  /usr/sbin/iw wlan1 link | /usr/bin/grep SSID > /dev/null
  if [ $? -ne 0 ]; then
    # We don't have a connection...
    # Try to connect to one of wifi we can see
    for sid in `/usr/bin/nmcli -t -f SSID device wifi`
    do
      /usr/bin/echo "sid: $sid"
      pass=`getpass $sid`
      if [ "x$pass" = "x" ]; then
        /usr/bin/echo "Ignore $sid not in our list"
      else
        /usr/bin/echo "trying $sid $pass"
        /usr/bin/sudo /usr/bin/nmcli device wifi connect $sid password $pass ifname wlan1
      fi
    done
  fi
}
/usr/sbin/iw wlan0 info
if [ $? -ne 0 ]; then
  /usr/bin/echo "failed wlan0 not found"
  exit 1
fi
INTER=`/usr/bin/sudo /usr/bin/grep ^interface= /etc/hostapd/hostapd.conf |  /usr/bin/awk -F= ' { print $2 } '`
if [ $INTER != wlan0 ]; then
  /usr/bin/echo " Please use $INTER as interface in /etc/hostapd/hostapd.conf"
  exit 1
fi

echo "Unblocking Wi-Fi and setting Regulatory Domain..."
sudo rfkill unblock wifi
sudo iw reg set ES

/usr/sbin/ifconfig wlan0 | /usr/bin/grep  10.0.0.201
if [ $? -ne 0 ]; then
   # Ensure wlan0 (Hotspot) has the correct IP
   sudo ip addr add 10.0.0.201/24 dev wlan0 2>/dev/null
   sudo ip link set wlan0 up
fi

# Configure the forwarding via iptables
a=`/usr/bin/cat /proc/sys/net/ipv4/ip_forward`
if [ $a == "1" ]; then
  /usr/bin/echo "already done!!!"
  exit 0
fi


# 3. Routing Cleanup
echo "Cleaning up routing table..."
# Remove the loopback gateway if it exists
sudo ip route del default via 10.0.0.201 dev wlan0 2>/dev/null
# Ensure the Movistar route is priority (replace wlan1 IP if it changes)
# Cleanly extract ONLY the IP address of wlan1
WLAN1_IP=$(ip -4 addr show wlan1 | awk '/inet / {print $2}' | cut -d/ -f1 | head -n 1)

if [ -z "$WLAN1_IP" ]; then
    echo "ERROR: wlan1 has no IP. Is the USB dongle connected to a WIFI???"
    checkstartwifi
    /usr/bin/sleep 60
    WLAN1_IP=$(ip -4 addr show wlan1 | awk '/inet / {print $2}' | cut -d/ -f1 | head -n 1)
    if [ -z "$WLAN1_IP" ]; then
      echo "ERROR: wlan1 has no IP, even after trying /home/pi/wpa_supplicant!"
      exit 1
    fi
fi

echo "Using External IP: $WLAN1_IP"
sudo iptables -t nat -F
sudo iptables -t nat -A POSTROUTING -s 10.0.0.0/24 -o wlan1 -j SNAT --to-source $WLAN1_IP

# 4. Firewall (nftables)
echo "Applying nftables bridge..."
sudo nft add table ip filter 2>/dev/null
sudo nft add chain ip filter FORWARD '{ type filter hook forward priority 0 ; policy accept ; }' 2>/dev/null
sudo nft add rule ip filter FORWARD iifname "wlan0" oifname "wlan1" accept 2>/dev/null
sudo nft add rule ip filter FORWARD iifname "wlan1" oifname "wlan0" ct state established,related accept 2>/dev/null

# 5. Services
echo "Restarting Services..."
sudo systemctl restart isc-dhcp-server
sudo systemctl restart hostapd

# 6. Power Management
sudo iw dev wlan0 set power_save off
sudo iw dev wlan1 set power_save off

echo "Hotspot is active. Client should be at 10.0.0.211"
# 1. Enable forwarding
/usr/bin/echo 1 | sudo tee /proc/sys/net/ipv4/ip_forward

sudo iw dev wlan0 set power_save off
sudo iw dev wlan1 set power_save off
