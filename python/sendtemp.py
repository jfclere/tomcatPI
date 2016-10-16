import stomp
from sense_hat import SenseHat

sense = SenseHat()
temp = sense.get_temperature()
print("Temperature: %s C" % temp)

# connection to ActiveMQ on my laptop. 
conn = stomp.Connection([('192.168.1.114', 61613)])
 
conn.start()
 
conn.connect()

mess='Temperature: ' + str(temp) + ' C'

conn.send(body=mess, destination='/queue/SampleQueue')
 
conn.disconnect()
