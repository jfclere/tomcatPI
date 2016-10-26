import stomp
import json
import time
import uuid
from sense_hat import SenseHat

class MyListener(stomp.ConnectionListener):
    def on_error(self, headers, message):
        print('received an error "%s"' % message)
    def on_message(self, headers, message):
        print('received a message "%s"' % message)
        sense.set_rotation(180)
        red = (110, 0, 0)
        sense.show_message(message, text_colour=red)
        

# connect to the Astro hat
sense = SenseHat()

# connection to ActiveMQ on my laptop. 
conn = stomp.Connection([('192.168.1.114', 61613)])
conn.set_listener('', MyListener())
 
conn.start()
 
conn.connect()

# send Hello and our id.
PIid='MyPI:' + str(uuid.getnode())
mess='Hello'
json_string = '{"deviceID": "' + PIid + '", "text": "' + mess + '"}'
print(json.dumps(json_string))

conn.send(body=json_string, destination='/queue/SampleQueue')

# receive for our queue (hopefully a welcome or Hello)
conn.subscribe(destination='/queue/' + PIid, id=1, ack='auto')

# read and send temperature every 10 seconds.
while True:
  time.sleep(10)
  temp = sense.get_temperature()
  mess='Temperature: ' + str(temp) + ' C'
  json_string = '{"deviceID": "' + PIid + '", "text": "' + mess + '"}'
  conn.send(body=json_string, destination='/queue/SampleQueue')
 
conn.disconnect()
