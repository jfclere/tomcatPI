#service firewalld stop

echo 1 > /proc/sys/net/ipv4/ip_forward

#iptables-restore gateway.ok.saintismier
#iptables-restore gateway.ok
ifconfig wlan1 | grep 10.0.0.201
if [ $? -eq 0 ]; then
  # wlan1 is configurated to forward wifi.
  iptables -P FORWARD ACCEPT
  iptables --table nat -A POSTROUTING -o wlan0 -j MASQUERADE

  iptables -A FORWARD -i wlan0 -o wlan1 -m state --state RELATED,ESTABLISHED -j ACCEPT
  iptables -A FORWARD -i wlan1 -o wlan0 -j ACCEPT

else
  iptables -P FORWARD ACCEPT
  iptables --table nat -A POSTROUTING -o wlan1 -j MASQUERADE

  iptables -A FORWARD -i wlan1 -o wlan0 -m state --state RELATED,ESTABLISHED -j ACCEPT
  iptables -A FORWARD -i wlan0 -o wlan1 -j ACCEPT

fi 
#something like: (Configure wlan1 to be a WiFi Client)
#iptables -t nat -A POSTROUTING -o wlan0 -j MASQUERADE
#iptables -A FORWARD -i wlan0 -o wlan1 -m state --state RELATED,ESTABLISHED -j ACCEPT
#iptables -A FORWARD -i wlan1 -o wlan0 -j ACCEPT
