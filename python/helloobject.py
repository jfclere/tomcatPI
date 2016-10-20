import stomp
import json
import time
import uuid

class MyListener(stomp.ConnectionListener):
    def on_error(self, headers, message):
        print('received an error "%s"' % message)
    def on_message(self, headers, message):
        print('received a message "%s"' % message)

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
conn.send(body=json_string, destination='/queue/SampleQueue')
conn.send(body=json_string, destination='/queue/SampleQueue')
conn.send(body=json_string, destination='/queue/SampleQueue')
conn.send(body=json_string, destination='/queue/SampleQueue')
conn.send(body=json_string, destination='/queue/SampleQueue')
conn.send(body=json_string, destination='/queue/SampleQueue')
conn.send(body=json_string, destination='/queue/SampleQueue')
conn.send(body=json_string, destination='/queue/SampleQueue')

# receive for our queue (hopefully a welcome or Hello)
conn.subscribe(destination='/queue/' + PIid, id=1, ack='auto')

time.sleep(20000);
 
conn.disconnect()
