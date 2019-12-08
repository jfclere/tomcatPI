The configuration is for AP doing the router for https://github.com/jfclere/kubernetes_f30_demo

Using dhcp it gives a fixed addr to the 3 RPI4 used in the demo.

In /etc/sysconfig/network-scripts/ifcfg-PI3 in the 3 RPI4 use:
```
MAC_ADDRESS_RANDOMIZATION=never
```
Otherwise you won't have a fixed address and nmap won't tell RPI4!
