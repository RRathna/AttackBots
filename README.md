# AttackBots
Master and Slave bots to carryout DDoS attacks

Programming assignment for Network Design course, CMPE206 under Dr. Juan Gomez, SJSU

The files CMPE206-ProgrammingProject-part1/part2 hold the requirements. This project is strictly built for the specifications mentioned in these documents.

Running the masterBot
the system takes -p PortNo as commandline arguement 
COMMANDS :

1. 'list' - Lists all the slaves registered with the master
2. 'connect SlaveIP/Name/all TargetIP/Name TargetPortNo No.ofConnections KeepAlive/URLextension'
    Eg. connect Slave1 www.google.com 80 3 url=/#q=   // to send random querries to the system
        connect slave1 www.google.com 80 3 yes  // to enable keepalive
        connect all www.google.com 80 2 no // to make all slaves registered with the master to connect to google.com 2 times each without keepalive option
3. 'disconnect SlaveIP/Name/all TargetIP/Name TargetPortNo/all'

