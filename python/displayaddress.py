import socket
from sense_hat import SenseHat
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.connect(("gmail.com",80))
text = s.getsockname()[0]
s.close()
sense = SenseHat()
sense.set_rotation(180)
green = (0, 220, 0)
while True:
      sense.show_message(text, text_colour=green)

