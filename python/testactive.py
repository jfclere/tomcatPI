#!/usr/bin/python
import socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.settimeout(1)
s.connect(("gmail.com",61613))
text = s.getsockname()[0]
s.close()
print(text)
