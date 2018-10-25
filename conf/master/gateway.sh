service firewalld stop

echo 1 > /proc/sys/net/ipv4/ip_forward

iptables-restore gateway.ok.saintismier
#iptables-restore gateway.ok

exit 0

iptables -P FORWARD ACCEPT
iptables --table nat -A POSTROUTING -o wlan1 -j MASQUERADE

iptables -A FORWARD -i wlan1 -o wlan0 -m state --state RELATED,ESTABLISHED -j ACCEPT
iptables -A FORWARD -i wlan0 -o wlan1 -j ACCEPT

#something like: (Configure wlan1 to be a WiFi Client)
#iptables -t nat -A POSTROUTING -o wlan0 -j MASQUERADE
#iptables -A FORWARD -i wlan0 -o wlan1 -m state --state RELATED,ESTABLISHED -j ACCEPT
#iptables -A FORWARD -i wlan1 -o wlan0 -j ACCEPT
