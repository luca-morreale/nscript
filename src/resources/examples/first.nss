0
15
Environment
ns
-1
Static
Small
10.0
No
trace.out
Yes
trace.nam
Yes
0.023071895424836602
0.020101010101010102
Node
Node0
-1
0.20098039215686272
0.21843434343434376
Node
Node1
-1
0.5049019607843137
0.2954545454545455
DuplexLink
DuplexLink0
-1
1Mb
10ms
1.0
1.0
DropTail
50
Off
Node0
Node1
UDP
UDP0
-1
1000
None
0.2009803921568627
0.17929292929292934
Null
Null0
-1
0.5081699346405228
0.2563131313131314
AttachToNode
AttachToNode0
-1
UDP0
Node0
AttachToNode
AttachToNode1
-1
Null0
Node1
Connect
Connect0
-1
UDP0
Null0
CBR
CBR0
-1
64Kb
.1
500
Off
0.2009803921568627
0.13510101010101008
AttachToApp
AttachToApp0
-1
UDP0
CBR0
Timer
.5
-1
0.14215686274509798
0.08585858585858584
Timer
4.5
-1
0.30882352941176466
0.08712121212121215
ApplicationEvent
ApplicationEvent0
-1
start
.5
CBR0
ApplicationEvent
ApplicationEvent1
-1
stop
4.5
CBR0
