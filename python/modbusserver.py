import stomp
import json
import time
import uuid
import minimalmodbus

class MyListener(stomp.ConnectionListener):
    def on_error(self, headers, message):
        print('received an error "%s"' % message)
    def on_message(self, headers, message):
        #print('received a message "%s"' % message)
        cmds=str.split(message, '-')
        if cmds[0] == "On":
          instrument.write_register(3,1,1)
        else:
          instrument.write_register(3,0,1)
        if len(cmds) >=2 and cmds[1] == "On":
          instrument.write_register(4,1,1)
        else:
          instrument.write_register(4,0,1)

# connect to Insdustruino (salve 2)
instrument = minimalmodbus.Instrument('/dev/ttyUSB0', 2)
instrument.serial.baudrate = 115200
instrument.serial.stopbits = 2
instrument.serial.timeout  = 5

# connection to ActiveMQ on my laptop. 
conn = stomp.Connection([('10.0.0.201', 61613)])
conn.set_listener('', MyListener())
 
conn.start()
 
conn.connect()

# send Hello and our id.
PIid='MyPI:' + str(uuid.getnode())
mess='Hello'
json_string = '{"deviceID": "' + PIid + '", "text": "' + mess + '"}'
print(json.dumps(json_string))

conn.send(body=json_string, destination='/topic/PITopic')

# receive for our queue (hopefully a welcome or Hello)
conn.subscribe(destination='/queue/' + PIid, id=1, ack='auto')

# read and send temperature every 10 seconds.
while True:
  time.sleep(10)
  temp1 = instrument.read_register(0, 2)
  temp2 = instrument.read_register(1, 2)
  mess='Temperature1: ' + str(temp1) + ' C Temperature2: ' +  str(temp2) + ' C '
  json_string = '{"deviceID": "' + PIid + '", "text": "' + mess + '"}'
  conn.send(body=json_string, destination='/topic/PITopic')
 
conn.disconnect()
